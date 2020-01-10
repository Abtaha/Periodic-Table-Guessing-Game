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
/****************************************************************************
* Generates binary fingerprint file for fast fingerprint search
****************************************************************************/

package openeye.examples.oegraphsim;

import java.io.*;
import java.net.URL;
import java.io.IOException;
import java.io.File;

import openeye.oechem.*;
import openeye.oegraphsim.*;

public class MakeFastFP
{
    private static final URL fileURL = MakeFastFP.class.getResource("MakeFastFP.txt");

    public static void main(String[] argv) throws IOException {
        OEInterface itf = new OEInterface();
        oechem.OEConfigureFromURL(itf, fileURL);
        oegraphsim.OEConfigureFingerPrint(itf, oegraphsim.OEGetFPType(OEFPType.Tree));

        if (!oechem.OEParseCommandLine(itf, argv, "MakeFastFP")) {
            oechem.OEThrow.Fatal("Unable to interpret command line!");
        }

        String ifname = itf.GetString("-in");
        String ffname = itf.GetString("-fpdb");

        String ext = oechem.OEGetFileExtension(ffname);
        if (!ext.equals("fpbin"))
            oechem.OEThrow.Fatal("Fingerprint database file should have '.fpbin' file extension!");

        String idxfname = oechem.OEGetMolDatabaseIdxFileName(ifname);

        File idxfile = new File(idxfname);
        if (!idxfile.exists()) {
            if (!oechem.OECreateMolDatabaseIdx(ifname))
                oechem.OEThrow.Warning("Unable to create %s molecule index file" + idxfname);
        }
        oechem.OEThrow.Info("Using %s index molecule file" + idxfname);

        OEMolDatabase moldb = new OEMolDatabase();
        if (!moldb.Open(ifname))
            oechem.OEThrow.Fatal("Cannot open molecule database file!");

        int  nrmols = moldb.GetMaxMolIdx();

        OEFPTypeBase fptype = oegraphsim.OESetupFingerPrint(itf);
        oechem.OEThrow.Info("Using fingerprint type " + fptype.GetFPTypeString());

        OECreateFastFPDatabaseOptions opts = new OECreateFastFPDatabaseOptions(fptype);
        opts.SetTracer(new OEDots(100000, 1000, "fingerprints"));
        oechem.OEThrow.Info("Generating fingerprints with " + opts.GetNumProcessors() + " threads");

        OEWallTimer timer = new OEWallTimer();

        if (!oegraphsim.OECreateFastFPDatabaseFile(ffname, ifname, opts))
            oechem.OEThrow.Fatal("Cannot create fingerprint database file!");

        oechem.OEThrow.Info(String.format("%5.2f", timer.Elapsed()) + " sec to generate " + nrmols + " fingerprints");
   }
}
