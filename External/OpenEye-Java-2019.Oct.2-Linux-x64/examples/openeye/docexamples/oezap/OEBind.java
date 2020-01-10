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
package openeye.docexamples.oezap;

import openeye.oechem.*;
import openeye.oezap.*;

public class OEBind {
    private static OEMolBase ReadMolecule(final String fileName) {
        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream();

        if (!ifs.open(fileName)) {
            oechem.OEThrow.Fatal("Unable to open " + fileName + " for reading");
        }

        if (!oechem.OEIs3DFormat(ifs.GetFormat())) {
            oechem.OEThrow.Fatal("Invalid input format: needs 3D coordinates");
        }

        if (!oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEThrow.Fatal("Unable to read a moleucle from " + fileName);
        }

        return mol;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            oechem.OEThrow.Usage("OEBind <protein> <ligand>");
        }

        OEMolBase protein = OEBind.ReadMolecule(args[0]);
        OEMolBase ligand = OEBind.ReadMolecule(args[1]);

        {
            openeye.oezap.OEBind bind = new openeye.oezap.OEBind();
            bind.SetProtein(protein);
            OEBindResults results = new OEBindResults();
            bind.Bind(ligand, results);
            results.Print(oechem.OEThrow);
        }

        {
            openeye.oezap.OEBind bind = new openeye.oezap.OEBind();
            bind.GetZap().SetGridSpacing(0.6f);
        }
    }
}
