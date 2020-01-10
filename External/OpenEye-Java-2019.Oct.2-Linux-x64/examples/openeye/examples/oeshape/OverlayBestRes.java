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

// This example reads in a reference molecule and a few fit
// molecules, performs overlay optimization, sorts the results
// in tanimoto order, and shows the single best result.
// With the default options, OEOverlay optimizes both shape and color.

package openeye.examples.oeshape;

import openeye.oechem.*;
import openeye.oeshape.*;

public class OverlayBestRes {

    public static void main(String[] args) {
        if (args.length!=2) {
            System.out.println("OverlayBestRes <reffile> <fitfile>");
            System.exit(0);
        }

        oemolistream reffs = new oemolistream(args[0]);
        oemolistream fitfs = new oemolistream(args[1]);

        OEMol refmol = new OEMol();
        oechem.OEReadMolecule(reffs, refmol);
        reffs.close();
        System.out.print("Ref. title: "+refmol.GetTitle()+" ");
        System.out.println("Num Confs: "+refmol.NumConfs());

        // Prepare reference molecule for calculation
        // With default options this will remove any explicit 
        // hydrogens present and add color atoms
        OEOverlapPrep prep = new OEOverlapPrep();
        prep.Prep(refmol);

        OEMultiRefOverlay overlay = new OEMultiRefOverlay(); 
        overlay.SetupRef(refmol);

        OEMol fitmol = new OEMol();
        while (oechem.OEReadMolecule(fitfs, fitmol)) {
            prep.Prep(fitmol);
            OEBestOverlayScore score = new OEBestOverlayScore();
            overlay.BestOverlay(score, fitmol, new OEHighestTanimoto());

            System.out.print("Fit. title: "+fitmol.GetTitle()+" ");
            System.out.print("FitConfIdx: "+score.GetFitConfIdx()+" ");
            System.out.print("RefConfIdx: "+score.GetRefConfIdx()+" ");
            System.out.println("Tanimoto Combo: "+score.GetTanimotoCombo());      
        }
        fitfs.close();
    }
}

