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

import openeye.oechem.*;
import openeye.oebio.*;

public class ResCount {

    private void displayUsage() {
        System.err.println("usage: ResCount <mol>");
        System.exit(1);
    }
    private void resCount(String filename) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(filename)) {
            System.err.println("Unable to open input file: " + filename);
            return;
        }

        int molCt=0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            ++molCt;
            if (! oechem.OEHasResidues(mol))
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);
            int atomCt = 0;
            int watCt = 0;
            int resCt = 0;
            int fragCt = 0;
            int chainCt = 0;

            OEHierView hv = new OEHierView(mol);
            for (OEHierChain chain : hv.GetChains()) {
                ++chainCt;
                for (OEHierFragment frag : chain.GetFragments()) {
                    ++fragCt;
                    for (OEHierResidue res : frag.GetResidues()) {
                        ++resCt;
                        if (oechem.OEGetResidueIndex(res.GetOEResidue()) == OEResidueIndex.HOH)
                            ++watCt;
                        else
                            for (OEAtomBase atom : res.GetAtoms())
                                ++atomCt;
                    }
                }
            }
            System.out.println("==========================================");
            System.out.println("Molecule:  "+molCt);
            System.out.println("Chains:    "+chainCt);
            System.out.println("Fragments: "+fragCt);
            System.out.println("Residues:  "+resCt+"( "+watCt+" ) waters");
            System.out.println("Atoms:     "+atomCt);
            System.out.println();
        }
        ifs.close();
    }

    public static void main(String argv[]) {
        ResCount app = new ResCount();
        if (argv.length < 1)
            app.displayUsage();
        app.resCount(argv[0]);
    }
}
