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

public class RecProtConstraintEdit {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("RecProtConstraintEdit <receptor>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);

        for (OEProteinConstraint constraint : oedocking.OEReceptorGetProteinConstraints(receptor)) {
            System.out.format("Atom %d has a constraint\n", constraint.GetAtom().GetIdx());
        }

        for (OEProteinConstraint cons : oedocking.OEReceptorGetProteinConstraints(receptor)) {
            System.out.format("Protein constraint on atom %d\n", cons.GetAtom().GetIdx());
            System.out.format("  Type : %s\n", oedocking.OEProteinConstraintTypeGetName(cons.GetType()));
            System.out.format("  Name : %s\n", cons.GetName());
        }

        for (OEAtomBase heavyAtom : receptor.GetAtoms(new OENotAtom(new OEIsHydrogen()))) {
            OEProteinConstraint proteinConstraint = new OEProteinConstraint();
            proteinConstraint.SetAtom(heavyAtom);
            proteinConstraint.SetType(OEProteinConstraintType.Contact);
            proteinConstraint.SetName("Example constraint");
            oedocking.OEReceptorSetProteinConstraint(receptor, proteinConstraint);
            break;
        }

        oedocking.OEReceptorClearProteinConstraints(receptor);
    }
}
