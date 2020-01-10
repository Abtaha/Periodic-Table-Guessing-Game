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
package openeye.docexamples.oebio;

import java.io.*;
import openeye.oechem.*;
import openeye.oebio.*;

public class OEGetCrystalSymmetry {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("OEGetCrystalSymmetry <pdb>");
        }
        oemolistream ifs = new oemolistream();
        if(!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        OEGraphMol mol = new OEGraphMol();
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        OECrystalSymmetryParams p = new OECrystalSymmetryParams();
        if (oebio.OEGetCrystalSymmetry(p, mol)) {
            System.out.print("a=" + p.GetA() + " b=" + p.GetB() + " c=" + p.GetC());
            System.out.print(" alpha=" + p.GetAlpha() + " beta=" + p.GetBeta() + " gamma=" + p.GetGamma());
            System.out.print(" spacegroup=" + p.GetSpaceGroup() + " z-value= " + p.GetZValue());
            System.out.println();
        }
    }
}
