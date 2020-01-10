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
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class SubstructureSearchMatch {
    public static void main(String argv[]) {
        OEGraphMol qmol = new OEGraphMol();
        oechem.OESmilesToMol(qmol, "c1ccccc1");

        /* create a substructure search object */
        //@ <SNIPPET>
        int itag = oechem.OEGetTag("__orig_idx");
        for (OEAtomBaseIter ai = qmol.GetAtoms(); ai.hasNext();)
            ai.Target().SetIntData(itag, ai.Target().GetIdx());

        OESubSearch ss = new OESubSearch(qmol, OEExprOpts.DefaultAtoms, OEExprOpts.DefaultBonds);

        OEGraphMol tmol = new OEGraphMol();
        oechem.OESmilesToMol(tmol, "Cc1ccccc1");
        oechem.OEPrepareSearch(tmol, ss);

        for (OEMatchBase mi : ss.Match(tmol, true))
        {
          OEMatch match = new OEMatch();
          for (OEMatchPairAtomIter apairi = mi.GetAtoms(); apairi.hasNext();)
          {
            OEMatchPairAtom apair = apairi.next();  
            int pidx = apair.getPattern().GetIntData(itag);
            OEAtomBase pattern = qmol.GetAtom(new OEHasAtomIdx(pidx));
            match.AddPair(pattern, apair.getTarget());
          }
        }
        //@ </SNIPPET>
    }
}

