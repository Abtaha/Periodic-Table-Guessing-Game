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
//@ <SNIPPET>
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class NewConfFromCoords {
    public static void main(String argv[]) {
        OEMol mol     = new OEMol();

        OEAtomBase o  = mol.NewAtom(OEElemNo.O);
        OEAtomBase h1 = mol.NewAtom(OEElemNo.H);
        OEAtomBase h2 = mol.NewAtom(OEElemNo.H);

        float Acrds[] = { 0.0f   , 0.0f   , 0.0f,
                0.9584f, 0.0f   , 0.0f,
                -0.2392f, 0.9281f, 0.0f};
        // Grab the default conformer
        OEConfBase Acnf = mol.GetConfs().next();
        Acnf.SetCoords(Acrds);

        float Bcrds[] = { 0.0f   , 0.0f   , 0.0f,
                0.9584f, 0.0f   , 0.0f,
                -0.2392f,-0.9281f, 0.0f};
        OEConfBase Bcnf = mol.NewConf(Bcrds);

        double Aang = Math.toDegrees(oechem.OEGetAngle(Acnf, h1, o, h2));
        System.out.println("1st Water Angle: " + Aang);

        double Bang = Math.toDegrees(oechem.OEGetAngle(Bcnf, h1, o, h2));
        System.out.println("2nd Water Angle: " + Bang);
    }
}
//@ </SNIPPET>
