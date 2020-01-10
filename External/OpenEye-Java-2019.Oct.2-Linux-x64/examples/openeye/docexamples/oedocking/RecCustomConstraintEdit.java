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

public class RecCustomConstraintEdit {

    private static void SetCustomConstraints(OEGraphMol receptor, OEAtomBase atom) {
        OECustomConstraints customConstraints =
                oedocking.OEReceptorGetCustomConstraints(receptor);

        OEFeature feature = customConstraints.AddFeature();
        feature.SetFeatureName("Example protein contact constraint");

        float sphereRadius = 4.0f;
        float[] sphereCenter = new float[3];
        receptor.GetCoords(atom, sphereCenter);
        OESphereBase sphere = feature.AddSphere();
        sphere.SetRad(sphereRadius);
        sphere.SetCenter(sphereCenter[0], sphereCenter[1], sphereCenter[2]);

        oedocking.OEReceptorSetCustomConstraints(receptor, customConstraints);
    }

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("RecCustomConstraintEdit <receptor>");
        }

        OEGraphMol receptor = new OEGraphMol();
        oedocking.OEReadReceptorFile(receptor, argv[0]);

        if (oedocking.OEReceptorHasCustomConstraints(receptor)) {
            System.out.println("Receptor has custom constraints");
        } else {
            System.out.println("Receptor does not have custom constraints");
        }

        OECustomConstraints customConstraints = oedocking.OEReceptorGetCustomConstraints(receptor);
        for (OEFeature feature : customConstraints.GetFeatures()) {
            System.out.println("Feature " + feature.GetFeatureName());
            System.out.println("  Spheres : ");
            for (OESphereBase sphere : feature.GetSpheres()) {
                System.out.format("    center (%.2f, %.2f, %.2f)  radius  %.2f\n",
                        sphere.GetX(), sphere.GetY(), sphere.GetZ(), sphere.GetRad());
                OEStringIter smartsIter = feature.GetSmarts();
                if (!smartsIter.IsValid()) {
                    System.out.println("  Constraint is matched by any heavy atom");
                } else {
                    System.out.println("  SMARTS:");
                    for (String smarts : smartsIter) {
                        System.out.format("    %s", smarts);
                    }
                    System.out.println("");
                }
            }
        }

        for (OEAtomBase heavyAtom : receptor.GetAtoms()) {
            if (!heavyAtom.IsHydrogen()) {
                SetCustomConstraints(receptor, heavyAtom);
                break;
            }
        }

        oedocking.OEReceptorClearCustomConstraints(receptor);
    }
}
