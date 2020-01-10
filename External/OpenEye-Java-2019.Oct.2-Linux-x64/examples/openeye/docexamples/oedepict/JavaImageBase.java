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

import java.net.URL;
import java.util.*;
import java.io.IOException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

import openeye.oechem.*;
import openeye.oedepict.*;

public class JavaImageBase {

    public static void main(String[] argv) {
        OEInterface itf = null;

        try {
            URL fileURL = JavaImageBase.class.getResource("JavaImageBase.txt");
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            System.err.println("Unable to open interface file");
            System.exit(1);
        }

        oedepict.OEConfigureImageWidth (itf, 400.0);
        oedepict.OEConfigureImageHeight(itf, 400.0);
        oedepict.OEConfigure2DMolDisplayOptions(itf);

        if (!oechem.OEParseCommandLine(itf, argv, "JavaImageBase")) {
            oechem.OEThrow.Fatal("Unable to interpret command line!");
        }

        String oname = itf.GetString("-out");
        String ext = oechem.OEGetFileExtension(oname);
        if (!oedepict.OEIsRegisteredImageFile(ext)) {
            oechem.OEThrow.Fatal("Unknown image type!");
        }

        double width  = oedepict.OEGetImageWidth(itf);
        double height = oedepict.OEGetImageHeight(itf);

        BufferedImage bufferedImage = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
        OEJavaImageBase image = new OEJavaImageBase(bufferedImage.getGraphics(), width, height);

        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream();
        String iname = itf.GetString("-in");
        if (!ifs.open(iname)) {
            oechem.OEThrow.Fatal("Cannot open " + iname + " input file!");
        }
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();
        oedepict.OEPrepareDepiction(mol, false, true);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions();
        oedepict.OESetup2DMolDisplayOptions(opts, itf);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        oedepict.OERenderMolecule(image, disp);

        try {
            File f = new File(oname);
            ImageIO.write(bufferedImage, ext, f);
        } catch(IOException e) {
            System.err.println("Unable to save output file: " + e.getMessage());
            System.exit(1);
        }
    }
}
