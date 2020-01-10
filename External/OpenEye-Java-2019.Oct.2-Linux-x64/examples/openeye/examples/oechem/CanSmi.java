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
/****************************************************************************
 * Generate canonical smiles of various flavors
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import java.io.*;
import openeye.oechem.*;

/***************************************************************************
 To create unique Kekule smiles, must reperceive bond orders from
 scratch to avoid arbitrary nondeterministic variations, e.g.,
 CC1=C(O)C=CC=C1 vs. CC1=CC=CC=C1O
 This is why OESMILESFlag_Kekule is not sufficient and not used.
 ***************************************************************************/

public class CanSmi {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    public static String canSmi(OEGraphMol mol, boolean isomeric, boolean kekule) {
        oechem.OEFindRingAtomsAndBonds(mol);
        oechem.OEAssignAromaticFlags(mol, OEAroModel.OpenEye);
        int smiflag = OESMILESFlag.Canonical;
        if (isomeric) {
            smiflag |= OESMILESFlag.ISOMERIC;
        }
        if (kekule) {
            for (OEBondBase bond : mol.GetBonds(new OEIsAromaticBond())) {
                bond.SetIntType(5);
            }
            oechem.OECanonicalOrderAtoms(mol);
            oechem.OECanonicalOrderBonds(mol);
            oechem.OEClearAromaticFlags(mol);
            oechem.OEKekulize(mol);
        }
        String smi = oechem.OECreateSmiString(mol, smiflag);
        return smi;
    }

    public static void main(String argv[]) {
        URL fileURL = CanSmi.class.getResource("CanSmi.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "CanSmi", argv);

            boolean isomeric = itf.GetBool("-isomeric");
            boolean kekule   = itf.GetBool("-kekule");
            boolean from3d   = itf.GetBool("-from3d");

            if (from3d) {
                isomeric = true;
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }

            PrintStream out = System.out;
            if (itf.HasString("-o")) {
                try {
                    out = new PrintStream(itf.GetString("-o"));
                }
                catch(java.io.IOException e) {
                    oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
                }
            }

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                if (from3d) {
                    oechem.OE3DToInternalStereo(mol);
                }
                String smi = canSmi(mol, isomeric, kekule);
                String title = mol.GetTitle();
                if ((title != null) && (!title.equals(""))) {
                    smi += " " + title;
                }
                out.println(smi);
            }
            ifs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
