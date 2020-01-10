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
package openeye.examples.oebio;

import java.io.*;
import openeye.oechem.*;
import openeye.oebio.*;

public class AltlocfactLocations {

    // list alternate location codes and atom info (unless hideAtoms is true)
    static void PrintLocations(OEMolBase mol, boolean hideAtoms) {
        if (! oechem.OEHasResidues(mol))
            oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

        OEAltLocationFactory alf = new OEAltLocationFactory(mol);

        System.out.println(mol.GetTitle());

        System.out.print("grp-cnt=" + alf.GetGroupCount());
        if (alf.GetGroupCount() > 0) { System.out.println(" {"); }
        else                         { System.out.println("");   }

        for (OEAltGroup grp : alf.GetGroups()) {
            System.out.println(" grp=" + grp.GetGroupID()
                    + " loc-cnt=" + grp.GetLocationCount()
                    + " grp-codes='"+ alf.GetLocationCodes(grp) + "'");

            for (OEAltLocation loc : grp.GetLocations()) {
                System.out.print(" grp=" + loc.GetGroupID()
                        + " loc=" + loc.GetLocationID()
                        + " loc-codes='"+ alf.GetLocationCodes(loc) + "' ");
                if (! hideAtoms) {
                    System.out.print("[ ");
                }
                int num_atoms = 0;
                for (OEAtomBase atom : alf.GetAltAtoms(loc)) {
                    ++num_atoms;
                    if (! hideAtoms) {
                        OEResidue res = oechem.OEAtomGetResidue(atom);
                        System.out.print(atom.GetName()
                                + ":" + res.GetAlternateLocation()
                                + ":" + res.GetName()
                                + res.GetResidueNumber()
                                + res.GetInsertCode()
                                + ":c" + res.GetChainID()
                                +  "m" + res.GetModelNumber() + ";");
                    }
                }
                if (! hideAtoms) {
                    System.out.print(" ] ");
                }
                System.out.println(num_atoms);
            }
        }
        if (alf.GetGroupCount() > 0) {
            System.out.println("}");
        }
    }

    public static void main(String argv[]) {
        if ((argv.length != 1) && (argv.length != 2)) {
            oechem.OEThrow.Usage("AltlocfactLocations [-H] <mol-infile>");
        }
        boolean hideAtoms = false;
        String filename   = argv[0];
        if (argv.length == 2) {
            if (! argv[0].equals("-H")) {
                oechem.OEThrow.Fatal("Bad flag: " + argv[0] + " (expecting -H)");
            }
            hideAtoms = true;
            filename  = argv[1];
        }

        oemolistream ims = new oemolistream();
        if(! ims.open(filename)) {
            oechem.OEThrow.Fatal("Unable to open " + filename + " for reading");
        }
        // need this flavor to read alt loc atoms
        ims.SetFlavor(OEFormat.PDB, OEIFlavor.PDB.ALTLOC);

        OEGraphMol mol = new OEGraphMol();
        while(oechem.OEReadMolecule(ims, mol)) {
            PrintLocations(mol, hideAtoms);
        }
        ims.close();
    }
}
