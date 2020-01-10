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
 * Generates 2D coordinates using user-defined ring templates
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;

public class Generate2D {

    public static void main(String argv[]) {
        URL fileURL = Generate2D.class.getResource("Generate2D.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);

            if (!oechem.OEParseCommandLine(itf, argv, "Generate2D"))
                oechem.OEThrow.Fatal("Unable to interpret command line!");

            String ifname = itf.GetString("-in");
            String ofname = itf.GetString("-out");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(ifname))
                oechem.OEThrow.Fatal("Cannot open input file for reading!");

            oemolostream ofs = new oemolostream();
            if (!ofs.open(ofname))
                oechem.OEThrow.Fatal("Cannot open output file for writing!");

            if (!oechem.OEIs2DFormat(ofs.GetFormat()))
                oechem.OEThrow.Fatal("Invalid output file format for 2D coordinates!");

            if (itf.HasString("-ringdict")) {
                String rdfname = itf.GetString("-ringdict");

                if (!oechem.OEIsValid2DRingDictionary(rdfname))
                    oechem.OEThrow.Warning("Invalid 2D ring dictionary file!");
                else
                    oechem.OEInit2DRingDictionary(rdfname);
            }

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                oechem.OEGenerate2DCoordinates(mol);
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
