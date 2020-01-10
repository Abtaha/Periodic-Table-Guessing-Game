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
import java.io.*;
import java.text.DecimalFormat;

public class NxNShape {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    private static FileOutputStream csv;
    private static PrintWriter csvfile;
    private static DecimalFormat df = new DecimalFormat("#.##");

    private String roundOff(float score) {
        return df.format(score);
    }

    private void oneConf(OEConfBase conf, OEOverlapPrep prep, String filename) {
        csvfile.print(conf.GetTitle() + "_" + conf.GetIdx());
        OEMol refmol = new OEMol(conf);

        OEOverlayOptions options = new OEOverlayOptions();
        options.SetOverlapFunc(new OEGridShapeFunc());
        OEOverlay overlay = new OEOverlay();
        overlay.SetupRef(refmol);

        oemolistream bfs = new oemolistream(filename);
        OEMol fitmol = new OEMol();
        while (oechem.OEReadMolecule(bfs, fitmol)) {
            prep.Prep(fitmol);
            OEBestOverlayResultsIter resiter = overlay.Overlay(fitmol);
            OEBestOverlayScoreIter scoreiter = new OEBestOverlayScoreIter();
            oeshape.OESortOverlayScores(scoreiter, resiter, new OEHighestTanimoto(), 1, true);
            for (OEBestOverlayScore score : scoreiter) {
                csvfile.print("," + roundOff(score.GetTanimoto()));
            }
        }
    }

    private void genHeader(String filename) {
        csvfile.print("name");
        oemolistream ifs = new oemolistream(filename);
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            System.out.println(mol.GetTitle());
            for (OEConfBase conf : mol.GetConfs()) {
                csvfile.print("," + conf.GetTitle() + "_" + conf.GetIdx());
            }
        }
        ifs.close();
        csvfile.println();
    }

    private void openCSV(String csvfilename) {
        try {
            csv = new FileOutputStream(csvfilename);
            csvfile = new PrintWriter(csv);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open output file " + csvfilename);
        }
    }

    public void nxn(String filename, String csvfilename) {
        openCSV(csvfilename);
        genHeader(filename);
        OEOverlapPrep prep = new OEOverlapPrep();
        prep.SetAssignColor(false);
        oemolistream afs = new oemolistream(filename);
        OEMol molA = new OEMol();
        while (oechem.OEReadMolecule(afs, molA)) {
            prep.Prep(molA);
            for (OEConfBase conf : molA.GetConfs()) {
                oneConf(conf, prep, filename);
            }
        }
        csvfile.close();
        afs.close();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("NxNShape <infile> <csvfile>");
            System.exit(0);
        }
        NxNShape app = new NxNShape();
        app.nxn(args[0], args[1]);
    }
}
