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
package openeye.examples.oebio;

import openeye.oechem.*;
import openeye.oebio.*;

public class CisCheck {

    private void displayUsage() {
        System.err.println("usage: CisCheck <mol>");
        System.exit(1);
    }
    private void check(String filename) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(filename)) {
            System.err.println("Unable to open input file: " + filename);
            return;
        }

        int cisCt=0;
        int molCt=0;
        double omega=0.0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            ++molCt;
            System.out.println("==========================================");
            System.out.println("Molecule: "+molCt+"   "+mol.GetTitle());
            if (! oechem.OEHasResidues(mol))
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

            OEHierView hv = new OEHierView(mol);
            for (OEHierResidue res : hv.GetResidues()) {
                if (!oebio.OEIsStandardProteinResidue(res))
                    continue;
                omega = oebio.OEGetTorsion(res, OEProtTorType.Omega);
                if (omega != -100.0) { // invalid torsion 
                    if (omega < 0.5 && omega > -0.5) {
                        ++cisCt;
                        System.out.print("  "+res.GetOEResidue().GetName());
                        System.out.print("  "+res.GetOEResidue().GetChainID());
                        System.out.print("  "+res.GetOEResidue().GetResidueNumber());
                        System.out.println(" omega = "+omega);
                    }
                }
            }
            System.out.println(cisCt+" cis amide bond(s) identified.\n");
        }
        ifs.close();
    }

    public static void main(String argv[]) {
        CisCheck app = new CisCheck();
        if (argv.length != 1)
            app.displayUsage();
        app.check(argv[0]);
    }
}
