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
package openeye.examples.oeshape;

import openeye.oechem.*;
import openeye.oeshape.*;

public class OverlapQuery {

    public static void main(String[] args) {
        if (args.length!=2) {
            System.out.println("OverlapQuery <queryfile> <fitfile>");
            System.exit(0);
        }

        String ext = oechem.OEGetFileExtension(args[0]);
        if (!ext.equals("sq"))
            oechem.OEThrow.Fatal("Requires a shape query .sq input file format");

        OEShapeQuery query = new OEShapeQuery();
        oeshape.OEReadShapeQuery(args[0], query);

        // Get appropriate function to calculate both shape and color
        // By default the OEOverlapFunc contains OEGridShapeFunc for shape
        // and OEExactColorFunc for color
        OEOverlapFunc func = new OEOverlapFunc();
        func.SetupRef(query);

        OEOverlapResults res = new OEOverlapResults();
        oemolistream fitfs = new oemolistream(args[1]);
        OEOverlapPrep prep = new OEOverlapPrep();
        OEGraphMol fitmol = new OEGraphMol();
        while (oechem.OEReadMolecule(fitfs, fitmol)) {
            prep.Prep(fitmol);
            func.Overlap(fitmol, res);
            System.out.print(fitmol.GetTitle());
            System.out.println(" tanimoto combo = "+res.GetTanimotoCombo());
            System.out.println(" shape tanimoto = "+res.GetTanimoto());
            System.out.println(" color tanimoto = "+res.GetColorTanimoto());
        }
    }
}

