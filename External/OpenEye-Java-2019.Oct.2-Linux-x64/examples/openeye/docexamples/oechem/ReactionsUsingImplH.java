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

public class ReactionsUsingImplH {
    public static void main(String argv[]) {
        OELibraryGen libgen = 
                new OELibraryGen("[O:1]=[C:2][Cl:3].[N:4]>>[O:1]=[C:2][Nh1:4]");
        libgen.SetExplicitHydrogens(false);

        OEGraphMol mol = new OEGraphMol();    
        oechem.OESmilesToMol(mol, "CC(=O)Cl");
        libgen.SetStartingMaterial(mol, 0);

        mol.Clear();
        oechem.OESmilesToMol(mol, "NCC");
        libgen.SetStartingMaterial(mol, 1);

        for (OEMolBase product : libgen.GetProducts()) {
            String smi = oechem.OEMolToSmiles(product);
            System.out.println("product smiles = "+smi);
        }
    }
}
//@ </SNIPPET>
