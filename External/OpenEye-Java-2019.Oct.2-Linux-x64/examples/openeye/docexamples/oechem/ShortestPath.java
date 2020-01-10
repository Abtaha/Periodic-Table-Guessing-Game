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

public class ShortestPath {

    public static void main(String argv[]) {

        {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1ccc2c(c1)cccn2");

        OEAtomBase atomA = mol.GetAtom(new OEHasAtomIdx(2));
        OEAtomBase atomB = mol.GetAtom(new OEHasAtomIdx(8));

        System.out.print("shortest path = ");
        for (OEAtomBase atom : oechem.OEShortestPath(atomA, atomB)) {
            System.out.print(atom.GetIdx() + " " + oechem.OEGetAtomicSymbol(atom.GetAtomicNum()) + " ");
        }
        System.out.println();
        }

        {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1ccc2c(c1)cccn2");

        OEAtomBase atomA = mol.GetAtom(new OEHasAtomIdx(2));
        OEAtomBase atomB = mol.GetAtom(new OEHasAtomIdx(8));

        System.out.print("shortest path = ");
        for (OEAtomBase atom : oechem.OEShortestPath(atomA, atomB, new OEIsNitrogen())) {
            System.out.print(atom.GetIdx() + " " + oechem.OEGetAtomicSymbol(atom.GetAtomicNum()) + " ");
        }
        System.out.println();
        }
    }
}
//@ </SNIPPET>
