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
package openeye.docexamples.oedocking;

import openeye.oechem.*;
import openeye.oedocking.*;

public class DockScoring {
    private static void printScore(OEDock dock, OEConfBase pose) {
        System.out.format("Total pose score = %8.2f\n", dock.ScoreLigand(pose));
        System.out.format("Score components contributions to score:\n");
        for (String comp : dock.GetComponentNames()) {
            System.out.format("%12s: %6.2f\n", comp, dock.ScoreLigandComponent(pose, comp));
        }
    }

    private static void printAtomScore(OEDock dock, OEConfBase pose, OEAtomBase atom) {
        System.out.format("\nAtom: %d  score: %6.2f\n",
                atom.GetIdx(),
                dock.ScoreAtom(atom, pose));
        System.out.format("Score components contributions to atoms score: \n");
        for (String comp : dock.GetComponentNames()) {
            System.out.format("%12s: %.2f\n", comp, dock.ScoreAtomComponent(atom, pose, comp));
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("DockScoring <receptor> <ligand>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);

        OEDock dock = new OEDock(OEDockMethod.PLP);
        dock.Initialize(receptor);

        OEMol mcmol = new OEMol();
        oemolistream imstr = new oemolistream(argv[1]);
        oechem.OEReadMolecule(imstr, mcmol);
        imstr.close();

        int numPoses = 10;
        OEMol poses = new OEMol();
        dock.DockMultiConformerMolecule(poses, mcmol, numPoses);

        for (OEConfBase pose : poses.GetConfs()) {
            printScore(dock, pose);
            for (OEAtomBase atom : pose.GetAtoms()) {
                printAtomScore(dock, pose, atom);
            }
        }
    }
}
