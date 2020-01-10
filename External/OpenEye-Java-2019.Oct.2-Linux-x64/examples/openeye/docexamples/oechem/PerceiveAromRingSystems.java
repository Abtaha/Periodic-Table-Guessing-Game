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

public class PerceiveAromRingSystems {

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "C(O)(=O)c1cccc2c1[nH]c(C3CCCc4c3cccc4)c2");

        int[] parts = new int[mol.GetMaxAtomIdx()];

        int nraromsystems = oechem.OEDetermineAromaticRingSystems(mol, parts);

        System.out.print("Aliphatic atoms: ");
        for (OEAtomBase atom : mol.GetAtoms()) {
            if (parts[atom.GetIdx()] == 0) {
                System.out.print(atom.GetIdx() + " ");
            }
        }
        System.out.println();
        System.out.println("Number of aromatic ring systems = " + nraromsystems);
        for (int ringidx = 1; ringidx <= nraromsystems; ++ringidx) {
            System.out.print(ringidx + " . aromatic ring system: ");
            for (OEAtomBase atom : mol.GetAtoms()) {
                if (parts[atom.GetIdx()] == ringidx) {
                    System.out.print(atom.GetIdx() + " ");
                }
            }
            System.out.println();
        }
    }
}
//@ </SNIPPET>
