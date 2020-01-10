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

public class ZapAtoms2 {

    public static void main(String argv[]) {
        if (argv.length != 1)  {
            System.err.println("usage: ZapAtoms2 <ligand>");
            System.exit(1);
        }                            

        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream(argv[0]);
        oechem.OEReadMolecule(ifs, mol);
        oechem.OEAssignBondiVdWRadii(mol);
        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);
        ifs.close();

        OEZap zap = new OEZap();
        zap.SetMolecule(mol);

        OEFloatArray apot = new OEFloatArray(mol.GetMaxAtomIdx());
        zap.CalcAtomPotentials(apot);

        OEFloatArray apot2 = new OEFloatArray(mol.GetMaxAtomIdx());
        zap.SetOuterDielectric(zap.GetInnerDielectric());
        zap.CalcAtomPotentials(apot2);

        // find the difference
        int idx=0;
        for (OEAtomBase atom : mol.GetAtoms()) {
            idx = atom.GetIdx();
            float diff=apot.getItem(idx)-apot2.getItem(idx);
            apot.setItem(idx, diff);
        }

        // output
        float energy=0.0f;
        for (OEAtomBase atom : mol.GetAtoms()) {
            energy += atom.GetPartialCharge()*apot.getItem(atom.GetIdx());
        }
        System.out.println("Sum of (Potential * Charge * 0.5) in kT = "+
                energy*0.5f);

        zap.delete();
    }
}
