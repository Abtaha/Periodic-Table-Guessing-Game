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
import java.util.*;

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE GRAPHEME DOCUMENTATION
 **************************************************************/

public class OEColorGradientDispOpts {
    public static void depictColorGradient(OELinearColorGradient colorg,
            OEColorGradientDisplayOptions opts,
            String basefilename) {
        int width  = 400;
        int height = 100;

        OEImage image = new OEImage(width, height);
        oegrapheme.OEDrawColorGradient(image, colorg, opts);

        oedepict.OEWriteImage(basefilename+".pdf", image);
        oedepict.OEWriteImage(basefilename+".png", image);
    }

    public static void main(String argv[]) {
        int width  = 400;
        int height = 100;

        OEImage image = new OEImage(width, height);
        OELinearColorGradient colorg = new OELinearColorGradient(new OEColorStop(0.0, (oechem.getOEYellow())));
        colorg.AddStop(new OEColorStop(+1.0, oechem.getOEOrange()));
        colorg.AddStop(new OEColorStop(-1.0, oechem.getOEGreen()));
        OEColorGradientDisplayOptions o = new OEColorGradientDisplayOptions();
        oegrapheme.OEDrawColorGradient(image, colorg, o);
        depictColorGradient(colorg, o, "OEColorGradientDisplayOptions_Default");

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            OEFont font = new OEFont();
            font.SetColor(oechem.getOEDarkGreen());
            font.SetStyle(OEFontStyle.Bold);
            opts.SetColorStopLabelFont(font);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetColorStopLabelFont");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.SetColorStopLabelFontScale(0.5);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetColorStopLabelFontScale");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.SetColorStopPrecision(4);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetColorStopPrecision");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.SetColorStopVisibility(false);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetColorStopVisibility");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.SetBoxRange(-0.5, 0.5);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetBoxRange");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.SetBoxRange(-0.5, 0.5);
            OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.On, 2.0, OEStipple.ShortDash);
            opts.SetBoxRangePen(pen);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetBoxRangePen");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.AddMarkedValue(0.20);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_AddMarkedValue");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            ArrayList<Double> markedvalues = new ArrayList<Double>();
            markedvalues.add(0.20);
            markedvalues.add(0.70);
            markedvalues.add(0.72);
            opts.AddMarkedValues(markedvalues);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_AddMarkedValues");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.AddMarkedValue(0.0);
            ArrayList<Double> markedvalues = new ArrayList<Double>();
            markedvalues.add(0.75);
            markedvalues.add(0.50);
            opts.SetMarkedValues(markedvalues);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetMarkedValues");
        }

        {
            OEColorGradientDisplayOptions opts = new  OEColorGradientDisplayOptions();
            ArrayList<Double> markedvalues = new ArrayList<Double>();
            markedvalues.add(0.20);
            markedvalues.add(0.70);
            markedvalues.add(0.72);
            opts.SetMarkedValues(markedvalues);
            opts.SetMarkedValuePrecision(1);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetMarkedValuePrecision");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            ArrayList<Double> markedvalues = new ArrayList<Double>();
            markedvalues.add(0.20);
            markedvalues.add(0.70);
            markedvalues.add(0.72);
            opts.SetMarkedValues(markedvalues);
            OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEDarkGreen(), OEFill.On, 2.0, OEStipple.ShortDash);
            opts.SetMarkedValuePen(pen);
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_SetMarkedValuePen");
        }

        {
            OEColorGradientDisplayOptions opts = new OEColorGradientDisplayOptions();
            opts.AddLabel(new OEColorGradientLabel(-1.0, "dissimilar"));
            opts.AddLabel(new OEColorGradientLabel(+1.0, "similar"));
            oegrapheme.OEDrawColorGradient(image, colorg, opts);
            depictColorGradient(colorg, opts, "OEColorGradientDisplayOptions_AddLabel");
        }

    }
}

