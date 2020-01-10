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
// Analyze binding site energies with szmaptk

package openeye.examples.oeszmap;

import java.io.*;
import java.net.URL;
import openeye.oechem.*;
import openeye.oeszmap.*;

public class SzmapEnergies {

    // getSzmapEnergies: run szmap at ligand coordinates in the protein context
    // lig: mol defining coordinates for szmap calcs
    // prot: context mol for szmap calcs (must have charges and radii)
    private static void getSzmapEnergies(OEMolBase lig, OEMolBase prot) {
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
        for (OEAtomBase atom: lig.GetAtoms()) {
            lig.GetCoords(atom, coord);

            if (!oeszmap.OEIsClashing(sz, coord)) {
                oeszmap.OECalcSzmapResults(result, sz, coord);

                System.out.format("%2d\t%s\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f%n",
                        i, atom.GetName(),
                        result.GetEnsembleValue(OEEnsemble.NeutralDiffDeltaG),
                        result.GetEnsembleValue(OEEnsemble.PSolv),
                        result.GetEnsembleValue(OEEnsemble.WSolv),
                        result.GetEnsembleValue(OEEnsemble.VDW),
                        result.GetEnsembleValue(OEEnsemble.OrderParam) );
            }
            else {
                System.out.format("%2d\t%s CLASH%n", i, atom.GetName());
            }
            i++;
        }
    }

    public static void main(String[] argv)
    {
        // the protein should have charges and radii but the ligand need not

        URL fileURL = SzmapEnergies.class.getResource("SzmapEnergies.txt");
        int returnCode = 0;
        try {
            OEInterface itf = new OEInterface(fileURL, "SzmapEnergies", argv);

            String ligFile = itf.GetString("-l");
            oemolistream lims = new oemolistream();
            if (! lims.open(ligFile)) {
                oechem.OEThrow.Fatal("Unable to open " + ligFile + " for reading");
            }
            OEGraphMol lig = new OEGraphMol();

            oechem.OEReadMolecule(lims, lig);

            String contextFile = itf.GetString("-p");
            oemolistream pims = new oemolistream();
            if (!pims.open(contextFile)) {
                oechem.OEThrow.Fatal("Unable to open "+ contextFile + " for reading");
            }

            OEGraphMol prot = new OEGraphMol();
            oechem.OEReadMolecule(pims, prot);
            getSzmapEnergies(lig, prot);
        } catch (IOException e) {
            System.err.println("Unable to open interface file: " + e.getMessage());
            returnCode = 1;
        }
        System.exit(returnCode);
    }
}
