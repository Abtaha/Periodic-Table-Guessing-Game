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

import java.net.URL;
import openeye.oechem.*;
import openeye.oezap.*;

public class ZapAtoms1 {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    private static void Output(OEMolBase mol, OEFloatArray apot, boolean atomtable) {
        System.out.println("Title: " + mol.GetTitle());
        if (atomtable) {
            System.out.println("Atom potentials");
            System.out.println("Index Elem Charge Potential");
        }
        double energy = 0.0f;
        OEAtomBaseIter aiter = mol.GetAtoms();
        while (aiter.hasNext()) {
            OEAtomBase atom = aiter.next();
            energy += atom.GetPartialCharge() * apot.getItem(atom.GetIdx());
            if (atomtable) {
                System.out.println(String.format("%4d   %2s   %4.1f  %8.3f",
                        atom.GetIdx(),
                        oechem.OEGetAtomicSymbol(atom.GetAtomicNum()),
                        atom.GetPartialCharge(),
                        apot.getItem(atom.GetIdx())));
            }
        }
        System.out.println("Sum of (Potential * Charge * 0.5) in kT = " + energy * 0.5f);
    }

    private void Calc(OEInterface itf) {
        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream();
        String infile = itf.GetString("-in");

        if (!ifs.open(infile)) {
            System.err.println("Unable to open for reading: " + infile);
            System.exit(1);
        }
        oechem.OEReadMolecule(ifs, mol);
        oechem.OEAssignBondiVdWRadii(mol);

        if (!itf.GetBool("-file_charges")) {
            oechem.OEMMFFAtomTypes(mol);
            oechem.OEMMFF94PartialCharges(mol);
        }
        ifs.close();

        OEZap zap = new OEZap();
        zap.SetMolecule(mol);
        zap.SetInnerDielectric(itf.GetFloat("-epsin"));
        zap.SetBoundarySpacing(itf.GetFloat("-boundary"));
        zap.SetGridSpacing(itf.GetFloat("-grid_spacing"));

        String calcType = itf.GetString("-calc_type");
        if (calcType.equals("default")) {
            OEFloatArray apot = new OEFloatArray(mol.GetMaxAtomIdx());
            zap.CalcAtomPotentials(apot);
            System.out.println("Before Output");
            Output(mol, apot, itf.GetBool("-atomtable"));
        } else if (calcType.equals("solvent_only")) {
            OEFloatArray apot = new OEFloatArray(mol.GetMaxAtomIdx());
            zap.CalcAtomPotentials(apot);
            OEFloatArray apot2 = new OEFloatArray(mol.GetMaxAtomIdx());
            zap.SetOuterDielectric(zap.GetInnerDielectric());
            zap.CalcAtomPotentials(apot2);
            OEAtomBaseIter aiter = mol.GetAtoms();
            float potdiff;
            while (aiter.hasNext()) {
                OEAtomBase atom = aiter.next();
                potdiff = apot.getItem(atom.GetIdx()) - apot2.getItem(atom.GetIdx());
                apot.setItem(atom.GetIdx(), potdiff);
            }
            Output(mol, apot, itf.GetBool("-atomtable"));
        } else if (calcType.equals("remove_self")) {
            OEFloatArray apot = new OEFloatArray(mol.GetMaxAtomIdx());
            zap.CalcAtomPotentials(apot);
            Output(mol, apot, itf.GetBool("-atomtable"));
        } else if (calcType.equals("coulombic")) {
            float x = oezaplib.OECoulombicSelfEnergy(mol, itf.GetFloat("-epsin"));
            System.out.println("Coulombic Assembly Energy ");
            System.out.println("Sum of {Potential * Charge over all atoms * 0.5} in kT = " + x);
        }
        zap.delete();
    }

    public static void main(String argv[]) {
        ZapAtoms1 app = new ZapAtoms1();
        OEInterface itf = new OEInterface();

        URL fileURL = app.getClass().getResource("ZapAtoms1.txt");
        try {
            oechem.OEConfigureFromURL(itf, fileURL);
            if (oechem.OECheckHelp(itf, argv)) {
                return;
            }
            oechem.OEParseCommandLine(itf, argv);
            app.Calc(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
