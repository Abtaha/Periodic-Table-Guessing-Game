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
package openeye.docexamples.oedocking;

import java.util.ArrayList;
import openeye.oechem.*;
import openeye.oedocking.*;

public class RecExtraMoleculesEdit {
    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("RecExtraMoleculesEdit <receptor>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);

        int count = 0;
        ArrayList<OEGraphMol> newExtraMolList = new ArrayList<OEGraphMol>();
        for (OEMolBase extraMol : oedocking.OEReceptorGetExtraMols(receptor)) {
            if (count == 0) {
                oedocking.OEReceptorSetBoundLigand(receptor, extraMol);
            } else {
                newExtraMolList.add(new OEGraphMol(extraMol));
            }
            count += 1;
        }

        oedocking.OEReceptorClearExtraMols(receptor);
        for (OEGraphMol extraMol : newExtraMolList) {
            oedocking.OEReceptorAddExtraMol(receptor, extraMol);
        }
    }
}
