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

public class ScoreTagging {
    private static void tagPoses(OEScore oescore, OEMCMolBase poses, String sdtag) {
        for (OEConfBase pose : poses.GetConfs()) {
            float score = oescore.ScoreLigand(pose);
            String stringScore = String.format("%f", score);
            oechem.OESetSDData(pose, sdtag, stringScore);
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("ScoreTagging <receptor> <poses>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);
        OEScore oescore = new OEScore();
        oescore.Initialize(receptor);

        OEMol poses = new OEMol();
        oemolistream imstr = new oemolistream(argv[1]);

        while (oechem.OEReadMolecule(imstr, poses)) {
            tagPoses(oescore, poses, oescore.GetName());
        }
        imstr.close();
    }
}
