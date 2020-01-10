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

public class ReactionsNonUnique {
    public static void main(String argv[]) {

        OELibraryGen lg = new OELibraryGen();
        lg.Init("[N:3][c:4]1[n:2][c:7][c:6][c:5][n:1]1>>[c:6]1[c:7][n:2][c:4]2[n:3]cc[n:1]2[c:5]1");
        lg.SetExplicitHydrogens(false);
        lg.SetValenceCorrection(true);

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol,"c1cnc(nc1O)N");
        int matches = lg.AddStartingMaterial(mol,0,false);

        System.out.println("number of matches = " + matches); 
        for (OEMolBase product : lg.GetProducts()) {
            String smi = oechem.OEMolToSmiles(product);
            System.out.println("product smiles = "+smi);
        }
    }
}
//@ </SNIPPET>
