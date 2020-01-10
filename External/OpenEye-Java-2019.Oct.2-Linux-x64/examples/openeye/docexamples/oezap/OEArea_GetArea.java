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
package openeye.docexamples.oezap;

import openeye.oechem.*;
import openeye.oezap.*;

public class OEArea_GetArea { 
    public static void main(String[] args) { 
        if (args.length != 1) { 
            oechem.OEThrow.Usage("OEArea_GetArea <molfile>");
        }

        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream();
        if (!ifs.open(args[0])) { 
            oechem.OEThrow.Error("Unable to open " + args[0]);
        }

        if (!oechem.OEReadMolecule(ifs, mol)) { 
            oechem.OEThrow.Error("Unable to read from " + args[0]);
        }
        ifs.close();

        oechem.OEAssignBondiVdWRadii(mol);
        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);

        {
            OEArea area = new OEArea();
            float a = area.GetArea(mol);
            System.out.println("Molecule area = " + a );
        }

        {
            OEFloatArray atomArea = new OEFloatArray(mol.GetMaxAtomIdx()); 
            OEArea area = new OEArea();
            area.GetArea(mol, atomArea);
            for (OEAtomBase atom: mol.GetAtoms()) {
                int index = atom.GetIdx();
                System.out.println(index + " " + atomArea.getItem(index));
            }
        }
    }
}
