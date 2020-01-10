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
package openeye.examples.oezap;

import openeye.oechem.*;
import openeye.oezap.*;

public class ZapBind {

    public static void main(String argv[]) {
        if (argv.length != 2) {
            System.err.println("usage: ZapBind <protein> <ligands>");
            System.exit(1);
        }

        OEMol protein = new OEMol();
        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            System.err.println("Unable to open for reading: " + argv[0]);
            System.exit(1);
        }
        oechem.OEReadMolecule(ifs, protein);
        oechem.OEAssignBondiVdWRadii(protein);
        oechem.OEMMFFAtomTypes(protein);
        oechem.OEMMFF94PartialCharges(protein);
        System.err.println("protein: " + protein.GetTitle());
        ifs.close();

        OEBind bind = new OEBind();
        bind.SetProtein(protein);
        OEBindResults results = new OEBindResults();

        OEMol ligand = new OEMol();
        if (!ifs.open(argv[1])) {
            System.err.println("Unable to open for reading: " + argv[1]);
            System.exit(1);
        }
        ifs.SetConfTest(new OEIsomericConfTest());
        while (oechem.OEReadMolecule(ifs, ligand)) {
            oechem.OEAssignBondiVdWRadii(ligand);
            oechem.OEMMFFAtomTypes(ligand);
            oechem.OEMMFF94PartialCharges(ligand);
            System.err.println("ligand: " + ligand.GetTitle());

            for (OEConfBase conf : ligand.GetConfs()) {
                bind.Bind(conf, results);
                System.err.println(" conf# " + conf.GetIdx() + " be = "
                        + results.GetBindingEnergy());
            }
            System.err.println();
        }
        results.delete();
        bind.delete();
    }
}
