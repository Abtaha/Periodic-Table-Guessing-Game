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
 * Depicts the interactions of an active site
 ****************************************************************************/

package openeye.examples.oegrapheme;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import openeye.oebio.*;
import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

public class Complex2Img {

    public static void main(String argv[]) {
        URL fileURL = Complex2Img.class.getResource("Complex2Img.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        oedepict.OEConfigureImageWidth (itf, 900.0);
        oedepict.OEConfigureImageHeight(itf, 600.0);
        oedepict.OEConfigure2DMolDisplayOptions(itf, OE2DMolDisplaySetup.AromaticStyle);
        oebio.OEConfigureSplitMolComplexOptions(itf, OESplitMolComplexSetup.LigName);

        if (!oechem.OEParseCommandLine(itf, argv, "Complex2Img"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        String iname = itf.GetString("-complex");
        String oname = itf.GetString("-out");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(iname))
            oechem.OEThrow.Fatal("Cannot open input file!");

        String ext = oechem.OEGetFileExtension(oname);
        if (!oedepict.OEIsRegisteredImageFile(ext))
            oechem.OEThrow.Fatal("Unknown image type!");

        oeofstream ofs = new oeofstream();
        if (!ofs.open(oname))
            oechem.OEThrow.Fatal("Cannot open output file!");
        ofs.close();

        OEGraphMol complexmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ifs, complexmol))
            oechem.OEThrow.Fatal("Unable to read molecule from " + iname);
        ifs.close();

        if (!oechem.OEHasResidues(complexmol))
            oechem.OEPerceiveResidues(complexmol, OEPreserveResInfo.All);

        // Separate ligand and protein

        OESplitMolComplexOptions sopts = new OESplitMolComplexOptions();
        oebio.OESetupSplitMolComplexOptions(sopts, itf);

        OEGraphMol ligand = new OEGraphMol();
        OEGraphMol protein = new OEGraphMol();
        OEGraphMol water = new OEGraphMol();
        OEGraphMol other = new OEGraphMol();

        OEUnaryRoleSetPred pfilter = sopts.GetProteinFilter();
        OEUnaryRoleSetPred wfilter = sopts.GetWaterFilter();
        sopts.SetProteinFilter(new OEOrRoleSet(pfilter, wfilter));
        sopts.SetWaterFilter(oebio.OEMolComplexFilterFactory(OEMolComplexFilterCategory.Nothing));

        oebio.OESplitMolComplex(ligand, protein, water, other, complexmol, sopts);

        if (ligand.NumAtoms() == 0)
            oechem.OEThrow.Fatal("Cannot separate complex!");

        // Perceive interactions

        OEInteractionHintContainer asite = new OEInteractionHintContainer(protein, ligand);
        if (!asite.IsValid())
            oechem.OEThrow.Fatal("Cannot initialize active site!");
        asite.SetTitle(ligand.GetTitle());

        oebio.OEPerceiveInteractionHints(asite);

        oegrapheme.OEPrepareActiveSiteDepiction(asite);

        // Depict active site with interactions

        double imagewidth = oedepict.OEGetImageWidth(itf);
        double imageheight = oedepict.OEGetImageHeight(itf);
        OEImage image = new OEImage(imagewidth, imageheight);

        OEImageFrame cframe = new OEImageFrame(image, imagewidth * 0.80, imageheight,
                                               new OE2DPoint(0.0, 0.0));
        OEImageFrame lframe = new OEImageFrame(image, imagewidth * 0.20, imageheight,
                                               new OE2DPoint(imagewidth * 0.80, 0.0));

        OE2DActiveSiteDisplayOptions opts = new OE2DActiveSiteDisplayOptions(
                cframe.GetWidth(), cframe.GetHeight());
        oedepict.OESetup2DMolDisplayOptions(opts, itf);

        OE2DActiveSiteDisplay adisp = new OE2DActiveSiteDisplay(asite, opts);
        oegrapheme.OERenderActiveSite(cframe, adisp);

        OE2DActiveSiteLegendDisplayOptions lopts = new OE2DActiveSiteLegendDisplayOptions(10, 1);
        oegrapheme.OEDrawActiveSiteLegend(lframe, adisp, lopts);

        oedepict.OEWriteImage(oname, image);
    }
}
