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

public class FPPathLength {

    static void PrintTanimoto(OEMolBase molA, OEMolBase molB, int minb, int maxb) {
        OEFingerPrint fpA = new OEFingerPrint();
        OEFingerPrint fpB = new OEFingerPrint();
        int numbits  = 2048;
        int atype = OEFPAtomType.DefaultAtom;
        int btype = OEFPBondType.DefaultBond;
        oegraphsim.OEMakePathFP(fpA, molA, numbits, minb, maxb, atype, btype);
        oegraphsim.OEMakePathFP(fpB, molB, numbits, minb, maxb, atype, btype);
        System.out.format("Tanimoto(A,B) = %.3f\n", oegraphsim.OETanimoto(fpA, fpB));
    }

    public static void main(String argv[]) {

        OEGraphMol molA = new OEGraphMol();
        oechem.OESmilesToMol(molA, "c1ccncc1");
        OEGraphMol molB = new OEGraphMol();
        oechem.OESmilesToMol(molB, "c1cc[nH]c1");

        PrintTanimoto(molA, molB, 0, 3);
        PrintTanimoto(molA, molB, 1, 3);
        PrintTanimoto(molA, molB, 0, 4);
        PrintTanimoto(molA, molB, 0, 5);
    }
}
