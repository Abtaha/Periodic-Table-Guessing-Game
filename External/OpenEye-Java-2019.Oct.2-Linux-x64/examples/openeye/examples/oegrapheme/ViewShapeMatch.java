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
package openeye.examples.oegrapheme;

import java.net.URL;

import openeye.oechem.OEGraphMol;
import openeye.oechem.OEInterface;
import openeye.oechem.oemolistream;
import openeye.oechem.oechem;

import openeye.oedepict.OE2DMolDisplayOptions;
import openeye.oedepict.OE2DMolDisplaySetup;
import openeye.oedepict.OEAtomColorStyle;
import openeye.oedepict.OEFill;
import openeye.oedepict.OEImage;
import openeye.oedepict.OEImageGrid;
import openeye.oedepict.OEPen;
import openeye.oedepict.OEStipple;
import openeye.oedepict.OETitleLocation;
import openeye.oedepict.oedepict;

import openeye.oegrapheme.OEColorAtomStyle;
import openeye.oegrapheme.OEColorOverlapDisplayOptions;
import openeye.oegrapheme.OEDefaultArcFxn;
import openeye.oegrapheme.OEShapeOverlapDisplay;
import openeye.oegrapheme.OEShapeOverlapDisplayOptions;
import openeye.oegrapheme.OEShapeQueryDisplay;
import openeye.oegrapheme.OEShapeQueryDisplayOptions;
import openeye.oegrapheme.oegrapheme;

import openeye.oeshape.OEColorFFType;
import openeye.oeshape.OEColorForceField;
import openeye.oeshape.oeshape;

public class ViewShapeMatch {

    private static void drawShapeView(OEInterface itf) {
        oemolistream reffs = new oemolistream();
        String refMolFile = itf.GetString("-ref");
        if (!reffs.open(refMolFile)) {
            oechem.OEThrow.Fatal("Unable to open reference mol file: " + refMolFile);
        }
        oemolistream fitfs = new oemolistream();
        String fitMolFile = itf.GetString("-fit");
        if (!fitfs.open(fitMolFile)) {
            oechem.OEThrow.Fatal("Unable to open fit molecule file: " + fitMolFile);
        }

        OEGraphMol refmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(reffs, refmol)) {
            oechem.OEThrow.Fatal("Unable to read reference molecule");
        }
        reffs.close();

        OEGraphMol fitmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(fitfs, fitmol)) {
            oechem.OEThrow.Fatal("Unable to read fit molecule");
        }
        fitfs.close();

        double imagewidth  = oedepict.OEGetImageWidth(itf);
        double imageheight = oedepict.OEGetImageHeight(itf);
        OEImage image = new OEImage(imagewidth, imageheight);

        OEImageGrid grid = new OEImageGrid(image, 2, 2);

        OEColorForceField cff = new OEColorForceField();
        cff.Init(OEColorFFType.ImplicitMillsDean);

        OEPen arcpen = new OEPen(oechem.getOEGrey(), oechem.getOEGrey(), OEFill.Off, 2.0, OEStipple.ShortDash);

        // render reference molecule (query)
        OEShapeQueryDisplayOptions qopts = new OEShapeQueryDisplayOptions();
        oedepict.OESetup2DMolDisplayOptions(qopts, itf);
        qopts.SetAtomColorStyle(OEAtomColorStyle.WhiteCPK);
        qopts.SetColorAtomStyle(OEColorAtomStyle.Hidden);

        OEShapeQueryDisplay disp = new OEShapeQueryDisplay(refmol, cff, qopts);
        oegrapheme.OERenderShapeQuery(grid.GetCell(1, 1), disp);

        qopts.SetSurfaceArcFxn(new OEDefaultArcFxn(arcpen));
        qopts.SetAtomColorStyle(OEAtomColorStyle.WhiteMonochrome);
        qopts.SetColorAtomStyle(OEColorAtomStyle.Circle);

        OEShapeQueryDisplay qdisp = new OEShapeQueryDisplay(refmol, cff, qopts);
        oegrapheme.OERenderShapeQuery(grid.GetCell(1, 2), qdisp);

        // render shape and color overlap
        OEShapeOverlapDisplayOptions sopts = new OEShapeOverlapDisplayOptions();
        oedepict.OESetup2DMolDisplayOptions(sopts, itf);
        sopts.SetQuerySurfaceArcFxn(new OEDefaultArcFxn(arcpen));

        OEColorOverlapDisplayOptions copts = new OEColorOverlapDisplayOptions();
        oedepict.OESetup2DMolDisplayOptions(copts, itf);
        copts.SetQuerySurfaceArcFxn(new OEDefaultArcFxn(arcpen));

        OEShapeOverlapDisplay sdisp = new OEShapeOverlapDisplay(qdisp, fitmol, sopts, copts);
        oegrapheme.OERenderShapeOverlap(grid.GetCell(2, 1), sdisp);
        oegrapheme.OERenderColorOverlap(grid.GetCell(2, 2), sdisp);

        String imgfile = itf.GetString("-out");
        String extension = oechem.OEGetFileExtension(imgfile);
        if (!oedepict.OEIsRegisteredImageFile(extension)) {
            oechem.OEThrow.Fatal("Output file is not a valid image type");
        }

        oedepict.OEWriteImage(imgfile, image);
    }

    public static void main(String[] args) {
        URL fileURL = ViewShapeMatch.class.getResource("ViewShapeMatch.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedepict.OEConfigureImageWidth(itf, 600.0);
            oedepict.OEConfigureImageHeight(itf, 600.0);

            int dispOptions = OE2DMolDisplaySetup.AromaticStyle;
            dispOptions |= OE2DMolDisplaySetup.AtomStereoStyle;
            dispOptions |= OE2DMolDisplaySetup.BondStereoStyle;
            oedepict.OEConfigure2DMolDisplayOptions(itf, dispOptions);

            if (oechem.OECheckHelp(itf, args, "ViewShapeMatch")) {
                return;
            }
            if (!oechem.OEParseCommandLine(itf, args, "ViewShapeMatch")) {
                oechem.OEThrow.Fatal("Unable to interpret command line!");
            }

            drawShapeView(itf);

        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
