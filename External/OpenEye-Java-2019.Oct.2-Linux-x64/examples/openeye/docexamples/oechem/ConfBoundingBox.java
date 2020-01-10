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

public class ConfBoundingBox {
    public static void main(String argv[]) {
        oemolistream ifs = new oemolistream(argv[0]);
        float ctr[] = new float[3];
        float ext[] = new float[3];
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            float box[] = {Float.POSITIVE_INFINITY, 
                    Float.POSITIVE_INFINITY, 
                    Float.POSITIVE_INFINITY, 
                    Float.NEGATIVE_INFINITY, 
                    Float.NEGATIVE_INFINITY, 
                    Float.NEGATIVE_INFINITY};

            for (OEConfBase conf : mol.GetConfs()) {
                oechem.OEGetCenterAndExtents(conf, ctr, ext);
                box[0] = Math.min(box[0], ctr[0] - ext[0]);
                box[1] = Math.min(box[1], ctr[1] - ext[1]);
                box[2] = Math.min(box[2], ctr[2] - ext[2]);
                box[3] = Math.max(box[3], ctr[0] + ext[0]);
                box[4] = Math.max(box[4], ctr[1] + ext[1]);
                box[5] = Math.max(box[5], ctr[2] + ext[2]);
            }

            System.out.println("Bounding box for the conformers of " + mol.GetTitle());
            System.out.printf("Lower Extent: %.3f %.3f %.3f", box[0], box[1], box[2]);
            System.out.printf(" Upper Extent: %.3f %.3f %.3f", box[3], box[4], box[5]);
            System.out.println();
        }
    }
}
//@ </SNIPPET>
