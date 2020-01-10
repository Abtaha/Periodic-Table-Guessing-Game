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
/****************************************************************************
 * Perform reactions on the given compounds
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class UniMolRxn {

    public static void uniMolRxn (oemolistream ifs,
            oemolostream ofs,
            OEUniMolecularRxn umr) {

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (umr.constCall(mol)) {
                oechem.OEWriteMolecule(ofs, mol);
            }
        }
    }

    public static void main(String argv[]) {
        if (argv.length < 2 || argv.length > 3) {
            oechem.OEThrow.Usage("UniMolRxn SMIRKS <infile> [<outfile>]");
        }

        OEQMol qmol = new OEQMol();
        if (!oechem.OEParseSmirks(qmol, argv[0])) {
            oechem.OEThrow.Fatal("Unable to parse SMIRKS: " + argv[0]);
        }

        OEUniMolecularRxn umr = new OEUniMolecularRxn();
        if (!umr.Init(qmol)) {
            oechem.OEThrow.Fatal("Failed to initialize reaction with " + argv[0] + " SMIRKS");
        }
        umr.SetClearCoordinates(true);

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[1])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for reading");
        }

        oemolostream ofs = new oemolostream(".ism");
        if (argv.length == 3) {
            if (!ofs.open(argv[2])) {
                oechem.OEThrow.Fatal("Unable to open " + argv[2] + " for writing");
            }
        }

        uniMolRxn(ifs, ofs, umr);
        ifs.close();
        ofs.close();
    }
}
