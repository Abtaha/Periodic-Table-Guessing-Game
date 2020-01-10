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
package openeye.docexamples.oedepict;

import openeye.oechem.*;
import openeye.oedepict.*;

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
 **************************************************************/

public class PrepareDepiction {

    private static boolean prepareDepiction(OEMolBase mol, boolean clearcoords, boolean suppressH) {

        oechem.OESetDimensionFromCoords(mol);
        oechem.OEPerceiveChiral(mol);
        if (mol.GetDimension() != 2 || clearcoords) {
            if (mol.GetDimension() == 3) {
                oechem.OE3DToBondStereo(mol);
                oechem.OE3DToAtomStereo(mol);
            }
            if (suppressH)
                oechem.OESuppressHydrogens(mol);
            oedepict.OEAddDepictionHydrogens(mol);

            oedepict.OEDepictCoordinates(mol);
            oechem.OEMDLPerceiveBondStereo(mol);
        }
        mol.SetDimension(2);
        return true;
    }
    private static boolean prepareDepiction(OEMolBase mol, boolean clearcoords) {
        return prepareDepiction(mol, clearcoords, true);
    }
    private static boolean prepareDepiction(OEMolBase mol) {
        return prepareDepiction(mol, false, true);
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "C[C@H]([C@@H](C(=O)O)N)O");
        prepareDepiction(mol);
        oedepict.OERenderMolecule("PrepareDepiction.png", mol);
    }
}
