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
 * Depicts the BFactor of a ligand and its environment
 ****************************************************************************/

package openeye.examples.oegrapheme;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import openeye.oebio.*;
import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

public class BFactor2Img {

    public static void main(String argv[]) {
        URL fileURL = BFactor2Img.class.getResource("BFactor2Img.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        oedepict.OEConfigureImageWidth (itf, 600.0);
        oedepict.OEConfigureImageHeight(itf, 600.0);
        oedepict.OEConfigure2DMolDisplayOptions(itf, OE2DMolDisplaySetup.AromaticStyle);
        oebio.OEConfigureSplitMolComplexOptions(itf, OESplitMolComplexSetup.LigName);

        if (!oechem.OEParseCommandLine(itf, argv, "BFactor2Img"))
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

        oebio.OESplitMolComplex(ligand, protein, water, other, complexmol, sopts);

        if (ligand.NumAtoms() == 0)
            oechem.OEThrow.Fatal("Cannot separate complex!");

        double maxdistance = 4.0;

        // Calculate average BFactor of the whole complex
        double avgbfactor = GetAverageBFactor(complexmol);

        //# Calculate minimum and maximum BFactor of the ligand and its environment
        SetMinAndMaxBFactor(ligand, protein, maxdistance);

        DecimalFormat df = new DecimalFormat("0.##");
        oechem.OEThrow.Info("Average BFactor of the complex = " + df.format(avgbfactor));
        oechem.OEThrow.Info("Minimum BFactor of the ligand and its environment = " + df.format(minbfactor));
        oechem.OEThrow.Info("Maximum BFactor of the ligand and its environment = " + df.format(maxbfactor));

        String stag = "avg residue BFactor";
        int itag = oechem.OEGetTag(stag);
        SetAverageBFactorOfNearbyProteinAtoms(ligand, protein, itag, maxdistance);

        // Create image
        double imagewidth = oedepict.OEGetImageWidth(itf);
        double imageheight = oedepict.OEGetImageHeight(itf);
        OEImage image = new OEImage(imagewidth, imageheight);

        OEImageFrame mframe = new OEImageFrame(image, imagewidth, imageheight * 0.90,
                                               new OE2DPoint(0.0, 0.0));
        OEImageFrame lframe = new OEImageFrame(image, imagewidth, imageheight * 0.10,
                                               new OE2DPoint(0.0, imageheight * 0.90));

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(
                mframe.GetWidth(), mframe.GetHeight(), OEScale.AutoScale);
        oedepict.OESetup2DMolDisplayOptions(opts, itf);
        opts.SetAtomColorStyle(OEAtomColorStyle.WhiteMonochrome);

        // Create BFactor color gradi    # Create BFactor color gradient
        OELinearColorGradient colorg = new OELinearColorGradient();
        colorg.AddStop(new OEColorStop(  0.0, oechem.getOEDarkBlue()));
        colorg.AddStop(new OEColorStop( 10.0, oechem.getOELightBlue()));
        colorg.AddStop(new OEColorStop( 25.0, oechem.getOEYellowTint()));
        colorg.AddStop(new OEColorStop( 50.0, oechem.getOERed()));
        colorg.AddStop(new OEColorStop(100.0, oechem.getOEDarkRose()));

        // Prepare ligand for depiction
        oegrapheme.OEPrepareDepictionFrom3D(ligand);
        BFactorArcFxn arcfxn = new BFactorArcFxn(colorg, itag);
        for (OEAtomBase ai : ligand.GetAtoms())
            oegrapheme.OESetSurfaceArcFxn(ligand, ai, arcfxn);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(ligand, opts));

        // Render ligand and visualize Bfactor
        OE2DMolDisplay disp = new OE2DMolDisplay(ligand, opts);
        ColorLigandAtomByBFactor colorbfactor = new ColorLigandAtomByBFactor(colorg);
        oegrapheme.OEAddGlyph(disp, colorbfactor, new OEIsTrueAtom());
        oegrapheme.OEDraw2DSurface(disp);
        oedepict.OERenderMolecule(mframe, disp);

        // Draw color gradient
        OEColorGradientDisplayOptions copts = new OEColorGradientDisplayOptions();
        copts.SetColorStopPrecision(1);
        copts.AddMarkedValue(avgbfactor);
        copts.SetBoxRange(minbfactor, maxbfactor);

        oegrapheme.OEDrawColorGradient(lframe, colorg, copts);

        oedepict.OEWriteImage(oname, image);
    }

    static double GetAverageBFactor(OEMolBase mol) {

        double sumbfactor = 0.0;
        for (OEAtomBase atom : mol.GetAtoms()) {
            OEResidue res = oechem.OEAtomGetResidue(atom);
            sumbfactor += res.GetBFactor();
        }
        double avgbfactor = sumbfactor / mol.NumAtoms();
        return avgbfactor;
    }

    static boolean ConsiderResidueAtom(OEAtomBase atom, OEResidue res) {
        if (atom.GetAtomicNum() == OEElemNo.H)
            return false;
        if (res.GetName().equals("HOH"))
            return false;
        return true;
    }

