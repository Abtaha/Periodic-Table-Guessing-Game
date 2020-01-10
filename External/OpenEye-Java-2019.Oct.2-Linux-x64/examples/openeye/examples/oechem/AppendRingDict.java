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
 * Appends rings to an existing 2D rings dictionary
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;

public class AppendRingDict {

    public static void main(String argv[]) {
        URL fileURL = AppendRingDict.class.getResource("AppendRingDict.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);

            if (!oechem.OEParseCommandLine(itf, argv, "AppendRingDict"))
                oechem.OEThrow.Fatal("Unable to interpret command line!");

            String ifname = itf.GetString("-in");
            String irdfname = itf.GetString("-inringdict");
            String ordfname = itf.GetString("-outringdict");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(ifname))
                oechem.OEThrow.Fatal("Cannot open input file for reading!");

            if (!oechem.OEIs2DFormat(ifs.GetFormat()))
                oechem.OEThrow.Fatal("Invalid input file format for 2D coordinates!");

            if (!oechem.OEIsValid2DRingDictionary(irdfname))
                oechem.OEThrow.Fatal("Invalid ring dictionary file!");

            OE2DRingDictionary ringdict = new OE2DRingDictionary(irdfname);

            int nrrings = ringdict.NumRings();

            OEDots dots = new OEDots(10000, 100, "molecules");

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                dots.Update();
                ringdict.AddRings(mol);
            }

            dots.Total();

            int nrnewrings = ringdict.NumRings() - nrrings;
            oechem.OEThrow.Info(Integer.toString(nrnewrings) + " new ring templates have been added!");

            oechem.OEWrite2DRingDictionary(ordfname, ringdict);

            ifs.close();

        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
