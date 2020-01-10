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
 * Output some basic molecule properties
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class MolStats {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("MolStats <infile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }

        System.out.println("Title MolWt NumAtoms NumHeavyAtoms NumRingAtoms NumRotors NumConfs");
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            String title = mol.GetTitle();
            if (title == null || title.equals("")) {
                title = "Untitled";
            }
            System.out.printf("%s %.3f %d %d %d %d %d\n",
                    title,
                    oechem.OECalculateMolecularWeight(mol),
                    mol.NumAtoms(),
                    oechem.OECount(mol, new OEIsHeavy()),
                    oechem.OECount(mol, new OEAtomIsInRing()),
                    oechem.OECount(mol, new OEIsRotor()),
                    mol.NumConfs());
        }
        ifs.close();
    }
}
