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

public class DockMolecules {

    private void dock(OEInterface itf) {
        oemolistream imstr = new oemolistream(itf.GetString("-in"));
        oemolostream omstr = new oemolostream(itf.GetString("-out"));

        OEGraphMol receptor = new OEGraphMol();
        if (!oedocking.OEReadReceptorFile(receptor, itf.GetString("-receptor"))) {
            oechem.OEThrow.Fatal("Unable to read receptor");
        }

        int dockMethod = oedocking.OEDockMethodGetValue(itf, "-method");
        int dockResolution = oedocking.OESearchResolutionGetValue(itf, "-resolution");
        OEDock dock = new OEDock(dockMethod, dockResolution);
        dock.Initialize(receptor);

        OEMol mcmol = new OEMol();
        while (oechem.OEReadMolecule(imstr, mcmol)) {
            System.err.format("docking %s\n", mcmol.GetTitle());
            OEGraphMol dockedMol = new OEGraphMol();
            int retCode = dock.DockMultiConformerMolecule(dockedMol, mcmol);
            if (retCode != OEDockingReturnCode.Success) {
                oechem.OEThrow.Error("Docking failed with error code " + oedocking.OEDockingReturnCodeGetName(retCode));
            }
            String sdtag = oedocking.OEDockMethodGetName(dockMethod);
            oedocking.OESetSDScore(dockedMol, dock, sdtag);
            dock.AnnotatePose(dockedMol);
            oechem.OEWriteMolecule(omstr, dockedMol);
        }
        omstr.close();
        imstr.close();
    }

    public static void main(String argv[]) {
        DockMolecules app = new DockMolecules();
        URL fileURL = app.getClass().getResource("DockMolecules.txt");

        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedocking.OEDockMethodConfigure(itf, "-method");
            oedocking.OESearchResolutionConfigure(itf, "-resolution");
            if (oechem.OEParseCommandLine(itf, argv)) {
                app.dock(itf);
            }
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
