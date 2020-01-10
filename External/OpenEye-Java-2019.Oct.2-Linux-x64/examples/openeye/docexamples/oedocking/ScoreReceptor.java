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

public class ScoreReceptor {
    private static void printScore(OEScore score, OEConfBase pose) {
        System.out.format("Total ligand score = %8.2f\n", score.ScoreLigand(pose));
        System.out.format("Score components contributions to score:\n");
        for (String comp : score.GetComponentNames()) {
            System.out.format("%15s: %6.2f\n", comp, score.ScoreLigandComponent(pose, comp));
        }
    }

    private static void printAtomScore(OEScore score, OEConfBase pose, OEAtomBase atom) {
        System.out.format("\nAtom: %d  score: %6.2f \n",
                atom.GetIdx(),
                score.ScoreAtom(atom, pose));
        System.out.format("Score components contribution to atom scores:\n");
        for (String comp : score.GetComponentNames()) {
            System.out.format("%15s: %.2f\n", comp, score.ScoreAtomComponent(atom, pose, comp));
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("ScoreReceptor <receptor> <ligand>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);

        OEScore score = new OEScore();
        score.Initialize(receptor);

        oemolistream imstr = new oemolistream(argv[1]);
        OEMol mcmol = new OEMol();
        oechem.OEReadMolecule(imstr, mcmol);
        imstr.close();

        for (OEConfBase pose : mcmol.GetConfs()) {
            printScore(score, pose);
            for (OEAtomBase atom : pose.GetAtoms()) {
                printAtomScore(score, pose, atom);
            }
        }
    }
}
