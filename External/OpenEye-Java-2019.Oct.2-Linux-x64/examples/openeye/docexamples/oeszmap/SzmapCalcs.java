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
package openeye.docexamples.oeszmap;

import java.io.*;
import openeye.oechem.*;
import openeye.oeszmap.*;

public class SzmapCalcs {

    static String InterfaceData =
                    "!BRIEF SzmapCalcs [-prob #.#] [-p] <molfile> [-l] <molfile> [-o] <molfile>\n" +
                    "!PARAMETER -p\n" +
                    "  !TYPE string\n" +
                    "  !BRIEF Input protein (or other) context mol\n" +
                    "  !REQUIRED true\n" +
                    "  !KEYLESS 1\n" +
                    "!END\n" +
                    "!PARAMETER -l\n" +
                    "  !TYPE string\n" +
                    "  !BRIEF Input ligand coordinates for calculations\n" +
                    "  !REQUIRED true\n" +
                    "  !KEYLESS 2\n" +
                    "!END\n" +
                    "!PARAMETER -o\n" +
                    "  !TYPE string\n" +
                    "  !BRIEF Output file for points and probes molecules\n" +
                    "  !REQUIRED true\n" +
                    "  !KEYLESS 3\n" +
                    "!END\n" +
                    "!PARAMETER -prob\n" +
                    "  !TYPE double\n" +
                    "  !DEFAULT 0.5\n" +
                    "  !BRIEF Cutoff for cumulative probability of probes\n" +
                    "  !REQUIRED false\n" +
                    "!END\n";

    // runSzmap: run szmap at ligand coordinates in the protein context
    // lig: mol defining coordinates for szmap calcs
    // prot: context mol for szmap calcs (must have charges and radii)
    private static void runSzmap(OEMolBase lig, OEMolBase prot) {
        OESzmapEngineOptions opt = new OESzmapEngineOptions();

        OEGraphMol mol = new OEGraphMol();
        opt.GetProbeMol(mol);
        System.out.println("probe mol smiles: " + oechem.OEMolToSmiles(mol));

        opt.SetProbe(360);
        System.out.println("norient = " + opt.NumOrientations());

        opt.SetProbe(24).SetMaskCutoff(999.99);
        System.out.println("norient = " + opt.NumOrientations());
        System.out.format( "cutoff  = %.3f%n", opt.GetMaskCutoff());

        opt.SetMaskCutoff(0.0); // reset cutoff
        System.out.format("cutoff  = %.3f%n", opt.GetMaskCutoff());

        OEMCMolBase p = opt.GetProbe();
        System.out.println("probe nconf = " + p.NumConfs());

        OESzmapEngine sz = new OESzmapEngine(prot, opt);

        {
        OESzmapEngineOptions opts = sz.GetOptions();
        System.out.println("norient= " + opts.NumOrientations());
        System.out.println("name   = " + opts.GetProbeName());
        System.out.format("cutoff = %.3f%n", opts.GetMaskCutoff());
        }

        {
        OEMolBase ctxMol = sz.GetContext();
        System.out.println("context mol: " + ctxMol.GetTitle());
        }
        System.out.println("probe smiles: " + oechem.OEMolToSmiles(opt.GetProbe()));

        OEFloatArray coord = new OEFloatArray(3);

        OESzmapResults result = new OESzmapResults();

        for (OEAtomBase atom:  lig.GetAtoms()) {
            lig.GetCoords(atom, coord);

            {
            String name = oeszmap.OEGetEnsembleName(OEEnsemble.NeutralDiffDeltaG);
            System.out.println("ensemble name: " + name);
            }

            {
            boolean longName = true;
            String name = oeszmap.OEGetEnsembleName(OEEnsemble.NeutralDiffDeltaG, longName);
            double nddg = oeszmap.OECalcSzmapValue(sz, coord);
            System.out.format("ensemble name: %s, value: %.3f%n", name, nddg);
            }

            {
            String name = oeszmap.OEGetComponentName(OEComponent.Interaction);
            System.out.println("component name: " + name);
            }

            if (! oeszmap.OEIsClashing(sz, coord)) {
                oeszmap.OECalcSzmapResults(result, sz, coord);
                double nddg = result.GetEnsembleValue(OEEnsemble.NeutralDiffDeltaG);
            }

            if (! oeszmap.OEIsClashing(sz, coord)) {
                oeszmap.OECalcSzmapResults(result, sz, coord);
                // ...

                System.out.println("result norient = " + result.NumOrientations());

                double nddg = result.GetEnsembleValue(OEEnsemble.NeutralDiffDeltaG);
                System.out.format("nddG = %.3f%n", nddg);
                System.out.format("vdw  = %.3f%n", result.GetEnsembleValue(OEEnsemble.VDW));

                System.out.println("interaction:");
                OEDoubleIter coulombIter = new OEDoubleIter(result.GetComponent(OEComponent.Interaction));
                while(coulombIter.hasNext()) {
                    System.out.println(coulombIter.next());
                }

                OEUIntArray order = new OEUIntArray(result.NumOrientations());
                result.GetProbabilityOrder(order);
                System.out.println("conf with greatest prob = " + order.getItem(0));

                OEDoubleArray prob = new OEDoubleArray(result.NumOrientations());
                result.GetProbabilities(prob);
                System.out.format("greatest prob = %.3f%n", prob.getItem(order.getItem(0)));

                OEDoubleArray vdw = new OEDoubleArray(result.NumOrientations());
                result.GetComponent(vdw, OEComponent.VDW);
                System.out.format("vdw greatest prob = %.3f%n", vdw.getItem(order.getItem(0)));

                OEFloatArray point = new OEFloatArray(3);
                result.GetCoords(point);
                System.out.format("calc point: (%.3f, %.3f, %.3f)%n",
                                   point.getItem(0), point.getItem(1), point.getItem(2));

                OEMol mcmol = new OEMol();
                result.PlaceProbeSet(mcmol);

                double probCutoff = 0.5;
                result.PlaceProbeSet(mcmol, probCutoff);
                System.out.println("nconf to yield 50pct = " + mcmol.NumConfs());

                boolean clear = false;
                double cumulativeProb = result.PlaceProbeSet(mcmol, 10, clear);
                System.out.println("best 10 cumulative prob = " + cumulativeProb);

                OEGraphMol pmol = new OEGraphMol();
                result.PlaceProbeMol(pmol, order.getItem(0));
            }
        }
    }

