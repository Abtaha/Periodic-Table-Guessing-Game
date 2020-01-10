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

public class MaximumCommonSS {
    public static void main(String argv[]) {
        OEGraphMol pattern = new OEGraphMol();
        oechem.OESmilesToMol(pattern, "c1cc(O)c(O)cc1CCN");
        OEGraphMol target = new OEGraphMol();
        oechem.OESmilesToMol(target,  "c1c(O)c(O)c(Cl)cc1CCCBr");

        int atomexpr = OEExprOpts.DefaultAtoms;
        int bondexpr = OEExprOpts.DefaultBonds;
        /* create maximum common substructure object */
        int mcsstype = OEMCSType.Exhaustive;
        OEMCSSearch mcss = new OEMCSSearch(pattern,atomexpr,bondexpr,mcsstype);
        /* set scoring function */
        mcss.SetMCSFunc(new OEMCSMaxAtoms());
        /* ignore matches smaller than 6 atoms */
        mcss.SetMinAtoms(6);

        boolean unique = true;
        int count = 1;
        /* loop over matches */
        for (OEMatchBase match : mcss.Match(target,unique)) {
            System.out.println("Match " + count + ":");

            System.out.print("pattern atoms: ");
            for(OEMatchPairAtom ma : match.GetAtoms())
                System.out.print(ma.getPattern().GetIdx()+" ");
            System.out.println();

            System.out.print("target atoms:  ");
            for(OEMatchPairAtom ma : match.GetAtoms())
                System.out.print(ma.getTarget().GetIdx()+" ");
            System.out.println();

            //create match subgraph
            OEGraphMol m = new OEGraphMol();
            oechem.OESubsetMol(m,match,true);
            System.out.println("match smiles = " + oechem.OEMolToSmiles(m));
            count++;
        }
    }
}
//@ </SNIPPET>
