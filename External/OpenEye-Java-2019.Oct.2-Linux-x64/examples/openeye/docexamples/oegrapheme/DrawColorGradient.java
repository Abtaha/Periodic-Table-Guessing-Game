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
package openeye.docexamples.oegrapheme;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

public class DrawColorGradient {
    public static void main(String argv[]) {
        OELinearColorGradient colorg = new OELinearColorGradient();
        colorg.AddStop(new OEColorStop( 0.0, oechem.getOEYellow()));
        colorg.AddStop(new OEColorStop(+1.0, oechem.getOEOrange()));
        colorg.AddStop(new OEColorStop(-1.0, oechem.getOEGreen()));

        OEImage image = new OEImage(400, 100);
        oegrapheme.OEDrawColorGradient(image, colorg);
        oedepict.OEWriteImage("DrawColorGradient.png", image);
    }
}
