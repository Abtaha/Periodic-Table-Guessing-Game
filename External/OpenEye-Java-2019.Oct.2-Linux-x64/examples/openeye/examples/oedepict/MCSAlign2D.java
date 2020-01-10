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
/****************************************************************************
 * Aligns the fit molecules based on their maximum common substructure
 * with the reference molecule.
 ****************************************************************************/
package openeye.examples.oedepict;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class MCSAlign2D {

    public static void main(String argv[]) {
        URL fileURL = MCSAlign2D.class.getResource("MCSAlign2D.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);

            if (!oechem.OEParseCommandLine(itf, argv, "MCSAlign2D"))
                oechem.OEThrow.Fatal("Unable to interpret command line!");

            String rname = itf.GetString("-ref");
            String fname = itf.GetString("-fit");
            String oname = itf.GetString("-out");

            oemolistream rifs = new oemolistream();
            if (!rifs.open(rname))
                oechem.OEThrow.Fatal("Cannot open reference molecule file!");

            OEGraphMol refmol = new OEGraphMol();
            if (!oechem.OEReadMolecule(rifs, refmol))
                oechem.OEThrow.Fatal("Cannot read reference molecule!");
            rifs.close();

            oemolistream fifs = new oemolistream();
            if (!fifs.open(fname))
                oechem.OEThrow.Fatal("Cannot open align molecule file!");

            oemolostream ofs = new oemolostream();
            if (!ofs.open(oname))
                oechem.OEThrow.Fatal("Cannot open output file!");
            if (!oechem.OEIs2DFormat(ofs.GetFormat()))
                oechem.OEThrow.Fatal("Invalid output format for 2D coordinates");

            oedepict.OEPrepareDepiction(refmol);

            OEMCSSearch mcss = new OEMCSSearch(OEMCSType.Approximate);
            int atomexpr = OEExprOpts.DefaultAtoms;
            int bondexpr = OEExprOpts.DefaultBonds;
            mcss.Init(refmol, atomexpr, bondexpr);
            mcss.SetMCSFunc(new OEMCSMaxBondsCompleteCycles());

            oechem.OEWriteConstMolecule(ofs, refmol);

            OEGraphMol fitmol = new OEGraphMol();
            while (oechem.OEReadMolecule(fifs, fitmol)) {
                OEAlignmentResult alignres = new OEAlignmentResult(oedepict.OEPrepareAlignedDepiction(fitmol, mcss));
                if (alignres.IsValid()) {
                    oechem.OEThrow.Info(fitmol.GetTitle() + "  mcs size: " + alignres.NumAtoms());
                    oechem.OEWriteMolecule(ofs, fitmol);
                }
            }
            fifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