    static double minbfactor = Double.MAX_VALUE;
    static double maxbfactor = Double.MIN_VALUE;

    static void SetMinAndMaxBFactor(OEMolBase ligand, OEMolBase protein, double maxdistance)
    {
        // Ligand atoms
        for (OEAtomBase latom : ligand.GetAtoms(new OEIsHeavy())) {
            OEResidue res = oechem.OEAtomGetResidue(latom);
            double bfactor = res.GetBFactor();
            minbfactor = Math.min(minbfactor, bfactor);
            maxbfactor = Math.max(maxbfactor, bfactor);
        }

        // Protein atoms close to ligand atoms
        OENearestNbrs nn = new OENearestNbrs(protein, maxdistance);
        for (OEAtomBase latom : ligand.GetAtoms(new OEIsHeavy())) {
            for (OENbrs neigh : nn.GetNbrs(latom)) {
                OEAtomBase ratom = neigh.GetBgn();
                OEResidue res = oechem.OEAtomGetResidue(ratom);
                if (ConsiderResidueAtom(ratom, res))
                {
                    double bfactor = res.GetBFactor();
                    minbfactor = Math.min(minbfactor, bfactor);
                    maxbfactor = Math.max(maxbfactor, bfactor);
                }
            }
        }
    }

    static void SetAverageBFactorOfNearbyProteinAtoms(OEMolBase ligand, OEMolBase protein,
                                                       int itag, double maxdistance) {

        OENearestNbrs nn = new OENearestNbrs(protein, maxdistance);
        for (OEAtomBase latom : ligand.GetAtoms(new OEIsHeavy())) {
            double sumbfactor = 0.0;
            OEAtomBaseVector neighs = new OEAtomBaseVector();
            for (OENbrs neigh : nn.GetNbrs(latom)) {
                OEAtomBase ratom = neigh.GetBgn();
                OEResidue res = oechem.OEAtomGetResidue(ratom);
                if (ConsiderResidueAtom(ratom, res))
                {
                    double bfactor = res.GetBFactor();
                    sumbfactor += bfactor;
                    neighs.add(ratom);
                }
            }
            double avgbfactor = 0.0;
            if (neighs.size() != 0)
                avgbfactor = sumbfactor / neighs.size();
            latom.SetDoubleData(itag, avgbfactor);
        }
    }

    private static class ColorLigandAtomByBFactor extends OEAtomGlyphBase {
        private OELinearColorGradient colorg;

        private ColorLigandAtomByBFactor() {
        }

        public ColorLigandAtomByBFactor(OELinearColorGradient cg) {
            colorg = new OELinearColorGradient(cg);
        }

        @Override
        public boolean RenderGlyph(OE2DMolDisplay disp, OEAtomBase atom) {

            OE2DAtomDisplay adisp = disp.GetAtomDisplay(atom);
            if (adisp == null || !adisp.IsVisible())
                return false;

            OEResidue res = oechem.OEAtomGetResidue(atom);
            double bfactor = res.GetBFactor();
            OEColor color = new OEColor(colorg.GetColorAt(bfactor));
            OEPen pen = new OEPen(color, color, OEFill.On, 1.0);
            double radius = disp.GetScale() / 3.0;

            OEImage layer = disp.GetLayer(OELayerPosition.Below);
            oegrapheme.OEDrawCircle(layer, OECircleStyle.Default,
                    adisp.GetCoords(), radius, pen);

            return true;
        }

        @Override
        public OEAtomGlyphBase CreateCopy() {
            ColorLigandAtomByBFactor copy = new ColorLigandAtomByBFactor(colorg);
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    private static class BFactorArcFxn extends OESurfaceArcFxnBase {
        private OELinearColorGradient colorg;
        private int itag;

        private BFactorArcFxn() {
        }

        public BFactorArcFxn(OELinearColorGradient cg, int i) {
            colorg = new OELinearColorGradient(cg);
            itag = i;
        }

        @Override
        public boolean constCall(OEImageBase image, OESurfaceArc arc) {
            OE2DAtomDisplay adisp = arc.GetAtomDisplay();
            if (adisp == null || !adisp.IsVisible())
                return false;

            OEAtomBase atom = adisp.GetAtom();
            if (atom == null)
                return false;

            double avgresiduebfactor = atom.GetDoubleData(itag);
            if (avgresiduebfactor == 0.0)
                return true;

            OEColor color = new OEColor(colorg.GetColorAt(avgresiduebfactor));
            OEPen pen = new OEPen(color, color, OEFill.Off, 5.0);
            OE2DPoint center = arc.GetCenter();
            double bAngle = arc.GetBgnAngle();
            double eAngle = arc.GetEndAngle();
            double radius = arc.GetRadius();
            oegrapheme.OEDrawDefaultSurfaceArc(image, center, bAngle, eAngle,
                    radius, pen);

            return true;
        }

        @Override
        public OESurfaceArcFxnBase CreateCopy() {
            BFactorArcFxn copy = new BFactorArcFxn(colorg, itag);
            copy.swigReleaseOwnership();
            return copy;
        }
    }

}
