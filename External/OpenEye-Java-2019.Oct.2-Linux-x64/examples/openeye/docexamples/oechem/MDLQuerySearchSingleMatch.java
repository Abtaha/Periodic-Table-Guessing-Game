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

public class MDLQuerySearchSingleMatch {
    public static void main(String argv[]) {
        oemolistream qfile = new oemolistream("query.mol");
        oemolistream tfile = new oemolistream("targets.sdf");

        // set the same aromaticity model for the query and the target file
        int aromodel = OEIFlavor.Generic.OEAroModelMDL;
        int qflavor  = qfile.GetFlavor(qfile.GetFormat());
        qfile.SetFlavor(qfile.GetFormat(),(qflavor|aromodel));
        int tflavor  = tfile.GetFlavor(tfile.GetFormat());
        tfile.SetFlavor(tfile.GetFormat(),(tflavor|aromodel));

        // read MDL query and initialize the substructure search
        int opts = OEMDLQueryOpts.Default|OEMDLQueryOpts.SuppressExplicitH;
        OEQMol qmol = new OEQMol();
        oechem.OEReadMDLQueryFile(qfile,qmol,opts);
        qfile.close();
        OESubSearch ss = new OESubSearch(qmol);
        // loop over target structures
        int tindex  = 1;
        OEGraphMol tmol = new OEGraphMol();
        while (oechem.OEReadMolecule(tfile,tmol))
        {
            oechem.OEPrepareSearch(tmol, ss);
            if (ss.SingleMatch(tmol))
                System.out.println("hit target = "+tindex);

            tindex += 1;
        }
        tfile.close();
    }
}
//@ </SNIPPET>
