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

public class DockScoreTagging {
    private static void tagPoses(OEDock dock, OEMCMolBase poses, String sdtag) {
        for (OEConfBase pose : poses.GetConfs()) {
            float score = dock.ScoreLigand(pose);
            String stringScore = String.format("%f", score);
            oechem.OESetSDData(pose, sdtag, stringScore);
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("DockScoreTagging <receptor> <conformers>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);
        OEDock dock = new OEDock();
        dock.Initialize(receptor);

        OEMol conformers = new OEMol();
        oemolistream imstr = new oemolistream(argv[1]);
        while (oechem.OEReadMolecule(imstr, conformers)) {
            OEMol poses = new OEMol();
            dock.DockMultiConformerMolecule(poses, conformers);
            tagPoses(dock, poses, dock.GetName());
        }
        imstr.close();
    }
}
