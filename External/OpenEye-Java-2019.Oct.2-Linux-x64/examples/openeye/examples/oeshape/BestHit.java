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

public class BestHit {

    public static void main(String[] args) {
        if (args.length!=3) {
            System.out.println("BestHit <reffile> <fitfile> <outfile>");
            System.exit(0);
        }

        oemolistream reffs = new oemolistream(args[0]);
        oemolistream fitfs = new oemolistream(args[1]);
        oemolostream outfs = new oemolostream(args[2]);

        OEMol refmol = new OEMol();
        oechem.OEReadMolecule(reffs, refmol);
        reffs.close();

        OEMol fitmol = new OEMol();
        oechem.OEReadMolecule(fitfs, fitmol);
        fitfs.close();

        OEROCSResult res = new OEROCSResult();
        oeshape.OEROCSOverlay(res, refmol, fitmol);
        OEMolBase outmol = res.GetOverlayConf();

        oechem.OEWriteMolecule(outfs, outmol);
        System.out.println("Tanimoto combo = "+res.GetTanimotoCombo());
    }
}

