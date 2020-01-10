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
package openeye.examples.oespicoli;

import java.text.DecimalFormat;

import openeye.oechem.*;
import openeye.oespicoli.*;

public class Volume
{
    public static void main(String [] argv)
    {
        if (argv.length != 2)
        {
            oechem.OEThrow.Usage("Volume <protein> <ligand>");
        }
        oemolistream pifs = new oemolistream();
        if (!pifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading protein.");
        }

        OEGraphMol prot = new OEGraphMol();
        if (!oechem.OEReadMolecule(pifs, prot)) {
            oechem.OEThrow.Fatal("Unable to read protein.");
        }

        oechem.OEAddExplicitHydrogens(prot);
        oechem.OEAssignBondiVdWRadii(prot);

        oemolistream lifs = new oemolistream();
        if (!lifs.open(argv[1])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for reading ligand.");
        }

        OEGraphMol lig = new OEGraphMol();
        if (!oechem.OEReadMolecule(lifs, lig)) {
            oechem.OEThrow.Fatal("Unable to read ligand.");
        }

        oechem.OEAddExplicitHydrogens(lig);
        oechem.OEAssignBondiVdWRadii(lig);

        OEGraphMol comp = new OEGraphMol(prot);
        oechem.OEAddMols(comp, lig);

        OESurface compSurf = new OESurface();
        oespicoli.OEMakeMolecularSurface(compSurf, comp);

        OESurface protSurf = new OESurface();
        oespicoli.OEMakeMolecularSurface(protSurf, prot);

        OESurface ligSurf = new OESurface();
        oespicoli.OEMakeMolecularSurface(ligSurf, lig);

        float compVol = oespicoli.OESurfaceVolume(compSurf);
        float protVol = oespicoli.OESurfaceVolume(protSurf);
        float ligVol  = oespicoli.OESurfaceVolume(ligSurf);
        oespicoli.OEWriteSurface("comp.oesrf", compSurf);
        oespicoli.OEWriteSurface("prot.oesrf", protSurf);
        oespicoli.OEWriteSurface("lig.oesrf", ligSurf);
        DecimalFormat df = new DecimalFormat("#.#");
        oechem.OEThrow.Info(prot.GetTitle() + "-" + lig.GetTitle() + ":" +
                            " dV(C-P) = " + df.format((compVol-protVol)) +
                            " V(L) = " + df.format(ligVol) +
                            " V(C) = " + df.format(compVol) +
                            " V(P) = " + df.format(protVol));
    }
}
