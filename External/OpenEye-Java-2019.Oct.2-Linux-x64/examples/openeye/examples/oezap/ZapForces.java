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

public class ZapForces {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("usage: ZapForces <molfile>");
            System.exit(1);
        }

        OEZap zap = new OEZap();
        zap.SetInnerDielectric(1.0f);
        zap.SetGridSpacing(0.5f);

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            System.err.println("Unable to open file for reading: " + argv[0]);
            System.exit(1);
        }
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            System.out.println(String.format("\nForce Components for %s, in kT/Angstrom",
                    mol.GetTitle()));
            System.out.println("Index   Element   -dE/dx    -dE/dy    -dE/dz");

            OEFloatArray forces = new OEFloatArray(mol.GetMaxAtomIdx() * 3);
            oechem.OEAssignBondiVdWRadii(mol);
            oechem.OEMMFFAtomTypes(mol);
            oechem.OEMMFF94PartialCharges(mol);
            zap.SetMolecule(mol);
            zap.CalcForces(forces);
            for (OEAtomBase atom : mol.GetAtoms()) {
                System.out.println(String.format(" %4d     %2s     %7.3f   %7.3f   %7.3f",
                        atom.GetIdx(),
                        oechem.OEGetAtomicSymbol(atom.GetAtomicNum()),
                        forces.getItem(atom.GetIdx()),
                        forces.getItem(atom.GetIdx() + 1),
                        forces.getItem(atom.GetIdx() + 2)));
            }
        }
        zap.delete();
        ifs.close();
    }
}
