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
package openeye.docexamples.oegrapheme;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

public class DrawComplexSurfaceUserDef {

    public static OEGraphMol importMolecule (String filename) {

        oemolistream ifs = new oemolistream();
        OEGraphMol mol = new OEGraphMol();
        if (!ifs.open(filename)) {
            oechem.OEThrow.Fatal("Unable to open " + filename + " for reading");
        }

        if (!oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEThrow.Fatal("Unable to read molecule in " + filename);
        }
        ifs.close();
        oechem.OEAssignBondiVdWRadii(mol);
        oechem.OESuppressHydrogens(mol);

        return mol;
    }

    private static void drawSurfaceArc(OEImageBase image, double depth, OESurfaceArc arc, OEPen pen)
    {
        double edgeAngle = 5.0;
        double patternAngle = 5.0;
        double minPatternWidthRatio = 0.05;
        double maxPatternWidthRatio = 0.10 * depth;
        int patternDirection = OEPatternDirection.Outside;
        oegrapheme.OEDrawSunSurfaceArc(image, arc.GetCenter(), arc.GetBgnAngle(), arc.GetEndAngle(),
                arc.GetRadius(), pen, edgeAngle,
                patternDirection, patternAngle,
                minPatternWidthRatio, maxPatternWidthRatio);
    }

    private static class SolventArcFxn extends OESurfaceArcFxnBase
    {
        @Override
        public boolean constCall(OEImageBase image, OESurfaceArc arc)
        {
            OEPen pen = new OEPen(oechem.getOELightGrey(), oechem.getOELightGrey());
            pen.SetLineWidth(0.5);
            oegrapheme.OEDrawDefaultSurfaceArc(image, arc.GetCenter(), arc.GetBgnAngle(),
                    arc.GetEndAngle(), arc.GetRadius(), pen);
            return true;
        }
        @Override
        public OESurfaceArcFxnBase CreateCopy()
        {
            SolventArcFxn copy = new SolventArcFxn();
            copy.swigReleaseOwnership();
            return copy;
        }      
    }

    private static class BuriedArcFxn extends OESurfaceArcFxnBase
    {
        @Override
        public boolean constCall(OEImageBase image, OESurfaceArc arc)
        {
            OEPen pen = new OEPen(oechem.getOEGrey(), oechem.getOEGrey());
            pen.SetLineWidth(2.0);
            oegrapheme.OEDrawDefaultSurfaceArc(image, arc.GetCenter(), arc.GetBgnAngle(),
                    arc.GetEndAngle(), arc.GetRadius(), pen);
            return true;
        }
        @Override
        public OESurfaceArcFxnBase CreateCopy()
        {
            BuriedArcFxn copy = new BuriedArcFxn();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    private static class CavityArcFxn extends OEComplexSurfaceArcFxnBase
    {
        public CavityArcFxn() {}
        public CavityArcFxn(CavityArcFxn rhs) { super(rhs); }
        @Override
        public boolean constCall(OEImageBase image, OESurfaceArc arc)
        {
            OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack());
            pen.SetLineWidth(2.0);
            drawSurfaceArc(image, GetDepth(), arc, pen);
            return true;
        }
        @Override
        public OEComplexSurfaceArcFxnBase CreateComplexCopy()
        {
            CavityArcFxn copy = new CavityArcFxn(this);
            copy.swigReleaseOwnership();
            return copy;
        }
        @Override
        public OESurfaceArcFxnBase CreateCopy()
        {
            CavityArcFxn copy = new CavityArcFxn(this);
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    private static class VoidArcFxn extends OEComplexSurfaceArcFxnBase
    {
        public VoidArcFxn() {}
        public VoidArcFxn(VoidArcFxn rhs) { super(rhs); }
        @Override
        public boolean constCall(OEImageBase image, OESurfaceArc arc)
        {
            OEPen pen = new OEPen(oechem.getOEDarkGrey(), oechem.getOEDarkGrey());
            pen.SetLineWidth(2.0);
            drawSurfaceArc(image, GetDepth(), arc, pen);
            return true;
        }
        @Override
        public OEComplexSurfaceArcFxnBase CreateComplexCopy()
        {
            VoidArcFxn copy = new VoidArcFxn(this);
            copy.swigReleaseOwnership();
            return copy;
        }
        @Override
        public OESurfaceArcFxnBase CreateCopy()
        {
            VoidArcFxn copy = new VoidArcFxn(this);
            copy.swigReleaseOwnership();
            return copy;
        }
    }


    public static void main(String argv[]) {

        if (argv.length != 2) {
            oechem.OEThrow.Usage("DrawComplexSurfaceUserDefined <receptor> <ligand>");
        }

        OEGraphMol receptor = importMolecule(argv[0]);
        OEGraphMol ligand = importMolecule(argv[1]);

        int width  = 450;
        int height = 350;

        SolventArcFxn sfxn = new SolventArcFxn();
        BuriedArcFxn  bfxn = new BuriedArcFxn();
        CavityArcFxn  cfxn = new CavityArcFxn();
        VoidArcFxn    vfxn = new VoidArcFxn();
        oegrapheme.OEAddComplexSurfaceArcsSpecial(ligand, receptor, sfxn, bfxn, cfxn, vfxn);

        oegrapheme.OEPrepareDepictionFrom3D(ligand);
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(ligand, opts));

        OE2DMolDisplay disp = new OE2DMolDisplay(ligand, opts);
        oegrapheme.OEDraw2DSurface(disp);

        oedepict.OERenderMolecule("DrawComplexSurfaceUserDefined.png", disp);
    }
}
