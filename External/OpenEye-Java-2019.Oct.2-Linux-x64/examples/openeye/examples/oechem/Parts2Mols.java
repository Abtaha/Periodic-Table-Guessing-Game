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
 * Writes each component of a molecule as a separate molecule
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class Parts2Mols {

    public static void main(String argv[]) {
        if (argv.length < 1 || argv.length > 2) {
            oechem.OEThrow.Usage("Parts2Mols <infile> [<outfile>]");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }

        oemolostream ofs = new oemolostream(".ism");
        if (argv.length == 2) {
            if (!ofs.open(argv[1])) {
                oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");
            }
        }

        int incount = 0;
        int outcount = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            incount++;
            int[] parts = new int[mol.GetMaxAtomIdx()];
            int pcount = oechem.OEDetermineComponents(mol, parts);
            OEPartPredAtom pred = new OEPartPredAtom(parts);
            for (int i = 1; i <= pcount; ++i) {
                outcount++;
                pred.SelectPart(i);
                OEGraphMol partmol = new OEGraphMol();
                oechem.OESubsetMol(partmol, mol, pred, true);
                oechem.OEWriteMolecule(ofs, partmol);
            }
        }
        ifs.close();
        ofs.close();
        System.err.println("results:  in: " + incount + "  out: " + outcount);
    }
}
