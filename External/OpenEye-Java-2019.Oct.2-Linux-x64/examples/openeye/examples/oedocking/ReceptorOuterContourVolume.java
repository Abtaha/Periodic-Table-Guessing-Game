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
package openeye.examples.oedocking;

import openeye.oechem.*;
import openeye.oedocking.*;

public class ReceptorOuterContourVolume {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("ReceptorOuterContourVolume <receptor>");
        }

        OEGraphMol receptor = new OEGraphMol();

        if (!oedocking.OEReadReceptorFile(receptor, argv[0])) {
            oechem.OEThrow.Fatal(argv[0]+" is not a valid receptor.");
        }

        OEFloatGrid negativeImagePotential = oedocking.OEReceptorGetNegativeImageGrid(receptor);
        float outerContourLevel = oedocking.OEReceptorGetOuterContourLevel(receptor);

        int outerCount = 0;
        for (int i=0; i<negativeImagePotential.GetSize();++i) {
            if (negativeImagePotential.GetValue(i) >= outerContourLevel) {
                outerCount += 1;
            }
        }
        double countToVolume = Math.pow(negativeImagePotential.GetSpacing(),3);

        System.err.format("%.2f cubic Angstroms\n", outerCount * countToVolume);
    }
}
