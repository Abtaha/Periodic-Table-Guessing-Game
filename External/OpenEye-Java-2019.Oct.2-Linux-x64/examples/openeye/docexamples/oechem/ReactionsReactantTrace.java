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

public class ReactionsReactantTrace {
    public static void main(String argv[]) {
        OELibraryGen libgen = new OELibraryGen("[O:1]=[C:2][Cl:3].[N:4]>>[O:1]=[C:2][N:4]");
        libgen.SetExplicitHydrogens(false);
        libgen.SetValenceCorrection(true);
        int nrproducts = 1;
        int nrmatches  = 0;

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol,"CC(=O)Cl acetyl chloride");      
        nrmatches += libgen.AddStartingMaterial(mol,0);
        mol.Clear();
        oechem.OESmilesToMol(mol,"c1ccccc1C(=O)Cl benzoyl chloride");      
        nrmatches += libgen.AddStartingMaterial(mol,0);
        nrproducts *= nrmatches;
        mol.Clear();

        nrmatches = 0;
        oechem.OESmilesToMol(mol,"NCC ethanamine");
        nrmatches += libgen.AddStartingMaterial(mol,1);
        mol.Clear();
        oechem.OESmilesToMol(mol,"OCCN 2-aminoethanol"); 
        nrmatches += libgen.AddStartingMaterial(mol,1);
        nrproducts *= nrmatches;
        System.out.println("Number of products = "+nrproducts);

        libgen.SetTitleSeparator("#");
        for (OEMolBase product : libgen.GetProducts()) {
            String smi = oechem.OEMolToSmiles(product);
            System.out.println("product smiles = "+smi+" "+product.GetTitle());
        }
    }
}
//@ </SNIPPET>
