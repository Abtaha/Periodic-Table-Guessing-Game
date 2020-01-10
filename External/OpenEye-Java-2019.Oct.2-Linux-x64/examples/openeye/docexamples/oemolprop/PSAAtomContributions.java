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
package openeye.docexamples.oemolprop;

import openeye.oechem.*;
import openeye.oemolprop.*;

public class PSAAtomContributions {
    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("PSAAtomContributions <molfile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");

        OEGraphMol mol = new OEGraphMol();    
        if (!oechem.OEReadMolecule(ifs, mol))
            oechem.OEThrow.Fatal("Unable to read molecule from " + argv[0]);
        ifs.close();

        OEFloatArray atomPSA = new OEFloatArray(mol.GetMaxAtomIdx());
        float psa = oemolprop.OEGet2dPSA(mol, atomPSA);

        System.out.println("PSA = " + psa);
        for (OEAtomBase atom : mol.GetAtoms()) {
            int idx = atom.GetIdx();
            System.out.println(idx + " " + atomPSA.getItem(idx));          
        }
    }
}
