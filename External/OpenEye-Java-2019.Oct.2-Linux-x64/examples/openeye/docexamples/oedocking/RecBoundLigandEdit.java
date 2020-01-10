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

import openeye.oechem.*;
import openeye.oedocking.*;

public class RecBoundLigandEdit {
    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("RecBoundLigandEdit <receptor>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);

        if (oedocking.OEReceptorHasBoundLigand(receptor)) {
            System.out.println("Receptor has a bound ligand");
        } else {
            System.out.println("Receptor does not have bound ligand");
        }

        OEGraphMol ligand = new OEGraphMol();
        if (oedocking.OEReceptorHasBoundLigand(receptor)) {
            ligand = oedocking.OEReceptorGetBoundLigand(receptor);
        }

        oedocking.OEReceptorSetBoundLigand(receptor, ligand);

        oedocking.OEReceptorClearBoundLigand(receptor);
    }
}
