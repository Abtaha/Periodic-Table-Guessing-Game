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

import java.util.ArrayList;
import java.util.Collections;
import openeye.oechem.*;
import openeye.oedocking.*;

public class SetOuterContourVolume {

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("SetOuterContourVolume <receptor> <volume>");
        }

        OEGraphMol receptor = new OEGraphMol();
        if (!oedocking.OEReadReceptorFile(receptor, argv[0])) {
            oechem.OEThrow.Fatal("Unable to open receptor file");
        }

        float outerContourVolume = Float.valueOf(argv[1].trim()).floatValue();

        OEFloatGrid negativeImagePotential = oedocking.OEReceptorGetNegativeImageGrid(receptor);

        ArrayList<Float> gridElement = new ArrayList<Float>();
        for (int i=0;i < negativeImagePotential.GetSize(); i++) {
            gridElement.add(negativeImagePotential.GetValue(i));
        }
        Collections.sort(gridElement, Collections.reverseOrder());

        float outerContourLevel = gridElement.get(gridElement.size()-1);
        float countToVolume = (float)Math.pow(negativeImagePotential.GetSpacing(),3);
        int ilevel = Math.round(outerContourVolume / countToVolume);
        if (ilevel < gridElement.size()) {
            outerContourLevel = gridElement.get(ilevel);
        }

        oedocking.OEReceptorSetOuterContourLevel(receptor, outerContourLevel);

        if (!oedocking.OEWriteReceptorFile(receptor, argv[0])) {
            oechem.OEThrow.Fatal("Unable to write updated receptor");
        }
    }
}
