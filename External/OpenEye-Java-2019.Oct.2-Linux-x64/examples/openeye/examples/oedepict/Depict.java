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
 * Generates 2D coordinates
 ****************************************************************************/
package openeye.examples.oedepict;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class Depict {

    public static void main(String argv[]) {
        URL fileURL = Depict.class.getResource("Depict.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedepict.OEConfigurePrepareDepictionOptions(itf, OEPrepareDepictionSetup.SuppressHydrogens |
                                                             OEPrepareDepictionSetup.DepictOrientation);

            if (!oechem.OEParseCommandLine(itf, argv, "Depict"))
                oechem.OEThrow.Fatal("Unable to interpret command line!");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-in")))
                oechem.OEThrow.Fatal("Cannot open input file!");

            oemolostream ofs = new oemolostream(".sdf");
            if (itf.HasString("-out")) {
                String oname = itf.GetString("-out");
                if (!ofs.open(oname))
                    oechem.OEThrow.Fatal("Cannot open output file!");
                if (!oechem.OEIs2DFormat(ofs.GetFormat()))
                    oechem.OEThrow.Fatal("Invalid output format for 2D coordinates");
            }

            if (itf.HasString("-ringdict")) {
                String rdfname = itf.GetString("-ringdict");
                if (!oechem.OEInit2DRingDictionary(rdfname))
                    oechem.OEThrow.Warning("Cannot use user-defined ring dictionary!");
            }

            OEPrepareDepictionOptions popts = new OEPrepareDepictionOptions();
            oedepict.OESetupPrepareDepictionOptions(popts, itf);
            popts.SetClearCoords(true);

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                oedepict.OEPrepareDepiction(mol, popts);
                oechem.OEWriteMolecule(ofs, mol);
            }
            ifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
