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
package openeye.docexamples.oedepict;

import openeye.oechem.*;
import openeye.oedepict.*;

public class DepictMolConfigure {
    static String InterfaceData =
            "!CATEGORY \"input/output options :\"\n" +
                    "\n" +
                    "  !PARAMETER -in\n" +
                    "    !ALIAS -i\n" +
                    "    !TYPE string\n" +
                    "    !REQUIRED true\n" +
                    "    !BRIEF Input filename\n" +
                    "  !END\n" +
                    "\n" +
                    "  !PARAMETER -out\n" +
                    "    !ALIAS -o\n" +
                    "    !TYPE string\n" +
                    "    !REQUIRED true\n" +
                    "    !BRIEF Output filename\n" +
                    "  !END\n" +
                    "\n" +
                    "!END\n";

    public static void main(String argv[]) {
        OEInterface itf = new OEInterface();
        // import configuration file
        oechem.OEConfigure(itf, InterfaceData);
        // add configuration for image size and display options
        oedepict.OEConfigureImageOptions(itf);
        oedepict.OEConfigure2DMolDisplayOptions(itf);

        if (!oechem.OEParseCommandLine(itf, argv, "DepictMolConfigure")) {
            oechem.OEThrow.Fatal("Unable to interpret command line!");
        }

        String ifname = itf.GetString("-in");
        String ofname = itf.GetString("-out");

        oemolistream ifs = new oemolistream(ifname);
        OEGraphMol mol = new OEGraphMol();
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();
        oedepict.OEPrepareDepiction(mol);

        double width  = oedepict.OEGetImageWidth (itf);
        double height = oedepict.OEGetImageHeight(itf);
        double scale  = OEScale.AutoScale;
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
        // set up display options from command line parameters
        oedepict.OESetup2DMolDisplayOptions(opts, itf);

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        oedepict.OERenderMolecule(ofname, disp);
        return;
    }
}

