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
package openeye.examples.oezap;

import openeye.oechem.*;
import openeye.oezap.*;

public class ZapFocussing {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    public static void CalcBindingEnergy(OEZap zap, OEGraphMol protein,
            OEGraphMol ligand, OEGraphMol cmplx) {
        OEStopwatch stopwatch = new OEStopwatch();
        stopwatch.Start();

        OEFloatArray ppot = new OEFloatArray(protein.GetMaxAtomIdx());
        zap.SetMolecule(protein);
        zap.CalcAtomPotentials(ppot);
        double proteinEnergy = 0.0;
        for (OEAtomBase atom : protein.GetAtoms()) {
            proteinEnergy += ppot.getItem(atom.GetIdx()) * atom.GetPartialCharge();
        }
        proteinEnergy *= 0.5;

        OEFloatArray lpot = new OEFloatArray(ligand.GetMaxAtomIdx());
        zap.SetMolecule(ligand);
        zap.CalcAtomPotentials(lpot);
        double ligandEnergy = 0.0;
        for (OEAtomBase atom : ligand.GetAtoms()) {
            ligandEnergy += lpot.getItem(atom.GetIdx()) * atom.GetPartialCharge();
        }
        ligandEnergy *= 0.5;

        OEFloatArray cpot = new OEFloatArray(cmplx.GetMaxAtomIdx());
        zap.SetMolecule(cmplx);
        zap.CalcAtomPotentials(cpot);
        double cmplxEnergy = 0.0;
        for (OEAtomBase atom : cmplx.GetAtoms()) {
            cmplxEnergy += cpot.getItem(atom.GetIdx()) * atom.GetPartialCharge();
        }
        cmplxEnergy *= 0.5;

        double energy = cmplxEnergy - ligandEnergy - proteinEnergy;
        float time = stopwatch.Elapsed();

        if (zap.IsFocusTargetSet()) {
            System.out.println("Yes\t\t" + (float) energy + "\t" + time);
        } else {
            System.out.println("No\t\t" + (float) energy + "\t" + time);
        }

    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            System.err.println("usage: ZapFocussing <protein> <ligand>");
            System.exit(1);
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            System.err.println("Unable to open file for reading: " + argv[0]);
            System.exit(1);
        }
        OEGraphMol protein = new OEGraphMol();
        oechem.OEReadMolecule(ifs, protein);

        oechem.OEAssignBondiVdWRadii(protein);
        oechem.OEMMFFAtomTypes(protein);
        oechem.OEMMFF94PartialCharges(protein);

        if (!ifs.open(argv[1])) {
            System.err.println("Unable to open file for reading: " + argv[1]);
            System.exit(1);
        }
        OEGraphMol ligand = new OEGraphMol();
        oechem.OEReadMolecule(ifs, ligand);

        oechem.OEAssignBondiVdWRadii(ligand);
        oechem.OEMMFFAtomTypes(ligand);
        oechem.OEMMFF94PartialCharges(ligand);

        OEGraphMol cmplx = new OEGraphMol(protein);
        oechem.OEAddMols(cmplx, ligand);

        float epsin = 1.0f;
        float spacing = 0.5f;
        OEZap zap = new OEZap();
        zap.SetInnerDielectric(epsin);
        zap.SetGridSpacing(spacing);

        System.out.println("\nBinding Energy and Wall Clock Time for " + protein.GetTitle() + " and " + ligand.GetTitle());
        System.out.println("\nFocussed?\tEnergy(kT)\tTime(s)");

        CalcBindingEnergy(zap, protein, ligand, cmplx);
        zap.SetFocusTarget(ligand);
        CalcBindingEnergy(zap, protein, ligand, cmplx);
        zap.delete();
        ifs.close();
    }
}
