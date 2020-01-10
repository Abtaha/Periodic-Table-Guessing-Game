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

package openeye.docexamples.oefastrocs;

import openeye.oechem.*;
import openeye.oefastrocs.*;

public class Tutorial1_InertialAtHeavyAtoms_Snippet {
    public static void main(String argv[]) {
        OEShapeDatabase shapeOnlyDB = new OEShapeDatabase(OEShapeDatabaseType.Shape);
    
        OEShapeDatabaseOptions opts = new OEShapeDatabaseOptions();
        opts.SetLimit(5);
        opts.SetInitialOrientation(OEFastROCSOrientation.InertialAtHeavyAtoms);

        oemolistream qfs = new oemolistream();
        qfs.open(argv[1]);

        OEGraphMol query = new OEGraphMol();
        oechem.OEReadMolecule(qfs, query);
        if(opts.GetInitialOrientation() == OEFastROCSOrientation.InertialAtHeavyAtoms) {
            long numStarts = opts.GetNumHeavyAtomStarts(query);
            System.out.println("This example will use " + numStarts + " starts");
        }

        OEFloatArray coords = new OEFloatArray(3 * query.GetMaxAtomIdx());
        int atomIdx = 1;
        query.GetCoords(coords);
        float [] vals = coords.toArray();
        float [] c = new float[3];
        for(int k = 0; k < 3; k++) {
            c[k] = vals[atomIdx*3 + k];
        }

        OEFloatArray startsCoords = new OEFloatArray(c); 

        opts.SetInitialOrientation(OEFastROCSOrientation.UserInertialStarts);
        opts.SetUserStarts(startsCoords, 1); 

        if(opts.GetInitialOrientation() == OEFastROCSOrientation.UserInertialStarts) {
            long numStarts = opts.GetNumUserStarts();
            System.out.println("This example will use " + numStarts + " starts");
        }

        String dbname = argv[0];
        System.out.println("Opening database file " + dbname + " ...");
        OEShapeDatabase dbase = new OEShapeDatabase();
        OEMolDatabase moldb = new OEMolDatabase();

        moldb.Open(dbname);
        dbase.Open(moldb, OEFastROCSOrientation.AsIs);

        opts.SetInitialOrientation(OEFastROCSOrientation.AsIs);

        if(opts.GetInitialOrientation() == OEFastROCSOrientation.AsIs) {
            long numStarts = opts.GetNumStarts();
            long numInertialStarts = opts.GetNumInertialStarts();
            System.out.println("This example will use " + numStarts + " starts & " + numInertialStarts + " inertial starts");
        }

        opts.SetMaxOverlays(opts.GetNumInertialStarts() * opts.GetNumStarts()); 

        opts.SetLimit(50);
        opts.SetMaxConfs(5); 
    }
}

