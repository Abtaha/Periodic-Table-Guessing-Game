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

public class MolFind {
    public static void main(String argv[]) {
        OESubSearch ss   = new OESubSearch(argv[0]);

        oemolistream ifs = new oemolistream(argv[1]);
        oemolostream ofs = new oemolostream(argv[2]);

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEPrepareSearch(mol, ss);
            if (ss.SingleMatch(mol))
                oechem.OEWriteMolecule(ofs, mol);
        }
        ofs.close();
        ifs.close();
    }
}
//@ </SNIPPET>
