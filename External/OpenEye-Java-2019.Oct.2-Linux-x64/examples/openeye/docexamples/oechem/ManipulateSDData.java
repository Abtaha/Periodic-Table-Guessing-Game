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

public class ManipulateSDData {

    private static void DumpSDData(OEMolBase mol) {
        System.out.println("SD data of " + mol.GetTitle());
        // loop over SD data
        for (OESDDataPair dp : oechem.OEGetSDDataPairs(mol))
            System.out.println(dp.GetTag() + " : " + dp.GetValue());
        System.out.println();
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1ccccc1");
        mol.SetTitle("benzene");

        // set some tagged data
        oechem.OESetSDData(mol, "color", "brown");
        oechem.OESetSDData(mol, new OESDDataPair("size", "small"));
        DumpSDData(mol);

        // check for existence of data, then delete it
        if (oechem.OEHasSDData(mol, "size"))
            oechem.OEDeleteSDData(mol, "size");
        DumpSDData(mol);

        // add additional color data
        oechem.OEAddSDData(mol, "color", "black");
        DumpSDData(mol);

        // remove all SD data
        oechem.OEClearSDData(mol);
        DumpSDData(mol);
    }
}
//@ </SNIPPET>
