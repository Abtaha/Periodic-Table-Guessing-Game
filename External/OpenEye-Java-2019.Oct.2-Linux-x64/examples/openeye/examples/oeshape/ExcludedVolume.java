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
import java.net.URL;
import java.io.IOException;

public class ExcludedVolume {

    public static void main(String[] args) {
        URL fileURL = ExcludedVolume.class.getResource("ExcludedVolume.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface(fileURL, "ExcludedVolume", args);
        } catch(IOException e) {
            oechem.OEThrow.Fatal("Unable to open itf file");
        }

        // Set up best overlay to the query molecule
        oemolistream qfs = new oemolistream();
        if (!qfs.open(itf.GetString("-q"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-q"));
        }

        OEMol qmol = new OEMol();
        oechem.OEReadMolecule(qfs, qmol);

        // Set up overlap to protein exclusion volume
        oemolistream efs = new oemolistream();
        if (!efs.open(itf.GetString("-e")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-e"));

        OEMol emol = new OEMol();
        oechem.OEReadMolecule(efs, emol);

        OEExactShapeFunc evol = new OEExactShapeFunc();
        evol.SetupRef(emol);

        // open database and output streams
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-d"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-d"));
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-o"))) { 
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o"));
        }

        System.out.println("Title                Combo  Rescore");
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            OEROCSResult res = new OEROCSResult();
            oeshape.OEROCSOverlay(res, qmol, mol);
            OEGraphMol outmol = new OEGraphMol(res.GetOverlayConf());

            // calculate overlap with protein
            OEOverlapResults eres = new OEOverlapResults();
            evol.Overlap(outmol, res);

            float frac = eres.GetOverlap() / eres.GetFitSelfOverlap();
            float rescore = res.GetTanimotoCombo() - frac;

            // attach data to molecule and write it
            oechem.OESetSDData(outmol, "TanimotoCombo", String.format("%5.3f", res.GetTanimotoCombo()));
            oechem.OESetSDData(outmol, "Exclusion Volume", String.format("%5.3f",  eres.GetOverlap()));
            oechem.OESetSDData(outmol, "Fraction Overlap", String.format("%5.3f", frac));
            oechem.OESetSDData(outmol, "Rescore", String.format("%5.3f", rescore));

            oechem.OEWriteMolecule(ofs, outmol);
            String line = String.format("%-20s %5.3f  %5.3f", outmol.GetTitle(), res.GetTanimotoCombo(), rescore);
            System.out.println(line);
        }
        ifs.close();
    }
}

