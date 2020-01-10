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

import java.net.URL;
import openeye.oechem.*;
import openeye.oedocking.*;

public class PoseMolecules {

    private void pose(OEInterface itf) {
        oemolistream imstr = new oemolistream(itf.GetString("-in"));
        oemolostream omstr = new oemolostream(itf.GetString("-out"));

        OEGraphMol receptor = new OEGraphMol();
        if (!oedocking.OEReadReceptorFile(receptor, itf.GetString("-receptor"))) {
            oechem.OEThrow.Fatal("Unable to read receptor");
        }

        OEPosit poser = new OEPosit();
        poser.Initialize(receptor);

        OEMol mcmol = new OEMol();
        while (oechem.OEReadMolecule(imstr, mcmol)) {
            System.err.format("posing %s\n", mcmol.GetTitle());
            OESinglePoseResult result = new OESinglePoseResult();
            int returnCode = poser.Dock(result, mcmol);

            if (returnCode == OEDockingReturnCode.Success) {
                OEGraphMol posedMol = new OEGraphMol(result.GetPose());
                oedocking.OESetSDScore(posedMol, poser);
                oechem.OEWriteMolecule(omstr, posedMol);
            }
            else {
                oechem.OEThrow.Warning("Docking failed for molecule " +
                    mcmol.GetTitle() + " with return code " + Integer.toString(returnCode));
            }
        }
        omstr.close();
        imstr.close();
    }

    public static void main(String argv[]) {
        PoseMolecules app = new PoseMolecules();
        URL fileURL = app.getClass().getResource("PoseMolecules.txt");

        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            if (oechem.OEParseCommandLine(itf, argv)) {
                app.pose(itf);
            }
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
