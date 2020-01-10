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

public class CalcET {

    public static void main(String argv[]) {
        if (argv.length != 2) {
            System.err.println("usage: CalcET <reference> <fitfile>");
            System.exit(1);
        }

        OEGraphMol refmol = new OEGraphMol();
        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            System.err.println("Unable to open for reading: " + argv[0]);
            System.exit(1);
        }
        oechem.OEReadMolecule(ifs, refmol);
        oechem.OEAssignBondiVdWRadii(refmol);
        oechem.OEMMFFAtomTypes(refmol);
        oechem.OEMMFF94PartialCharges(refmol);
        ifs.close();

        OEET et = new OEET();
        et.SetRefMol(refmol);

        System.err.println("dielectric: " + et.GetDielectric());
        System.err.println("inner mask: " + et.GetInnerMask());
        System.err.println("outer mask: " + et.GetOuterMask());
        System.err.println("salt conc : " + et.GetSaltConcentration());
        System.err.println("join      : " + et.GetJoin());

        OEGraphMol fitmol = new OEGraphMol();
        if (!ifs.open(argv[1])) {
            System.err.println("Unable to open for reading: " + argv[1]);
            System.exit(1);
        }
        while (oechem.OEReadMolecule(ifs, fitmol)) {
            oechem.OEAssignBondiVdWRadii(fitmol);
            oechem.OEMMFFAtomTypes(fitmol);
            oechem.OEMMFF94PartialCharges(fitmol);
            System.err.println("Title: " + fitmol.GetTitle() + " ET "
                    + et.Tanimoto(fitmol));
        }
        ifs.close();
        et.delete();
    }
}
