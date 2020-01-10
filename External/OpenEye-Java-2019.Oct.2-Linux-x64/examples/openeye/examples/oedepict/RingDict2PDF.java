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
 * Converts a ring dictionary into a multi-page PDF document.
 ****************************************************************************/
package openeye.examples.oedepict;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class RingDict2PDF {

    public static void main(String argv[]) {
        URL fileURL = RingDict2PDF.class.getResource("RingDict2PDF.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedepict.OEConfigureReportOptions(itf);

            if (!oechem.OEParseCommandLine(itf, argv, "RingDict2PDF"))
                oechem.OEThrow.Fatal("Unable to interpret command line!");

            String ifname = itf.GetString("-ringdict");
            String ofname = itf.GetString("-out");

            if (!oechem.OEIsValid2DRingDictionary(ifname))
                oechem.OEThrow.Fatal("Invalid ring dictionary file!");

            OE2DRingDictionary ringdict = new OE2DRingDictionary(ifname);

            OEReportOptions ropts = new OEReportOptions();
            oedepict.OESetupReportOptions(ropts, itf);

            oedepict.OEWrite2DRingDictionaryReport(ofname, ringdict, ropts);

        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
