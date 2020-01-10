/*
(C) 2017 OpenEye Scientific Software Inc. All rights reserved.

TERMS FOR USE OF SAMPLE CODE The software below ("Sample Code") is
provided to current licensees or subscribers of OpenEye products or
SaaS offerings (each a "Customer").
Customer is hereby permitted to use, copy, and modify the Sample Code,
subject to these terms. OpenEye claims no rights to Customer's
modifications. Modification of Sample Code is at Customer's sole and
exclusive risk. Sample Code may require Customer to have a then
current license or subscription to the applicable OpenEye offering.
THE SAMPLE CODE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED.  OPENEYE DISCLAIMS ALL WARRANTIES, INCLUDING, BUT
NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. In no event shall OpenEye be
liable for any damages or liability in connection with the Sample Code
or its use.
*/
package openeye.examples.oefastrocs;

import java.net.URL;
import java.io.IOException;

import openeye.oechem.*;
import openeye.oefastrocs.*;
import openeye.oeshape.*;

import java.util.Collections;
import java.util.ArrayList;
import java.lang.Float;
import java.lang.Math;

public class SphereExclusionClustering {
 
    private static class Volume implements Comparable<Volume> {
        int index;
        float vol;

        @Override
        public int compareTo(Volume vol) {
            if(this.vol == vol.vol) {
                if(this.index == vol.index)
                    return 0;
                else if(this.index > vol.index)
                    return 1;
                return -1;
            }
            else if(this.vol < vol.vol) {
                return -1;
            }
            else 
                return 1;
        }
    }

    private static class Pair {
        OEGraphMol mol;
        boolean inCluster;
        int index;
    }

    private static String GetScoreGetter(boolean shapeOnly) {
        if(shapeOnly)
            return "GetShapeTanimoto";
        return "GetTanimotoCombo";
    }
    
    private static class ShapeCluster {
        private float cutoff;
        private OEShapeDatabaseOptions defaultOptions;
        private OEShapeDatabase shapedb;
        private ArrayList<Pair> dbmols;
        private ArrayList<Volume> volumes;
        private int nextClusterHeadIdx;
        private int remainingMolecules;
        private ArrayList<Float> tanimotos;
        private String scoreGetter;

        ShapeCluster(String dbname, float cutoff, boolean shapeOnly) {
            this.cutoff = cutoff;
            
            // set up and options and database based upon shapeOnly
            this.defaultOptions = new OEShapeDatabaseOptions();
            int dbtype = OEShapeDatabaseType.Default;
            if(shapeOnly)
                dbtype = OEShapeDatabaseType.Shape;

            this.defaultOptions.SetScoreType(dbtype);
            this.shapedb = new OEShapeDatabase(dbtype);
            this.dbmols = new ArrayList<Pair>();
            this.volumes = new ArrayList<Volume>();            

            // read in database
            oemolistream ifs = new oemolistream();
            if(!ifs.open(dbname))
                oechem.OEThrow.Fatal("Unable to open " + dbname);

            int count = 0;
            OEGraphMol mol = new OEGraphMol();
            while(oechem.OEReadMolecule(ifs, mol)) {
                String title = mol.GetTitle();
                if(title == null) {
                    title = "Untitled " + count;
                    mol.SetTitle(title);
                    count++;
                }

                int idx = this.shapedb.AddMol(new OEMol(mol));

                float volume = oeshape.OEGetCachedSelfShape(mol);
                if(volume == 0.0)
                   volume = oeshape.OESelfShape(mol);
                Volume v = new Volume();
                v.index = idx;
                v.vol = volume;
                volumes.add(v);

                OEGraphMol dbmol = new OEGraphMol(mol, OEMolBaseType.OEDBMol);
                dbmol.Compress();
                Pair p = new Pair();
                p.mol = dbmol;
                p.inCluster = false;
                p.index = idx;
                this.dbmols.add(p);
            }

            int numMols = volumes.size();
            // find the molecule with the median volume as our first query
            Collections.sort(volumes);
            int medianIdx = volumes.get(numMols / 2).index;

            this.nextClusterHeadIdx = medianIdx;
            this.remainingMolecules = numMols;  
            
            this.tanimotos = new ArrayList<Float>(numMols);
            for(int i = 0; i < numMols; i++) {
                float f = 0; 
                this.tanimotos.add(f);
            }
            
            this.scoreGetter = GetScoreGetter(shapeOnly);
        }

