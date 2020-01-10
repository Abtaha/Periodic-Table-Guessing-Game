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
package openeye.docexamples.oeszybki;

import openeye.oechem.*;
import openeye.oeszybki.*;

public class TorsionScan {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("TorsionScan <infile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }

        OEMol mol = new OEMol();
        oechem.OEReadMolecule(ifs, mol);
        OEMol outmol = new OEMol();

        OETorsionScanOptions opts = new OETorsionScanOptions();
        opts.SetDelta(30.0);
        opts.SetForceFieldType(OEForceFieldType.MMFF94);
        opts.SetSolvationType(OESolventModel.NoSolv);

        for (OETorsion tor : oechem.OEGetTorsions(mol)) {
            System.out.println(String.format("Torsion: %d %d %d %d",
                                                       tor.getA().GetIdx(), tor.getB().GetIdx(),
                                                       tor.getC().GetIdx(), tor.getD().GetIdx()));

            for(OETorsionScanResult res : oeszybkilib.OETorsionScan(outmol, mol, tor, opts)) {
                System.out.println(String.format("%.2f %.2f", res.GetAngle(), res.GetEnergy()));
            }

        }
    }
}
