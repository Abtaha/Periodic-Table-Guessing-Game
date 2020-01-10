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
 * Extract properties from SD file and save as CSV
 ****************************************************************************/
package openeye.examples.oechem;

import java.io.PrintStream;
import java.util.ArrayList;
import openeye.oechem.*;

public class SDF2CSV {

    public static void sdf2CSV(oemolistream ifs,
            PrintStream csv) {
        ArrayList<String> tagList = new ArrayList<String>();
        OEGraphMol mol = new OEGraphMol();
        // read through once to find all unique tags
        while (oechem.OEReadMolecule(ifs, mol)) {
            for (OESDDataPair dp : oechem.OEGetSDDataPairs(mol)) {
                if (!tagList.contains(dp.GetTag())) {
                    tagList.add(dp.GetTag());
                }
            }
        }

        // output the header row
        csv.print("Title");
        for (String tag : tagList) {
            csv.printf(",%s", tag);
        }
        csv.println();

        // read through again filling rows for each molecule
        ifs.rewind();
        while (oechem.OEReadMolecule(ifs, mol)) {
            csv.printf("%s", mol.GetTitle());
            for (String tag : tagList) {
                csv.printf(",%s", oechem.OEGetSDData(mol, tag));
            }
            csv.println();
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("SDF2CSV <infile> <csvfile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        int fmt = ifs.GetFormat();
        if (fmt != OEFormat.SDF && fmt != OEFormat.OEB) {
            oechem.OEThrow.Fatal("Only works for sdf or oeb input files");
        }

        PrintStream csv;
        try {
            csv = new PrintStream(argv[1]);
            sdf2CSV(ifs, csv);
            csv.close();
            ifs.close();
        }
        catch(java.io.IOException e) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");
        }
    }
}
