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
 * Extracting rings/ring systems from input molecules
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class RingSubset {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    public static void RingSubSet(oemolistream ifs, oemolostream ofs, boolean exo) {
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            OEGraphMol submol = new OEGraphMol();
            OEUnaryAtomPred pred = new OEAtomIsInRing();
            if (exo) {
                pred = new OEOrAtom(pred, new OEIsNonRingAtomDoubleBondedToRing());
            }
            boolean adjustHCount = true;
            oechem.OESubsetMol(submol, mol, pred, adjustHCount);

            String title = mol.GetTitle() + "_rings";
            submol.SetTitle(title);
            if (submol.NumAtoms() != 0) {
                oechem.OEWriteMolecule(ofs, submol);
            }
        }
    }

    public static void main(String argv[]) {
        URL fileURL = RingSubset.class.getResource("RingSubset.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "RingSubset", argv);

            boolean exo_dbl_bonds = itf.GetBool("-exo");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }

            oemolostream ofs = new oemolostream(".ism");
            if (itf.HasString("-o")) {
                if (!ofs.open(itf.GetString("-o"))) {
                    oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
                }
            }
            RingSubSet(ifs, ofs, exo_dbl_bonds);
            ifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
