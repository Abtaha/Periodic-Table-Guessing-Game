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

public class GenericDataToAtom {
    private static class IsDonorAtomPred extends OEUnaryAtomPred {
        public boolean constCall(OEAtomBase atom) {
            return atom.GetBoolData("isdonor");
        }
        public OEUnaryAtomBoolFunc CreateCopy() {
            OEUnaryAtomBoolFunc copy = new IsDonorAtomPred();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1c(Cl)cncc1C(=O)O");

        OEMatchAtom IsDonorAtom = new OEMatchAtom("[!H0;#7,#8]");
        for (OEAtomBase atom : mol.GetAtoms()) {
            atom.SetBoolData("isdonor", IsDonorAtom.constCall(atom));
        }

        System.out.print("Donor atoms: ");
        for (OEAtomBase atom : mol.GetAtoms(new IsDonorAtomPred())) {
            System.out.print(atom.GetIdx() + " " +  oechem.OEGetAtomicSymbol(atom.GetAtomicNum()));
        }
        System.out.println("");
    }
}
//@ </SNIPPET>
