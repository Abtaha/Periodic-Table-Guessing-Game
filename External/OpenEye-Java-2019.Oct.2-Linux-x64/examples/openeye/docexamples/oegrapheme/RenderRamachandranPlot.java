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

import openeye.oebio.*;
import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

/**************************************************************
* USED TO GENERATE CODE SNIPPETS FOR THE GRAPHEME DOCUMENTATION
**************************************************************/

public class RenderRamachandranPlot {

    public static OEGraphMol importProtein(String filename) {

        oemolistream ifs = new oemolistream();
        OEGraphMol mol = new OEGraphMol();
        if (!ifs.open(filename)) {
            oechem.OEThrow.Fatal("Unable to open " + filename + " for reading");
        }

        if (!oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEThrow.Fatal("Unable to read molecule in " + filename);
        }
        ifs.close();
        return mol;
    }

    public static void main(String argv[]) {

        if (argv.length != 2) {
            oechem.OEThrow.Usage("RenderRamachandranPlot <protein>");
        }

        OEGraphMol protein = importProtein(argv[0]);

        {
        OEImage image = new OEImage(800, 600);

        OERamachandranPlot ramaplot = new OERamachandranPlot();
        ramaplot.AddMolecule(protein);

        oegrapheme.OERenderRamachandranPlot(image, ramaplot);
        oedepict.OEDrawCurvedBorder(image, oedepict.getOELightGreyPen(), 10.0);
        oedepict.OEWriteImage("RenderRamachandranPlot.svg", image);
        }

        {
        OEImage image = new OEImage(800, 600);

        OERamachandranPlot ramaplot = new OERamachandranPlot();
        ramaplot.AddMolecule(protein);

        oegrapheme.OERenderRamachandranPlot(image, ramaplot, OERamaType.General);
        oedepict.OEAddInteractiveIcon(image, OEIconLocation.TopRight, 0.50);
        oedepict.OEDrawCurvedBorder(image, oedepict.getOELightGreyPen(), 10.0);
        oedepict.OEWriteImage("RenderRamachandranPlot-General.svg", image);
        }
    }
}
