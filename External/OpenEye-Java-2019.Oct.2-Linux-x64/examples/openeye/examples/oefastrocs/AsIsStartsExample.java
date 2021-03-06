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

import openeye.oechem.*;
import openeye.oefastrocs.*;

import java.lang.StringBuilder;

public class AsIsStartsExample {
    public static void main(String argv[]) {
        if(argv.length < 2) {
            oechem.OEThrow.Usage("openeye.examples.oefastrocs.AsIsStartsExample <database> [<queries> ... ]");
            System.exit(0);
        }

        // check system
        if(!oefastrocs.OEFastROCSIsGPUReady()) {
            oechem.OEThrow.Info("No supported GPU available!");
            System.exit(0);
        }

        // read in database
        String dbname = argv[0];
        oechem.OEThrow.Info("Opening database file " + dbname + " ...");
        OEShapeDatabase dbase = new OEShapeDatabase();
        OEMolDatabase moldb = new OEMolDatabase();

        if(!moldb.Open(dbname))
            oechem.OEThrow.Fatal("Unable to open '" + dbname + "'");

        OEThreadedDots dots = new OEThreadedDots(10000, 200, "conformers");
        if(!dbase.Open(moldb, dots, OEFastROCSOrientation.AsIs))
            oechem.OEThrow.Fatal("Unable to initialize OEShapeDatabase on '" + dbname + "'");

        // customize search options
        OEShapeDatabaseOptions opts = new OEShapeDatabaseOptions();
        opts.SetInitialOrientation(OEFastROCSOrientation.AsIs);
    
        opts.SetLimit(50);
        opts.SetMaxConfs(5);
        opts.SetMaxOverlays(opts.GetNumInertialStarts() * opts.GetNumStarts());
    
        if(opts.GetInitialOrientation() == OEFastROCSOrientation.AsIs) {
            long numStarts = opts.GetNumStarts();
            long numInertialStarts = opts.GetNumInertialStarts();
            oechem.OEThrow.Info("This example will use " + numStarts
                                + " starts & " + numInertialStarts
                                + " inertial starts");
        }
        
        for(int i = 1; i < argv.length; i++) {
            // read in query
            String qfname = argv[i];
            oemolistream qfs = new oemolistream();
            if(!qfs.open(qfname)) {
                oechem.OEThrow.Fatal("Unable to open '" + qfname + "'");
            }

            OEGraphMol query = new OEGraphMol();
            if(!oechem.OEReadMolecule(qfs, query))
                oechem.OEThrow.Fatal("Unable to read query from '" + qfname + "'");

            String ext = oechem.OEGetFileExtension(qfname);
            StringBuilder  base = new StringBuilder(qfname.substring(0, qfname.length() - ext.length() - 1));
            
            // write out everything to a similarly named file
            oemolostream ofs = new oemolostream();
            String ofname = base + "_asis_results." + ext;
            if(!ofs.open(ofname))
                oechem.OEThrow.Fatal("Unable to open '" + argv[4] + "'");
        
            oechem.OEWriteMolecule(ofs, query);
            oechem.OEThrow.Info("Searching for" + qfname);
            OEShapeDatabaseScoreIter s = dbase.GetSortedScores(query, opts);
            while(s.hasNext()) {
                OEShapeDatabaseScore score = s.next();
                oechem.OEThrow.Info("Score for mol " + score.GetMolIdx() + 
                                    "(conf " + score.GetConfIdx() + ") " +
                                     score.GetShapeTanimoto() + " shape " + 
                                     score.GetColorTanimoto() + " color"); 
                OEMol dbmol = new OEMol();
                long molidx = score.GetMolIdx();
                if(!moldb.GetMolecule(dbmol, (int)molidx)) {
                    oechem.OEThrow.Warning("Unable to retrieve molecule '" + molidx + "' from the database");
                    continue;
                }

                OEGraphMol mol = new OEGraphMol(dbmol.GetConf(new OEHasConfIdx(score.GetConfIdx())));
                oechem.OESetSDData(mol, "ShapeTanimoto", String.valueOf(score.GetShapeTanimoto()));
                oechem.OESetSDData(mol, "ColorTanimoto", String.valueOf(score.GetColorTanimoto()));
                oechem.OESetSDData(mol, "TanimotoCombo", String.valueOf(score.GetTanimotoCombo()));
                score.Transform(mol);

                oechem.OEWriteMolecule(ofs, mol);
            }
        
            oechem.OEThrow.Info("Wrote results to " + ofname);
            ofs.close();
        }  
 
        System.exit(0);
    }
}
