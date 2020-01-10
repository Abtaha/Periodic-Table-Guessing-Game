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

public class SetBondStereo {

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "C\\C=C\\C=C/C=CC");

        for (OEBondBase bond : mol.GetBonds()) {
            if (bond.GetOrder() == 2 && !bond.HasStereoSpecified(OEBondStereo.CisTrans)) {
                OEAtomBaseVector v = new OEAtomBaseVector();
                for (OEAtomBase neigh : bond.GetBgn().GetAtoms()) {
                    if (neigh.GetIdx() != bond.GetEnd().GetIdx()) {
                        v.add(neigh);
                        break;
                    }
                }
                for (OEAtomBase neigh : bond.GetEnd().GetAtoms()) {
                    if (neigh.GetIdx() != bond.GetBgn().GetIdx()) {
                        v.add(neigh);
                        break;
                    }
                }
                bond.SetStereo(v,OEBondStereo.CisTrans,OEBondStereo.Trans);
            }
        }
        System.out.println(oechem.OEMolToSmiles(mol));
    }
}
//@ </SNIPPET>
