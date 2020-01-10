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

public class FPOverlap {

    public static void main(String argv[]) {

        OEGraphMol pmol = new OEGraphMol();
        oechem.OESmilesToMol(pmol, "c1cnc2c(c1)CC(CC2O)CF");

        OEGraphMol tmol = new OEGraphMol();
        oechem.OESmilesToMol(tmol, "c1cc2c(cc1)CC(CCl)CC2N");

        OEFPTypeBase fptype = oegraphsim.OEGetFPType("Tree,ver=2.0.0,size=4096,bonds=5-5,atype=AtmNum|HvyDeg|EqHalo,btype=Order");

        int idx = 0;
        for (OEMatchBase match : oegraphsim.OEGetFPOverlap(pmol, tmol, fptype)) {
            idx++;
            System.out.printf("match %2d:", idx);
            for (OEMatchPairAtom mpair : match.GetAtoms()) {
                System.out.printf(" %d%s-%d%s", mpair.getPattern().GetIdx(), oechem.OEGetAtomicSymbol(mpair.getPattern().GetAtomicNum()),
                        mpair.getTarget().GetIdx(), oechem.OEGetAtomicSymbol(mpair.getTarget().GetAtomicNum()));
            }
            System.out.printf("\n");
        }
    }
}
