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

package openeye.examples.oeshape;

import openeye.oechem.*;
import openeye.oeshape.*;
import openeye.oegrid.*;
import java.net.URL;
import java.io.IOException;


public class HermiteTanimoto {
    public static void main(String[] args) {

        URL fileURL = HermiteTanimoto.class.getResource("HermiteTanimoto.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface(fileURL, "HermiteTanimoto", args);
        } catch(IOException e) {
            oechem.OEThrow.Fatal("Unable to open itf file");
        }

        int NPolyMax_MAX = itf.GetInt("-NPolyMax_MAX");
        String ifrefname = itf.GetString("-inputreffile");
        String iffitname = itf.GetString("-inputfitfile");
        String ofname = itf.GetString("-outputfile");

        oemolistream ifsref = new oemolistream();
        oemolistream ifsfit = new oemolistream();
        oemolostream ofs = new oemolostream();

        if (!ifsref.open(ifrefname)){
            oechem.OEThrow.Fatal("Unable to open " + ifrefname + " file for reading" );
        }

        if (!ifsfit.open(iffitname)){
            oechem.OEThrow.Fatal("Unable to open " + iffitname + " file for reading" );
        }

        OEMol refmol = new OEMol();
        OEMol fitmol = new OEMol();

        if (!oechem.OEReadMolecule(ifsref, refmol)){
            oechem.OEThrow.Fatal("Unable to read molecule in " + ifrefname + " file");
        }

        OEOverlapPrep prep = new OEOverlapPrep();
        prep.SetAssignColor(false);
        prep.Prep(refmol);
        OETrans reftransfm = new OETrans();
        oeshape.OEOrientByMomentsOfInertia(refmol, reftransfm);

        if (!ofs.open(ofname)){
            oechem.OEThrow.Fatal("Unable to open " + ofname + " file for writing");
        }

        int idx = 0;
        OETrans transfm = new OETrans();
        while (oechem.OEReadMolecule(ifsfit, fitmol)){
            OEMol fitmol_copy = new OEMol(fitmol);
            System.out.println("Working on the fit molecule with idx = " + String.valueOf(idx) + "...");

            fitmol = fitmol_copy;
            prep.Prep(fitmol);
            oeshape.OEOrientByMomentsOfInertia(fitmol, transfm);

            OEHermiteOptions hermiteoptionsref = new OEHermiteOptions();
            hermiteoptionsref.SetNPolyMax(NPolyMax_MAX);
            hermiteoptionsref.SetUseOptimalLambdas(true);

            OEHermiteOptions hermiteoptionsfit = new OEHermiteOptions();
            hermiteoptionsfit.SetNPolyMax(NPolyMax_MAX);
            hermiteoptionsfit.SetUseOptimalLambdas(true);

            OEShapeOptions shape_options = new OEShapeOptions();
            OEHermiteShapeFunc hermiteshapeoverlap = new OEHermiteShapeFunc(shape_options, hermiteoptionsref, hermiteoptionsfit);

            OEOverlayOptions options = new OEOverlayOptions();
            options.SetOverlapFunc(hermiteshapeoverlap);
            OEOverlay overlay = new OEOverlay(options);
            overlay.SetupRef(refmol);

            OEBestOverlayScoreIter scoreiter = new OEBestOverlayScoreIter();
            oeshape.OESortOverlayScores(scoreiter, overlay.Overlay(new OEMol(fitmol)), new OEHighestTanimoto());

            double hermitetanimoto = -1.0;
            for (OEBestOverlayScore score : scoreiter) {
                hermitetanimoto = score.GetTanimoto();
                score.Transform(fitmol);
            }

            // Transform from the inertial frame to the original reference mol frame
            reftransfm.Transform(fitmol);

            System.out.println("Hermite Tanimoto = " + String.valueOf(hermitetanimoto));

            oechem.OESetSDData(fitmol, "HermiteTanimoto_"+String.valueOf(NPolyMax_MAX), String.valueOf(hermitetanimoto));
            oechem.OEWriteMolecule(ofs, fitmol);
            idx += 1;
        }
        ofs.flush();
        ofs.close();
    }
}
