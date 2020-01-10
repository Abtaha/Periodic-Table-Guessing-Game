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

public class TopHitsQuery {

    public static void main(String[] args) {
        if (args.length!=4) {
            System.out.println("TopHitsQuery <queryfile> <fitfile> <outfile> <nhits>");
            System.exit(0);
        }

        String ext = oechem.OEGetFileExtension(args[0]);
        if (!ext.equals("sq"))
            oechem.OEThrow.Fatal("Requires a shape query .sq input file format");

        oemolistream fitfs = new oemolistream(args[1]);
        oemolostream outfs = new oemolostream(args[2]);
        int nhits = Integer.parseInt(args[3]);

        OEShapeQuery query = new OEShapeQuery();
        oeshape.OEReadShapeQuery(args[0], query);

        // Setup OEROCS with specified number of best hits
        OEROCSOptions options = new OEROCSOptions();
        options.SetNumBestHits(nhits);
        OEROCS rocs = new OEROCS(options);
        rocs.SetDatabase(fitfs);

        for (OEROCSResult res : rocs.Overlay(query))
        {
            OEGraphMol outmol = new OEGraphMol(res.GetOverlayConf());
            oechem.OEWriteMolecule(outfs, outmol);
            System.out.println(" title = "+outmol.GetTitle());
            System.out.println(" tanimoto combo = "+res.GetTanimotoCombo());
        }
    }
}

