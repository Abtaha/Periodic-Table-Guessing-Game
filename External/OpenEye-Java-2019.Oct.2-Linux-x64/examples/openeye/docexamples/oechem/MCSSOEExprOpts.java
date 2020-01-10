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

public class MCSSOEExprOpts {
    public static void main(String argv[]) {
        OEGraphMol pattern = new OEGraphMol();
        OEGraphMol target  = new OEGraphMol();
        oechem.OESmilesToMol(pattern, "c1(cc(nc2c1C(CCC2)Cl)CCl)O");
        oechem.OESmilesToMol(target,  "c1(c2c(nc(n1)CF)COC=C2)N");

        int atomexpr = OEExprOpts.DefaultAtoms;
        int bondexpr = OEExprOpts.DefaultBonds;

        OEQMol patternQ = new OEQMol(pattern);
        /* generate query with atom and bond expression options */
        patternQ.BuildExpressions(atomexpr,bondexpr);
        OEMCSSearch mcss = new OEMCSSearch(patternQ.QMol());

        boolean unique = true;
        int count = 1;
        /* loop over matches */
        for (OEMatchBase match : mcss.Match(target,unique)) {
            System.out.println("Match " + count + ":");
            System.out.println("Number of matched atoms: " + match.NumAtoms());
            System.out.println("Number of matched bonds: " + match.NumBonds());
            /* create match subgraph */
            OEGraphMol m = new OEGraphMol();
            oechem.OESubsetMol(m,match,true);
            System.out.println("match smiles = "+oechem.OEMolToSmiles(m));
            count++;
        }
    }
}
//@ </SNIPPET>
