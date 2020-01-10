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
package openeye.examples.oebio;

import openeye.oechem.*;
import openeye.oebio.*;

public class SeqAlign {

    private OEGraphMol ref;
    private OEGraphMol fit;
    private boolean writeMol = false;
    private oemolostream ofs;

    private void displayUsage() {
        System.err.println("usage: SeqAlign <refmol> <fitmol> [<outmol>]");
        System.exit(1);
    }

    private boolean loadMols(String argv[]) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            System.err.println("Unable to open refmol file:" + argv[0]);
            return false;
        } else {
            ref = new OEGraphMol();
            if (!oechem.OEReadMolecule(ifs, ref)) {
                System.err.println("Unable to load refmol");
                return false;
            }
        }
        oechem.OEPerceiveResidues(ref, OEPreserveResInfo.All);
        ifs.close();
        if (!ifs.open(argv[1])) {
            System.err.println("Unable to open fitmol file:" + argv[1]);
            return false;
        } else {
            fit = new OEGraphMol();
            if (!oechem.OEReadMolecule(ifs, fit)) {
                System.err.println("Unable to load fit");
                return false;
            }
        }
        oechem.OEPerceiveResidues(fit, OEPreserveResInfo.All);
        ifs.close();
        return true;
    }

    private void align() {
        OESequenceAlignment seqA = oebio.OEGetAlignment(ref, fit);
        System.out.println("Alignment of " + fit.GetTitle() + " to "
                + ref.GetTitle());
        System.out.println();
        System.out.println("  Method: "
                + oebio.OEGetAlignmentMethodName(seqA.GetMethod()));
        System.out.println("  Gap   : " + seqA.GetGap());
        System.out.println("  Extend: " + seqA.GetExtend());
        System.out.println("  Score : " + seqA.GetScore());
        System.out.println();
        oebio.OEWriteAlignment(oechem.getOeout(), seqA);

        boolean onlyCAlpha = true;
        boolean overlay = true;
        double rmsd = 0.0;
        if (writeMol) {
            double[] rot = new double[9];
            double[] trans = new double[3];
            rmsd = oebio.OERMSD(ref, fit, seqA, onlyCAlpha, overlay, rot, trans);
            oechem.OERotate(fit, rot);
            oechem.OETranslate(fit, trans);
            oechem.OEWriteMolecule(ofs, fit);
            ofs.close();
        } else {
            rmsd = oebio.OERMSD(ref, fit, seqA, onlyCAlpha, overlay);
        }
        System.out.println("  RMSD =" + rmsd);
    }

    public static void main(String argv[]) {
        SeqAlign app = new SeqAlign();
        if (argv.length <= 1)
            app.displayUsage();
        if (app.loadMols(argv)) {
            if (argv.length == 4) {
                app.writeMol = true;
                app.ofs = new oemolostream(argv[4]);
            }
            app.align();
        }
    }
}
