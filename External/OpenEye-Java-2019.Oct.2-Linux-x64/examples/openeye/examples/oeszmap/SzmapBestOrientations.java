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
// Generate szmap points and probe orientations at ligand atoms

package openeye.examples.oeszmap;

import java.net.URL;

import openeye.oechem.OEAtomBase;
import openeye.oechem.OEGraphMol;
import openeye.oechem.OEInterface;
import openeye.oechem.OEMol;
import openeye.oechem.OEMolBase;
import openeye.oechem.*;
import openeye.oechem.oemolistream;
import openeye.oechem.oemolostream;
import openeye.oeszmap.OESzmapEngine;
import openeye.oeszmap.OESzmapResults;
import openeye.oeszmap.oeszmap;

public class SzmapBestOrientations {

    // generateSzmapProbes: generate multiconf probes and data-rich points at
    // ligand coords
    // oms: output mol stream for points and probes
    // cumulativeProb: cumulative probability for cutoff of point set
    // lig: mol defining coordinates for szmap calcs
    // prot: context mol for szmap calcs (must have charges and radii)
    private static void generateSzmapProbes(oemolostream oms, double cumulativeProb, OEMolBase lig, OEMolBase prot) {
        OEFloatArray coord = new OEFloatArray(3);

        OESzmapEngine sz = new OESzmapEngine(prot);
        OESzmapResults result = new OESzmapResults();

        OEGraphMol points = new OEGraphMol();
        String title = "points " + lig.GetTitle();
        points.SetTitle(title);

        OEMol probes = new OEMol();

        for (OEAtomBase atom : lig.GetAtoms()) {
            lig.GetCoords(atom, coord);

            if (!oeszmap.OEIsClashing(sz, coord)) {
                oeszmap.OECalcSzmapResults(result, sz, coord);

                result.PlaceNewAtom(points);

                boolean clear = false;
                result.PlaceProbeSet(probes, cumulativeProb, clear);
            }
        }

        oechem.OEWriteMolecule(oms, points);
        oechem.OEWriteMolecule(oms, probes);
    }

    public static void main(String[] argv) {
        // the protein should have charges and radii but the ligand need not
        URL fileURL = SzmapBestOrientations.class.getResource("SzmapBestOrientations.txt");
        int returnCode = 0;
        try {
            OEInterface itf = new OEInterface(fileURL, "SzmapBestOrientations", argv);

            String ligFile = itf.GetString("-l");
            oemolistream lims = new oemolistream();
            if (!lims.open(ligFile)) {
                oechem.OEThrow.Fatal("Unable to open " + ligFile + " for reading ");
            }
            OEGraphMol lig = new OEGraphMol();

            oechem.OEReadMolecule(lims, lig);

            String contextFile = itf.GetString("-p");
            oemolistream pims = new oemolistream();
            if (!pims.open(contextFile)) {
                oechem.OEThrow.Fatal("Unable to open" + contextFile + " for reading");
            }
            OEGraphMol prot = new OEGraphMol();

            oechem.OEReadMolecule(pims, prot);

            String outputFile = itf.GetString("-o");
            oemolostream oms = new oemolostream();
            if (!oms.open(outputFile)) {
                oechem.OEThrow.Fatal("Unable to open " + outputFile + " for writing");
            }
            generateSzmapProbes(oms, itf.GetDouble("-prob"), lig, prot);
            oms.close();
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file: " + e.getMessage());
            returnCode = 1;
        }
        System.exit(returnCode);
    }
}