    private static void placeNewAtom(OESzmapResults result) {
        OEGraphMol amol = new OEGraphMol();
        OEAtomBase atom = result.PlaceNewAtom(amol);
        System.out.println("vdw = " + atom.GetStringData("vdw"));
    }

    private static void GetSzmapEnergies(OEMolBase lig, OEMolBase prot) {
        //run szmap at ligand coordinates in the protein context

        //lig: mol defining coordinates for szmap calcs
        //prot: context mol for szmap calcs (must have charges and radii)

        System.out.format("num\tatom\t%s\t%s\t%s\t%s\t%s%n",
                    oeszmap.OEGetEnsembleName(OEEnsemble.NeutralDiffDeltaG),
                    oeszmap.OEGetEnsembleName(OEEnsemble.PSolv),
                    oeszmap.OEGetEnsembleName(OEEnsemble.WSolv),
                    oeszmap.OEGetEnsembleName(OEEnsemble.VDW),
                    oeszmap.OEGetEnsembleName(OEEnsemble.OrderParam) );

        OEFloatArray coord = new OEFloatArray(3);

        OESzmapEngine sz = new OESzmapEngine(prot);
        OESzmapResults result = new OESzmapResults();

        int i = 0;
        for(OEAtomBase atom:  lig.GetAtoms()) {
            lig.GetCoords(atom, coord);

            if (! oeszmap.OEIsClashing(sz, coord)) {
                oeszmap.OECalcSzmapResults(result, sz, coord);

                System.out.format("%2d\t%s\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f%n",
                        i, atom.GetName(),
                        result.GetEnsembleValue(OEEnsemble.NeutralDiffDeltaG),
                        result.GetEnsembleValue(OEEnsemble.PSolv),
                        result.GetEnsembleValue(OEEnsemble.WSolv),
                        result.GetEnsembleValue(OEEnsemble.VDW),
                        result.GetEnsembleValue(OEEnsemble.OrderParam) );
            } else {
                System.out.format("%2d\t%s CLASH%n", i, atom.GetName());
            }
            i++;
        }
    }

    private static void GenerateSzmapProbes(oemolostream oms,
                                            double cumulativeProb,
                                            OEMolBase lig,
                                            OEMolBase prot) {
        //generate multiconf probes and data-rich points at ligand coords

        //oms: output mol stream for points and probes
        //cumulativeProb: cumulative probability for cutoff of point set
        //lig: mol defining coordinates for szmap calcs
        //prot: context mol for szmap calcs (must have charges and radii)

        OEFloatArray coord = new OEFloatArray(3);

        OESzmapEngine sz = new OESzmapEngine(prot);
        OESzmapResults result = new OESzmapResults();

        OEGraphMol points = new OEGraphMol();
        String title = "points " + lig.GetTitle();
        points.SetTitle(title);

        OEMol probes = new OEMol();

        int i = 0;
        for(OEAtomBase atom:  lig.GetAtoms()) {
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

        OEInterface itf = new OEInterface(InterfaceData, "SzmapCalcs", argv);

        String ligFile = itf.GetString("-l");
        oemolistream lims = new oemolistream();
        if (! lims.open(ligFile)) {
            oechem.OEThrow.Fatal("Unable to open "+ ligFile + " for reading");
        }
        OEGraphMol lig = new OEGraphMol();

        oechem.OEReadMolecule(lims, lig);

        String contextFile = itf.GetString("-p");
        oemolistream pims = new oemolistream();
        if (! pims.open(contextFile)) {
            oechem.OEThrow.Fatal("Unable to open " + contextFile + " for reading");
        }
        OEGraphMol prot = new OEGraphMol();

        oechem.OEReadMolecule(pims, prot);

        runSzmap(lig, prot);

        GetSzmapEnergies(lig, prot);

        String outputFile = itf.GetString("-o");
        oemolostream oms = new oemolostream();
        if (! oms.open(outputFile)) {
            oechem.OEThrow.Fatal("Unable to open "+ outputFile + " for writing");
        }
        GenerateSzmapProbes(oms, itf.GetDouble("-prob"), lig, prot);
    }
}
