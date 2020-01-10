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

public class MakeReceptorWithHintXYZ {

    public static void main(String argv[]) {
        if (argv.length != 4) {
            oechem.OEThrow.Usage("MakeReceptorWithHintXYZ <protein> <hint x> <hint y> <hint z>");
        }
        OEGraphMol protein = new OEGraphMol();
        oemolistream imstr = new oemolistream(argv[0]);
        oechem.OEReadMolecule(imstr, protein);
        imstr.close();

        float hintX = Float.valueOf(argv[1].trim()).floatValue();
        float hintY = Float.valueOf(argv[2].trim()).floatValue();
        float hintZ = Float.valueOf(argv[3].trim()).floatValue();

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEMakeReceptor(receptor, protein, hintX, hintY, hintZ);

        oedocking.OEWriteReceptorFile(receptor,"receptor.oeb.gz");

    }
}