        private boolean HasRemainingMolecules() {
            return this.remainingMolecules != 0;
        }

        private Pair removeMolecule(int idx) {
            this.remainingMolecules--;
            
            assert this.dbmols.get(idx).inCluster == false;
            Pair p = this.dbmols.get(idx);
            p.mol.UnCompress();
            p.inCluster = true;
            this.dbmols.set(idx, p);

            this.tanimotos.set(idx, Float.MAX_VALUE);

            return p;
        }

        private Pair GetNextClusterHead() {
            assert this.nextClusterHeadIdx >= 0;
            return this.removeMolecule(this.nextClusterHeadIdx);
        }

        private void GetCluster(OEGraphMol query, String sdtag) {
            OEShapeDatabaseOptions options = new OEShapeDatabaseOptions(this.defaultOptions);

            OEDots dots = new OEDots(10000, 200, "molecules searched");

            float minTani = Float.MAX_VALUE;
            int minIdx = 0;
            OEShapeDatabaseScoreIter s = this.shapedb.GetScores(query, options);
            String clusterData = "";
            OEShapeDatabaseScore score;
            while(s.hasNext()) {
                score = s.next();
                int idx = (int)score.GetMolIdx();
                // check if already in a cluster
                if(this.dbmols.get(idx).inCluster == true) 
                    continue;
                float num = 0;
                if(this.scoreGetter == "GetShapeTanimoto")
                    num = score.GetShapeTanimoto();
                else
                    num = score.GetTanimotoCombo();
                if(this.cutoff < num) {
                    Pair p = this.removeMolecule(idx);
                    OEGraphMol nbrMol = p.mol;
                    oechem.OESetSDData(nbrMol, sdtag, String.valueOf(num));
                    score.Transform(nbrMol);
                    clusterData += String.valueOf(p.index);
                    clusterData += " "; 
                }
                else {
                    float temp = Math.max(this.tanimotos.get(idx), num);
                    this.tanimotos.set(idx, temp);
                    if (this.tanimotos.get(idx) < minTani) {
                        minTani = this.tanimotos.get(idx);
                        minIdx = idx;
                    }
                }

                dots.Update();
            }

            dots.Total();
            this.nextClusterHeadIdx = minIdx;
            query.SetStringData(query.GetTitle(), clusterData);
        }
    }

    public static void main(String argv[]) throws IOException{
        SphereExclusionClustering app = new SphereExclusionClustering();
        URL fileURL = app.getClass().getResource("SphereExclusionClustering.txt");
        OEInterface itf = new OEInterface(fileURL, "SphereExclusionClustering", argv); 
        float cutoff = itf.GetFloat("-cutoff");

        oemolostream ofs = new oemolostream();
        if(!ofs.open(itf.GetString("-clusters")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-clusters"));
    
        String sdtag = "TanimotoComboFromHead";
        if(itf.GetBool("-shapeOnly"))
            sdtag = "ShapeTanimotoFromHead";

        String getter = GetScoreGetter(itf.GetBool("-shapeOnly"));

        ShapeCluster cluster = new ShapeCluster(itf.GetString("-dbase"), cutoff, itf.GetBool("-shapeOnly"));

        // do the clustering
        while(cluster.HasRemainingMolecules()) {
            OEGraphMol clusterHead = cluster.GetNextClusterHead().mol;
            oechem.OEThrow.Info("Searching for neighbors of " + clusterHead.GetTitle());
            cluster.GetCluster(clusterHead, sdtag);
            oechem.OEWriteMolecule(ofs, clusterHead);
        }

        ofs.close();
        System.exit(0);
    }
}
