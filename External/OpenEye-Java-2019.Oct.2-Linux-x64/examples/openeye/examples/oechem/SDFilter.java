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
 * Filter molecules by SD data
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import openeye.oechem.*;

public class SDFilter {

    public static void main(String argv[]) {
        URL fileURL = SDFilter.class.getResource("SDFilter.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "SDFilter", argv);
            if (!(itf.HasDouble("-min") || itf.HasDouble("-max"))) {
                oechem.OEThrow.Fatal("Please set a filter value with -min or -max");
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }
            if (!oechem.OEIsSDDataFormat(ifs.GetFormat()))
            {
                oechem.OEThrow.Fatal("Only works for input file formats that support SD data (sdf,oeb,csv)");
            }

            oemolostream ofs = new oemolostream();
            if (!ofs.open(itf.GetString("-o"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
            }
            if (!oechem.OEIsSDDataFormat(ofs.GetFormat()))
            {
                oechem.OEThrow.Fatal("Only works for output file formats that support SD data (sdf,oeb,csv)");
            }

            String tag = itf.GetString("-tag");

            double minval = Double.MIN_VALUE;
            if (itf.HasDouble("-min")) {
                minval = itf.GetDouble("-min");
            }

            double maxval = Double.MAX_VALUE;
            if (itf.HasDouble("-max")) {
                maxval= itf.GetDouble("-max");
            }

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                if (!oechem.OEHasSDData(mol, tag)) {
                    oechem.OEThrow.Warning("Unable to find " + tag + " on " + mol.GetTitle());
                    continue;
                }
                double tagvalue;
                String value = oechem.OEGetSDData(mol, tag);
                try {
                    tagvalue = Double.valueOf(value.trim()).doubleValue();
                }
                catch (NumberFormatException nfe) {
                    oechem.OEThrow.Warning("Failed to convert (" + value + ") to a number in " + mol.GetTitle());
                    continue;
                }
                if (tagvalue < minval) {
                    continue;
                }
                if (tagvalue > maxval) {
                    continue;
                }

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
