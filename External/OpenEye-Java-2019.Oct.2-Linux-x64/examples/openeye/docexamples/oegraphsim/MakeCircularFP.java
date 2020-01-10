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

public class MakeCircularFP {

    public static void main(String argv[]) {

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1ccccc1");

        OEFingerPrint fp = new OEFingerPrint();
        oegraphsim.OEMakeCircularFP(fp, mol);

        System.out.println(fp.GetFPTypeBase().GetFPTypeString());

        oegraphsim.OEMakeFP(fp, mol, OEFPType.Circular);

        System.out.println(fp.GetFPTypeBase().GetFPTypeString());

        int numbits  = 1024;
        int minradius = 0;
        int maxradius = 3;
        oegraphsim.OEMakeCircularFP(fp, mol, numbits, minradius, maxradius,
                OEFPAtomType.DefaultCircularAtom,
                OEFPBondType.DefaultCircularBond);

        System.out.println(fp.GetFPTypeBase().GetFPTypeString());
    }
}
