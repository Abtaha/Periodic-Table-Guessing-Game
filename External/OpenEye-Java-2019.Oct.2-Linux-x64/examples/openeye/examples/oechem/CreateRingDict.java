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
 * Creates a new 2D ring dictionary
 ****************************************************************************/

package openeye.examples.oechem;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;

public class CreateRingDict {

    public static void main(String argv[]) {
        URL fileURL = CreateRingDict.class.getResource("CreateRingDict.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);

            if (!oechem.OEParseCommandLine(itf, argv, "CreateRingDict"))
                oechem.OEThrow.Fatal("Unable to interpret command line!");

            String ifname = itf.GetString("-in");
            String ofname = itf.GetString("-ringdict");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(ifname))
                oechem.OEThrow.Fatal("Cannot open input file for reading!");

            if (!oechem.OEIs2DFormat(ifs.GetFormat()))
                oechem.OEThrow.Fatal("Invalid input file format for 2D coordinates!");

            oemolostream ofs = new oemolostream();
            if (!ofs.open(ofname))
                oechem.OEThrow.Fatal("Unable to open output file for writing!");

            if (ofs.GetFormat() != OEFormat.OEB)
                oechem.OEThrow.Fatal("Output file has to have OEB format!");

            OE2DRingDictionaryCreatorOptions opts = new OE2DRingDictionaryCreatorOptions();
            opts.SetRetainExistingBuiltInTemplates(itf.GetBool("-retain-built-in"));
            OE2DRingDictionary ringdict = new OE2DRingDictionary(opts);

            OEDots dots = new OEDots(10000, 100, "molecules");

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                dots.Update();
                ringdict.AddRings(mol);
            }
            ifs.close();
            dots.Total();

            int nrrings = ringdict.NumRings();
            oechem.OEThrow.Info(Integer.toString(nrrings) + " number of ring templates have been extracted!");

            if (nrrings != 0)
                oechem.OEWrite2DRingDictionary(ofname, ringdict);

        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
