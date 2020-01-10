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

import java.io.IOException;

import openeye.oechem.*;
import openeye.oefastrocs.*;

import java.net.URL;
import java.lang.Integer;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class BestShapeOverlayMultiConfQuery {
    public static void main(String argv[]) throws IOException{
        BestShapeOverlayMultiConfQuery app = new BestShapeOverlayMultiConfQuery();
        URL fileURL = app.getClass().getResource("BestShapeOverlayMultiConfQuery.txt");
        OEInterface itf = new OEInterface(fileURL, "BestShapeOverlayMultiConfQuery", argv);
        
        String dbname = itf.GetString("-dbase");
        
        if(!oefastrocs.OEFastROCSIsGPUReady()) {
            oechem.OEThrow.Info("No supported GPU available");
            System.exit(0);
        }

        // set options 
        OEShapeDatabaseOptions opts = new OEShapeDatabaseOptions();
        opts.SetLimit(itf.GetInt("-nHits"));
        oechem.OEThrow.Info("Number of hits set to " + opts.GetLimit());
        if(itf.GetFloat("-cutoff") != -1) {
            if(itf.GetFloat("-cutoff") > 0) {
                opts.SetCutoff(itf.GetFloat("-cutoff"));
                oechem.OEThrow.Info("Cutoff set to " + itf.GetFloat("-cutoff")); 
            }
            else
            oechem.OEThrow.Fatal("Cutoff must be greater than 0");
        }

        if(itf.GetBool("-tversky"))
            opts.SetSimFunc(OEShapeSimFuncType.Tversky);

        // read in database
        oemolistream ifs = new oemolistream();
        if(!ifs.open(dbname))
            oechem.OEThrow.Fatal("Unable to open " + dbname);

        oechem.OEThrow.Info("Opening database file " + dbname + " ...");
        OEWallTimer timer = new OEWallTimer();
        OEShapeDatabase dbase = new OEShapeDatabase();
        OEMolDatabase moldb = new OEMolDatabase();
        if(!moldb.Open(ifs))
            oechem.OEThrow.Fatal("Unable to open " + dbname);

        OEThreadedDots dots = new OEThreadedDots(10000, 200, "conformers");
        if(!dbase.Open(moldb, dots))
            oechem.OEThrow.Fatal("Unable to initialize OEShapeDatabase on '" + dbname + "'");

        dots.Total();
        oechem.OEThrow.Info(timer.Elapsed() + " seconds to load database");
        ArrayList<String> queryFiles = new ArrayList<String>();
        for(String file : itf.GetStringList("-query"))
            queryFiles.add(file); 
        for(int i = 0; i < queryFiles.size(); i++) {
            String qfname = queryFiles.get(i);
            // read in query
            oemolistream qfs = new oemolistream();
            if(!qfs.open(qfname))
                oechem.OEThrow.Fatal("Unable to open '" + qfname + "'");

            OEMol mcmol = new OEMol();
            if(!oechem.OEReadMolecule(qfs, mcmol))
                oechem.OEThrow.Fatal("Unable to read query from '" + qfname  + "'");
            qfs.rewind();
    
            String ext = oechem.OEGetFileExtension(qfname);
            int qmolidx = 0;
            while(oechem.OEReadMolecule(qfs, mcmol)) {
                // write out to file name based on molecule title
                oemolostream ofs = new oemolostream();
                String moltitle = mcmol.GetTitle();
                if(moltitle.length() == 0)
                    moltitle = Integer.toString(qmolidx);

                String ofname = moltitle + "_results." + ext;
                if(!ofs.open(ofname))
                    oechem.OEThrow.Fatal("Unable to open '" + qfname + "'");

                oechem.OEThrow.Info("Searching for " + moltitle
                                   + " of " + qfname + " ("
                                   + mcmol.NumConfs() + " conformers)");
                
                int qconfidx = 0;
                OEConfBaseIter c = mcmol.GetConfs();
                DecimalFormat df = new DecimalFormat("#.####");
                while(c.hasNext()) {
                    OEConfBase conf = c.next();
                    OEShapeDatabaseScoreIter s = dbase.GetSortedScores(conf, opts);
                    while(s.hasNext()) {
                        OEShapeDatabaseScore score = s.next();
                        OEMol dbmol = new OEMol();
                        float dbmolidx = score.GetMolIdx();
                        if(!moldb.GetMolecule(dbmol, (int)dbmolidx)) {
                            oechem.OEThrow.Warning("Unable to retrieve molecule '"
                                               + dbmolidx + "' from the database");
                            continue;
                        }

                        OEGraphMol mol = new OEGraphMol(dbmol.GetConf(new OEHasConfIdx(score.GetConfIdx())));
                        oechem.OESetSDData(mol, "QueryConfIdx", Integer.toString(qconfidx)); 
                        oechem.OESetSDData(mol, "ShapeTanimoto", df.format(score.GetShapeTanimoto()));
                        oechem.OESetSDData(mol, "ColorTanimoto", df.format(score.GetColorTanimoto()));
                        oechem.OESetSDData(mol, "TanimotoCombo", df.format(score.GetTanimotoCombo()));
                        score.Transform(mol);

                        oechem.OEWriteMolecule(ofs, mol);
                    }

                    qconfidx++;
                }            

                oechem.OEThrow.Info(qconfidx + " conformers processed");
                oechem.OEThrow.Info("Wrote results to " + ofname);
                ofs.close();
            } 

            qmolidx++;
        }
    
        System.exit(0);
    }   
}
