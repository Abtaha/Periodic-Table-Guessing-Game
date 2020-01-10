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
 * Extract compound(s) from a file based on molecule title
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class MolExtract {

    private static Set fillTitleSet(OEInterface itf) {
        Set<String> titles = new HashSet<String>();

        if (itf.HasString("-title")) {
            titles.add(itf.GetString("-title"));
        } else if (itf.HasString("-list")) {
            oeifstream ifs = new oeifstream();
            if (!ifs.open(itf.GetString("-list"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-list") + " for reading");
            }
            StringBuffer line = new StringBuffer();
            while (ifs.getline(line)) {
                titles.add(line.toString().trim());
            }
            ifs.close();
        } else {
            oechem.OEThrow.Usage("Must give either -list or -title");
        }
        return titles;
    }

    private static void extract(OEInterface itf) {
        Set titleSet = fillTitleSet(itf);
        if (titleSet.size() == 0) {
            oechem.OEThrow.Fatal("No titles requested");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-i"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-o"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
        }

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (titleSet.contains(mol.GetTitle())) {
                oechem.OEWriteMolecule(ofs, mol);
            }
        }
        ifs.close();
        ofs.close();
    }

    public static void main(String argv[]) {
        URL fileURL = MolExtract.class.getResource("MolExtract.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "MolExtract", argv);
            extract(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
