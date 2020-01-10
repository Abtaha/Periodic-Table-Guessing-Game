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
package openeye.docexamples.oegraphsim;

import openeye.oechem.*;
import openeye.oegraphsim.*;

public class FPAtomTyping {

    static void PrintTanimoto(OEMolBase molA, OEMolBase molB, int atype, int btype) {
        OEFingerPrint fpA = new OEFingerPrint();
        OEFingerPrint fpB = new OEFingerPrint();
        int numbits  = 2048;
        int minb = 0;
        int maxb = 5;
        oegraphsim.OEMakePathFP(fpA, molA, numbits, minb, maxb, atype, btype);
        oegraphsim.OEMakePathFP(fpB, molB, numbits, minb, maxb, atype, btype);
        System.out.format("Tanimoto(A,B) = %.3f\n", oegraphsim.OETanimoto(fpA, fpB));
    }

    public static void main(String argv[]) {

        OEGraphMol molA = new OEGraphMol();
        oechem.OESmilesToMol(molA, "Oc1c2c(cc(c1)CF)CCCC2");
        OEGraphMol molB = new OEGraphMol();
        oechem.OESmilesToMol(molB, "c1ccc2c(c1)c(cc(n2)CCl)N");

        PrintTanimoto(molA, molB, OEFPAtomType.DefaultAtom, OEFPBondType.DefaultBond);
        PrintTanimoto(molA, molB, OEFPAtomType.DefaultAtom|OEFPAtomType.EqAromatic,
                OEFPBondType.DefaultBond);
        PrintTanimoto(molA, molB, OEFPAtomType.Aromaticity, OEFPBondType.DefaultBond);
        PrintTanimoto(molA, molB, OEFPAtomType.InRing, OEFPBondType.InRing);
    }
}
