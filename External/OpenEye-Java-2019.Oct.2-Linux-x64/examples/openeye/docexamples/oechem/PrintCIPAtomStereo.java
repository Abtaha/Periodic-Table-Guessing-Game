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

public class PrintCIPAtomStereo {

    public static void main(String argv[]) {

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "N[C@@H]1N[C@@H](O)CC(O)C1.C2[C@@H](O)CC[C@@H](O)C2");

        for (OEAtomBase atom : mol.GetAtoms()) {

            int cip = oechem.OEPerceiveCIPStereo(mol,atom);

            if (atom.HasStereoSpecified()) {
                System.out.print("atom " + atom.GetIdx() + " is ");

                if (cip == OECIPAtomStereo.S) {
                    System.out.println("S");
                } else if (cip == OECIPAtomStereo.R) {
                    System.out.println("R");
                } else if (cip == OECIPAtomStereo.NotStereo) {
                    System.out.println("not a CIP stereo center");
                }

            } 
            if (cip == OECIPAtomStereo.UnspecStereo) {
                System.out.print("atom " + atom.GetIdx() + " is ");
                System.out.println("a CIP stereo center without specified stereochemistry");
            }
        }
    }
}
//@ </SNIPPET>
