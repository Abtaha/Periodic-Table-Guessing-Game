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

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
 **************************************************************/

public class OE2DMolDisplayOpts {

    private static void DepictMolecules(OE2DMolDisplayOptions opts, String smiles, String basefilename) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, smiles);
        oedepict.OEPrepareDepiction(mol);

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        OEImage image = new OEImage(disp.GetWidth(), disp.GetHeight());
        oedepict.OERenderMolecule(image, disp);

        oedepict.OEWriteImage(basefilename+".png", image);
        oedepict.OEWriteImage(basefilename+".pdf", image);
    }

    public static void main(String argv[]) {
        String smiles = "Cc1cccnc1/C=C/[C@H](C(=O)O)O title";
        String smiles_pgroup = "c1ccc(cc1)COC(=O)C(CCOC2CCCCO2)N title";

        {
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions();
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_Default");
        }

        /***********************************************************************
         * AROMATIC
         ***********************************************************************/

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAromaticStyle(OEAromaticStyle.Circle);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAromaticStyle");
        }

        /***********************************************************************
         * ATOM
         ***********************************************************************/

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAtomColor(OEElemNo.O, new OEColor(80, 0, 0)); // very dark red
            opts.SetAtomColor(OEElemNo.N, new OEColor(0, 0, 80)); // very dark blue
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomColor");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAtomColorStyle(OEAtomColorStyle.BlackMonochrome);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomColorStyle");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            OEFont font = new OEFont();
            font.SetFamily(OEFontFamily.Courier);
            font.SetStyle(OEFontStyle.Bold);
            opts.SetAtomLabelFont(font);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomLabelFont");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAtomLabelFontScale(1.5);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomLabelFontScale");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            OEFont font = new OEFont();
            font.SetStyle(OEFontStyle.Bold);
            font.SetColor(oechem.getOEDarkGreen());
            opts.SetAtomPropLabelFont(font);
            opts.SetAtomPropertyFunctor(new OEDisplayAtomIdx());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomPropLabelFont");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAtomPropLabelFontScale(1.5);
            opts.SetAtomPropertyFunctor(new OEDisplayAtomIdx());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomPropLabelFontScale");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAtomPropertyFunctor(new OEDisplayAtomIdx());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomPropertyFunctor");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAtomStereoStyle(OEAtomStereoStyle.Display.All|OEAtomStereoStyle.HashWedgeStyle.Standard);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomStereoStyle");
        }

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetAtomVisibilityFunctor(new OEAtomIsInRing());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetAtomVisibilityFunctor");
        }
        /***********************************************************************
         * BACKGROUND
         ***********************************************************************/

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBackgroundColor(oechem.getOEYellowTint());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBackgroundColor");
        }

        /***********************************************************************
         * BOND
         ***********************************************************************/

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBondColorStyle(OEBondColorStyle.Monochrome);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondColorStyle");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            OEFont font = new OEFont();
            font.SetStyle(OEFontStyle.Bold);
            font.SetColor(oechem.getOEDarkGreen());
            opts.SetBondPropLabelFont(font);
            opts.SetBondPropertyFunctor(new OEDisplayBondIdx());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondPropLabelFont");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBondPropLabelFontScale(1.5);
            opts.SetBondPropertyFunctor(new OEDisplayBondIdx());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondPropLabelFontScale");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBondPropertyFunctor(new OEDisplayBondIdx());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondPropertyFunctor");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBondStereoStyle(OEBondStereoStyle.Display.All);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondStereoStyle");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.On, 5.0);
            opts.SetDefaultBondPen(pen);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetDefaultBondPen");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBondLineGapScale(0.5);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondLineGapScale");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBondLineAtomLabelGapScale(2.0);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondLineAtomLabelGapScale");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetBondWidthScaling(true);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetBondWidthScaling");
        }

        /***********************************************************************
         *
         ***********************************************************************/

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetHydrogenStyle(OEHydrogenStyle.ImplicitTerminal);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetHydrogenStyle");
        }

        /***********************************************************************
         * SUPER ATOM
         ***********************************************************************/

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            OEFont font = new OEFont();
            font.SetColor(oechem.getOEDarkRed());
            opts.SetSuperAtomLabelFont(font);
            opts.SetSuperAtomStyle(OESuperAtomStyle.All);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetSuperAtomLabelFont");
        }
        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetSuperAtomStyle(OESuperAtomStyle.All);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetSuperAtomStyle");
        }

        /***********************************************************************
         * PROTECTIVE GROUP
         ***********************************************************************/

       {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            OEFont font = new OEFont();
            font.SetColor(oechem.getOEDarkRed());
            opts.SetProtectiveGroupLabelFont(font);
            opts.SetProtectiveGroupStyle(OEProtectiveGroupStyle.All);
            DepictMolecules(opts, smiles_pgroup, "OE2DMolDisplayOptions_SetProtectiveGroupLabelFont");
       }

       {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetProtectiveGroupStyle(OEProtectiveGroupStyle.All);
            DepictMolecules(opts, smiles_pgroup, "OE2DMolDisplayOptions_SetProtectiveGroupStyle");
       }

         /***********************************************************************
         * TITLE
         ***********************************************************************/

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            OEFont font = new OEFont();
            font.SetStyle(OEFontStyle.Bold);
            font.SetColor(oechem.getOEDarkBrown());
            opts.SetTitleFont(font);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetTitleFont");
        }

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetTitleLocation(OETitleLocation.Bottom);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetTitleLocation");
        }

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetTitleHeight(20.0);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetTitleHeight");
        }

        {
            double width = 300.0;
            double height = 200.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetTitleFontScale(0.5);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetTitleFontScale");
        }

        /***********************************************************************
         * LAYOUT
         ***********************************************************************/

        {
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions();
            opts.SetHeight(150.0);
            opts.SetBackgroundColor(oechem.getOELightGrey());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetHeight");
        }
        {
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions();
            opts.SetWidth(150.0);
            opts.SetBackgroundColor(oechem.getOELightGrey());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetWidth");
        }
        {
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions();
            opts.SetScale(20.0);
            opts.SetBackgroundColor(oechem.getOELightGrey());
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetScale");
        }
        {
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions();
            double width  = 180.0;
            double height = 180.0;
            double scale  = 20.0;
            opts.SetDimensions(width, height, scale);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetDimensions");
        }
        {
            double width = 240.0;
            double height = 180.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetMargin(OEMargin.Bottom, 25.0);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetMargin");
        }
        {
            double width = 240.0;
            double height = 180.0;
            double scale = OEScale.AutoScale;
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
            opts.SetMargins(25.0);
            DepictMolecules(opts, smiles, "OE2DMolDisplayOptions_SetMargins");
        }
    }
}
