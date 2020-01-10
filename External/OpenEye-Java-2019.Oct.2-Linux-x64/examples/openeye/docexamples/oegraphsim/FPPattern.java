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

public class FPPattern {

    public static void main(String argv[]) {

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "NCC(=O)[O-]");

        OEFPTypeBase fptype = oegraphsim.OEGetFPType("Tree,ver=2.0.0,size=4096,bonds=0-4,atype=AtmNum|Arom|FCharge|HvyDeg,btype=Order");

        int idx = 0;
        for (OEFPPattern pattern : oegraphsim.OEGetFPPatterns(mol, fptype)) {
            idx++;
            System.out.printf("%2d %5d %50s", idx, pattern.GetBit(), pattern.GetSmarts());
            for (OEAtomBase a : pattern.GetAtoms()) {
                System.out.printf("%d %s", a.GetIdx(), oechem.OEGetAtomicSymbol(a.GetAtomicNum()));
            }
            System.out.printf("\n");
        }
    }
}
