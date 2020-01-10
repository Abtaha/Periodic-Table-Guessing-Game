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
 * Find the minimum path length between 2 smarts patterns
 * or the path length between 2 named atoms
 ****************************************************************************/
package openeye.examples.oechem;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.net.URL;
import openeye.oechem.*;

public class MinPath {

    private static class Pair<L,R> {

        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }
        public R getRight() {
            return right;
        }

        public int hashCode() {
            return left.hashCode() ^ right.hashCode();
        }

        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof Pair)) return false;
            Pair pairo = (Pair) o;
            return this.left.equals(pairo.getLeft()) &&
                    this.right.equals(pairo.getRight());
        }
    }

    public static void AtomPathLength(oemolistream ifs, oemolostream ofs,
            OEInterface itf,
            String atm1, String atm2) {

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OETriposAtomNames(mol);

            OEAtomBase a1 = null;
            OEAtomBase a2 = null;
            for (OEAtomBase atm : mol.GetAtoms()) {
                if (atm1.equals(atm.GetName())) {
                    a1 = atm;
                }
                if (atm2.equals(atm.GetName())) {
                    a2 = atm;
                }
                if (a1 != null && a2 != null) {
                    break;
                }
            }
            if (a1 == null || a2 == null) {
                oechem.OEThrow.Warning("Failed to find atoms " + atm1 + " and " + atm2 + " in molecule");
                continue;
            }

            int pathlen = oechem.OEGetPathLength(a1, a2);
            if (itf.GetBool("-verbose") || !itf.HasString("-o"))
            {
                String smiles = oechem.OECreateIsoSmiString(mol);
                System.out.println("Path length: " + pathlen + " in " + smiles);
            }

            OEAtomBaseIter spath = oechem.OEShortestPath(a1, a2);
            OEGraphMol spathmol = new OEGraphMol();
            boolean adjustHCount = true;
            oechem.OESubsetMol(spathmol, mol, new OEIsAtomMember(spath), adjustHCount);
            String spathsmiles = oechem.OECreateIsoSmiString(spathmol);

            if (itf.HasString("-o")) {
                oechem.OEWriteMolecule(ofs, spathmol);
            }
            else if (itf.GetBool("-verbose")) {
                System.out.println(spathsmiles);
            }
        }
    }

    public static void SmartsPathLength(oemolistream ifs, oemolostream ofs,
            OEInterface itf, OESubSearch ss1, OESubSearch ss2) {
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEPrepareSearch(mol, ss1);
            oechem.OEPrepareSearch(mol, ss2);
            if (!(ss1.SingleMatch(mol) && ss2.SingleMatch(mol))) {
                oechem.OEThrow.Warning("Unable to find SMARTS matches in "
                        + mol.GetTitle() + ", skipping");
                continue;
            }

            List<Pair> allatompairs = new LinkedList<Pair>();
            boolean unique = true;
            int allminlen = Integer.MAX_VALUE;
            for (OEMatchBase match1 : ss1.Match(mol, unique)) {
                for (OEMatchBase match2 : ss2.Match(mol, unique)) {
                    List<Pair> atompairs = new LinkedList<Pair>();
                    int minlen = Integer.MAX_VALUE;
                    for (OEMatchPairAtom mp1 : match1.GetAtoms()) {
                        for (OEMatchPairAtom mp2 : match2.GetAtoms()) {
                            int pathlen = oechem.OEGetPathLength(mp1.getTarget(), mp2.getTarget());
                            if (minlen > pathlen) {
                                minlen = pathlen;
                                atompairs.clear();
                                atompairs.add(new Pair<OEAtomBase, OEAtomBase>(mp1.getTarget(), mp2.getTarget()));
                            }
                            else if (minlen == pathlen) {
                                atompairs.add(new Pair<OEAtomBase, OEAtomBase>(mp1.getTarget(), mp2.getTarget()));
                            }
                        }
                    }
                    if (minlen < allminlen) {
                        allminlen = minlen;
                        allatompairs = atompairs;
                    }
                    else if (minlen == allminlen) {
                        allatompairs.addAll(atompairs);
                    }
                }
            }

            if (itf.GetBool("-verbose") || !itf.HasString("-o"))
            {
                System.out.println("Shortest path length: " + allminlen + " in " + oechem.OECreateIsoSmiString(mol));
            }

            Set<String> spathlist = new HashSet<String>();
            for (Pair pair : allatompairs) {
                OEAtomBaseIter spath = oechem.OEShortestPath((OEAtomBase)pair.getLeft(), (OEAtomBase)pair.getRight());
                OEGraphMol spathmol = new OEGraphMol();
                oechem.OESubsetMol(spathmol, mol, new OEIsAtomMember(spath));
                String spathsmiles = oechem.OECreateIsoSmiString(spathmol);
                if (!spathlist.add(spathsmiles)) {
                    continue;
                }
                if (itf.HasString("-o")) {
                    oechem.OEWriteMolecule(ofs, spathmol);
                }
                else if (itf.GetBool("-verbose")) {
                    System.out.println(spathsmiles);
                }
            }
        }
    }

    public static void main(String argv[]) {
        URL fileURL = MinPath.class.getResource("MinPath.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "MinPath", argv);

            if (!((itf.HasString("-smarts1") && itf.HasString("-smarts2"))
                    ^ (itf.HasString("-atom1") && itf.HasString("-atom2")))) {
                oechem.OEThrow.Fatal("-smarts1 and -smarts2 or -atom1 and -atom2 must be set");
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }

            oemolostream ofs = new oemolostream();
            if (itf.HasString("-o")) {
                if (!ofs.open(itf.GetString("-o"))) {
                    oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
                }
            }

            if (itf.HasString("-smarts1") && itf.HasString("-smarts2")) {
                OESubSearch ss1 = new OESubSearch();
                String smarts1 = itf.GetString("-smarts1");
                if (!ss1.Init(smarts1)) {
                    oechem.OEThrow.Fatal("Unable to parse SMARTS1: " + smarts1);
                }

                OESubSearch ss2 = new OESubSearch();
                String smarts2 = itf.GetString("-smarts2");
                if (!ss2.Init(smarts2)) {
                    oechem.OEThrow.Fatal("Unable to parse SMARTS2: " + smarts2);
                }

                SmartsPathLength(ifs, ofs, itf, ss1, ss2);
                ifs.close();
                ofs.close();
            }
            else {
                String atom1 = itf.GetString("-atom1");
                String atom2 = itf.GetString("-atom2");
                AtomPathLength(ifs, ofs, itf, atom1, atom2);
            }
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
