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
package openeye.examples.oedocking;

import java.net.URL;
import openeye.oechem.*;
import openeye.oedocking.*;

public class RescorePoses {

    private void rescore(OEInterface itf) {
        OEGraphMol receptor = new OEGraphMol();
        if (!oedocking.OEReadReceptorFile(receptor, itf.GetString("-receptor"))) {
            oechem.OEThrow.Fatal("Unable to read receptor");
        }

        oemolistream imstr = new oemolistream();
        if (!imstr.open(itf.GetString("-in"))) {
            oechem.OEThrow.Fatal("Unable to open input file of ligands");
        }

        oemolostream omstr = new oemolostream();
        if (!omstr.open(itf.GetString("-out"))) {
            oechem.OEThrow.Fatal("Unable to open out file for rescored ligands");
        }

        int scoreType = oedocking.OEScoreTypeGetValue(itf, "-score");
        OEScore score = new OEScore(scoreType);
        score.Initialize(receptor);

        OEMol ligand = new OEMol();
        boolean optimize = itf.GetBool("-optimize");
        while (oechem.OEReadMolecule(imstr, ligand)) {
            if (optimize) {
                score.SystematicSolidBodyOptimize(ligand);
            }
            score.AnnotatePose(ligand);
            String sdtag = score.GetName();
            oedocking.OESetSDScore(ligand, score, sdtag);
            oechem.OESortConfsBySDTag(ligand, sdtag, score.GetHighScoresAreBetter());
            oechem.OEWriteMolecule(omstr, ligand);
        }
        omstr.close();
        imstr.close();
    }

    public static void main(String argv[]) {
        RescorePoses app = new RescorePoses();
        URL fileURL = app.getClass().getResource("RescorePoses.txt");

        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedocking.OEScoreTypeConfigure(itf, "-score");
            if (oechem.OEParseCommandLine(itf, argv)) {
                app.rescore(itf);
            }
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
