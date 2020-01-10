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

public class FPTanimoto {

    public static void main(String argv[]) {

        OEGraphMol molA = new OEGraphMol();
        oechem.OESmilesToMol(molA, "c1ccc2c(c1)c(c(oc2=O)OCCSC(=N)N)Cl");
        OEFingerPrint fpA = new OEFingerPrint();
        oegraphsim.OEMakeFP(fpA, molA, OEFPType.MACCS166);

        OEGraphMol molB = new OEGraphMol();
        oechem.OESmilesToMol(molB, "COc1cc2ccc(cc2c(=O)o1)NC(=N)N");
        OEFingerPrint fpB = new OEFingerPrint();
        oegraphsim.OEMakeFP(fpB, molB, OEFPType.MACCS166);

        OEGraphMol molC = new OEGraphMol();
        oechem.OESmilesToMol(molC, "COc1c(c2ccc(cc2c(=O)o1)NC(=N)N)Cl");
        OEFingerPrint fpC = new OEFingerPrint();
        oegraphsim.OEMakeFP(fpC, molC, OEFPType.MACCS166);

        System.out.format("Tanimoto(A,B) = %.3f\n", oegraphsim.OETanimoto(fpA, fpB));
        System.out.format("Tanimoto(A,C) = %.3f\n", oegraphsim.OETanimoto(fpA, fpC));
        System.out.format("Tanimoto(B,C) = %.3f\n", oegraphsim.OETanimoto(fpB, fpC));
    }
}
