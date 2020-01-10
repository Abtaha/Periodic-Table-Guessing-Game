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

public class MDLQuerySearchMapping {
    public static void main(String argv[]) {
        oemolistream qfile = new oemolistream("query.mol");
        oemolistream tfile = new oemolistream("targets.sdf");

        //read MDL query and initialize the substructure search
        OEQMol qmol = new OEQMol();
        oechem.OEReadMDLQueryFile(qfile,qmol);
        qfile.close();
        OESubSearch ss = new OESubSearch(qmol);

        // loop over target structures
        int tindex = 1;
        OEGraphMol tmol = new OEGraphMol();
        while(oechem.OEReadMolecule(tfile,tmol)) {
            oechem.OEAddExplicitHydrogens(tmol);
            oechem.OEPrepareSearch(tmol, ss);
            for (OEMatchBase match : ss.Match(tmol,true)) {
                System.out.print("hit target = " + tindex);

                for(OEAtomBase a : match.GetTargetAtoms())
                    System.out.print(" "+a.GetIdx()+oechem.OEGetAtomicSymbol(a.GetAtomicNum()));

                System.out.println();
            }
            tindex += 1;
        }
        tfile.close();
    }
}
//@ </SNIPPET>
