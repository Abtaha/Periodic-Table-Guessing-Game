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
 * Extract the ring scaffold of a molecule
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class ExtractScaffold {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    static boolean TraverseForRing(boolean[] visited, OEAtomBase atom) {
        visited[atom.GetIdx()] = true;

        for (OEAtomBase nbor : atom.GetAtoms()) {
            if (!visited[nbor.GetIdx()]) {
                if (nbor.IsInRing()) {
                    return true;
                }

                if (TraverseForRing(visited, nbor)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean DepthFirstSearchForRing(OEAtomBase root, OEAtomBase nbor) {
        boolean[] visited = new boolean[root.GetParent().GetMaxAtomIdx()];

        visited[root.GetIdx()] = true;

        return TraverseForRing(visited, nbor);
    }

    static class IsInScaffold extends OEUnaryAtomPred {
        @Override
        public boolean constCall(OEAtomBase atom) {
            if (atom.IsInRing()) {
                return true;
            }

            int count = 0;
            for (OEAtomBase nbor : atom.GetAtoms()) {
                if (DepthFirstSearchForRing(atom, nbor)) {
                    ++count;
                }
            }
            return count > 1;
        }

        @Override
        public OEUnaryAtomBoolFunc CreateCopy() {
            OEUnaryAtomBoolFunc copy = new IsInScaffold();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    public static void main(String argv[]) {
        URL fileURL = ExtractScaffold.class.getResource("ExtractScaffold.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "ExtractScaffold", argv);

            boolean exo_dbl_bonds = itf.GetBool("-exo");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }

            oemolostream ofs = new oemolostream();
            if (!ofs.open(itf.GetString("-o"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
            }

            OEMol src = new OEMol();
            OEMol dst = new OEMol();
            while (oechem.OEReadMolecule(ifs, src)) {
                OEUnaryAtomPred pred = new IsInScaffold();
                if (exo_dbl_bonds) {
                    pred = new OEOrAtom(pred, new OEIsNonRingAtomDoubleBondedToRing());
                }
                boolean adjustHCount = true;
                oechem.OESubsetMol(dst, src, pred, adjustHCount);

                if (dst.IsValid()) {
                    oechem.OEWriteMolecule(ofs, dst);
                }
            }
            ifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
