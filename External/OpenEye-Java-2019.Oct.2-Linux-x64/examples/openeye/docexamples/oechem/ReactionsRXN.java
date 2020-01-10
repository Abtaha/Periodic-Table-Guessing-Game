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

public class ReactionsRXN {
    private static void printatoms(String label, OEQAtomBaseIter aiter) {
        System.out.println(label);

        aiter.ToFirst();
        for (OEAtomBase atom : aiter) {
            System.out.print(" atom: " + atom.GetIdx() + " ");
            System.out.print(oechem.OEGetAtomicSymbol(atom.GetAtomicNum()));
            System.out.print(" in component " + atom.GetIntData(OEProperty.Component));
            System.out.println(" with map index " + atom.GetMapIdx());
        }
    }

    public static void main(String argv[]) {
        oemolistream rfile = new oemolistream("amide.rxn");
        OEQMol reaction = new OEQMol();
        // reading reaction
        int opt = OEMDLQueryOpts.ReactionQuery | OEMDLQueryOpts.SuppressExplicitH;
        oechem.OEReadMDLReactionQueryFile(rfile, reaction, opt); 
        rfile.close();
        printatoms("Reactant atoms:",reaction.GetQAtoms(new OEAtomIsInReactant()));
        printatoms("Product atoms :",reaction.GetQAtoms(new OEAtomIsInProduct()));
        // initializing library generator
        OELibraryGen libgen = new OELibraryGen();
        libgen.Init(reaction);
        libgen.SetExplicitHydrogens(false); 
        libgen.SetValenceCorrection(true);  
        // adding reactants
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1ccc(cc1)C(=O)O");
        libgen.AddStartingMaterial(mol, 0);
        mol.Clear();
        oechem.OESmilesToMol(mol, "CC(C)CN");
        libgen.AddStartingMaterial(mol, 1);

        // accessing generated products
        for (OEMolBase product : libgen.GetProducts())
            System.out.println("product smiles = "+oechem.OEMolToSmiles(product));
    }
}
//@ </SNIPPET>
