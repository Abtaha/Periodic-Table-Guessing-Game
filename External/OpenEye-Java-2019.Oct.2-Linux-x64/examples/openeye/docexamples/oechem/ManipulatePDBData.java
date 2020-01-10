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
//@ <SNIPPET>
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class ManipulatePDBData {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("ManipulatePDBData <pdbfile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0]);
        }
        // need to set input flavor to ensure PDB data is stored on molecule
        ifs.SetFlavor(OEFormat.PDB, OEIFlavor.Generic.Default |
                OEIFlavor.PDB.Default | OEIFlavor.PDB.DATA);
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (oechem.OEHasPDBData(mol,"COMPND")) {
                System.out.println("COMPND:");
                System.out.println(oechem.OEGetPDBData(mol,"COMPND"));
            }
            if (oechem.OEHasPDBData(mol,"HELIX ")) {
                System.out.println("HELIX:");
                System.out.println(oechem.OEGetPDBData(mol,"HELIX "));
            }
            if (oechem.OEHasPDBData(mol,"SSBOND")) {
                System.out.println("SSBOND:");
                for (OESDDataPair dp : oechem.OEGetPDBDataPairs(mol)) {
                    if (dp.GetTag().equals("SSBOND")) {
                        System.out.println(dp.GetValue());
                    }
                }
            }
        }
        ifs.close();
    }
}
//@ </SNIPPET>
