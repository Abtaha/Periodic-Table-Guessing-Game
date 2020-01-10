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
import java.util.ArrayList;
import java.lang.Float;
import java.io.File;
import java.io.PrintWriter;

import openeye.oechem.*;
import openeye.oefastrocs.*;

public class ShapeDistanceMatrix {
    public static void main(String argv[]) throws IOException{
        ShapeDistanceMatrix app = new ShapeDistanceMatrix();
        URL fileURL = app.getClass().getResource("ShapeDistanceMatrix.txt");
        OEInterface itf = new OEInterface(fileURL, "ShapeDistanceMatrix", argv);
        
        oemolistream ifs = new oemolistream();
        if(!ifs.open(itf.GetString("-dbase"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-dbase") + " for reading");
            System.exit(0);
        }

        String colname = "TanimotoCombo";
        String getter = "GetTanimotoCombo";
        int dbtype = OEShapeDatabaseType.Default;
        if(itf.GetBool("-shapeOnly")) {
            colname = "ShapeTanimoto";
            getter = "GetShapeTanimoto";
            dbtype = OEShapeDatabaseType.Shape;
        }
        
        OEShapeDatabase shapedb = new OEShapeDatabase(dbtype);
        OEShapeDatabaseOptions options = new OEShapeDatabaseOptions();
        options.SetScoreType(dbtype);
       
        // add an empty vector because the first molecule is not computed 
        ArrayList<ArrayList<Float>> lmat = new ArrayList<ArrayList<Float>>();
        ArrayList<Float> tempArray = new ArrayList<Float>();
        lmat.add(tempArray); 
        ArrayList<String> titles = new ArrayList<String>();
        OEMol mol = new OEMol();
        while(oechem.OEReadMolecule(ifs, mol)) {
            if(titles.size() > 0) {
                ArrayList<Float> bestscores = new ArrayList<Float>(titles.size());
                for(int i = 0; i < titles.size(); i++)
                    bestscores.add(0f);
                
                OEConfBaseIter c = mol.GetConfs();
                while(c.hasNext()) {
                    OEConfBase conf = c.next();
                    OEShapeDatabaseScoreIter s = shapedb.GetScores(conf, options);
                    while(s.hasNext()) {
                        OEShapeDatabaseScore score = s.next();
                        int midx = (int)score.GetMolIdx();
                        float sc = 0;
                        if(getter == "GetTanimotoCombo")
                            sc = score.GetTanimotoCombo();
                        else
                            sc = score.GetShapeTanimoto();
                        float temp = Math.max(bestscores.get(midx), sc);
                        bestscores.set(midx, temp);
                    }
                }

                lmat.add(bestscores);
            }

            shapedb.AddMol(mol);

            String title = "";
            if(mol.GetTitle() == null)
                title = Integer.toString(titles.size() + 1);
            else
                title = mol.GetTitle();
            titles.add(title); 
        }        

        // write csv file
        PrintWriter csvfile = new PrintWriter(new File(itf.GetString("-matrix")));
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < titles.size(); i++)
            sb.append(titles.get(i) + ',');
        sb.append('\n');
    
        int nrows = titles.size();
        for(int i = 0; i < nrows; i++) {
            sb.append(i + 1);
            sb.append(',');
            for(int j = 0; j < nrows; j++) {
                double val = 2.0;
                if(itf.GetBool("-shapeOnly"))
                    val = 1.0;

                if(j > i)
                    val -= lmat.get(j).get(i);
                else if(j < i)
                    val -= lmat.get(i).get(j);
                else if(j == i)
                    val = 0.0;
                
                sb.append(val);
                sb.append(',');
            }
        
            sb.append('\n');
        }

        csvfile.write(sb.toString());
        csvfile.close();       
        System.exit(0);
    }
}
