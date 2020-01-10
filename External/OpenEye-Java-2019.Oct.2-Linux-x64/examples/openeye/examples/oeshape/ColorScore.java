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

public class ColorScore {

    public static void main(String[] args) {
        if (args.length!=3) {
            System.out.println("ColorScore <reffile> <overlayfile> <outfile>");
            System.exit(0);
        }

        oemolistream reffs = new oemolistream(args[0]);
        oemolistream fitfs = new oemolistream(args[1]);
        oemolostream outfs = new oemolostream(args[2]);

        OEGraphMol refmol = new OEGraphMol();
        oechem.OEReadMolecule(reffs, refmol);
        reffs.close();

        // Prepare reference molecule for calculation
        // With default options this will add required color atoms
        OEOverlapPrep prep = new OEOverlapPrep();
        prep.Prep(refmol);

        // Get appropriate function to calculate analytic color
        OEAnalyticColorFunc colorFunc = new OEAnalyticColorFunc();
        colorFunc.SetupRef(refmol);

        OEOverlapResults res = new OEOverlapResults();
        OEGraphMol fitmol = new OEGraphMol();
        while (oechem.OEReadMolecule(fitfs, fitmol)) {
            prep.Prep(fitmol);
            colorFunc.Overlap(fitmol, res);
            oechem.OESetSDData(fitmol, "MyColorTanimoto", String.valueOf(res.GetColorTanimoto()));
            oechem.OESetSDData(fitmol, "MyColorScore",  String.valueOf(res.GetColorScore()));
            oechem.OEWriteMolecule(outfs, fitmol);

            System.out.print("Fit. Title: "+fitmol.GetTitle()+" ");
            System.out.print("Color Tanimoto: "+res.GetColorTanimoto()+" ");
            System.out.print("Color Score: "+res.GetColorScore()+" ");
        } 
    }
}


