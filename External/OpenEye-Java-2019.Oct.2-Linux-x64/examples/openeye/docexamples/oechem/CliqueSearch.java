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

public class CliqueSearch {
    public static void main(String argv[]) {
        OEGraphMol pattern = new OEGraphMol();
        OEGraphMol target  = new OEGraphMol();
        oechem.OESmilesToMol(pattern,"c1cc(O)c(O)cc1CCN");
        oechem.OESmilesToMol(target, "c1c(O)c(O)c(Cl)cc1CCCBr");
        /* create clique earch object */
        OECliqueSearch cs = new OECliqueSearch(pattern, 
                OEExprOpts.DefaultAtoms,
                OEExprOpts.DefaultBonds);
        /* ignore cliques that differ by more than 5 atoms from MCS */
        cs.SetSaveRange(5);

        int count = 1;
        /* loop over matches */
        for (OEMatchBase match : cs.Match(target)) {
            System.out.println("Match " + count + " :");

            System.out.print("pattern atoms: ");
            for(OEMatchPairAtom ma : match.GetAtoms())
                System.out.print(ma.getPattern().GetIdx() + " ");

            System.out.println();

            System.out.print("target atoms: ");
            for(OEMatchPairAtom ma : match.GetAtoms())
                System.out.print(" " + ma.getTarget().GetIdx());

            System.out.println();
            count++;
        }
    }
}
//@ </SNIPPET>
