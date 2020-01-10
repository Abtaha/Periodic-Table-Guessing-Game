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
package openeye.examples.oemolprop;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oemolprop.*;

public class MolPropSDData {

    public static void main(String[] argv) {
        URL fileURL = MolPropSDData.class.getResource("MolPropSDData.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oemolprop.OEConfigureFilterParams(itf);

            if (!oechem.OEParseCommandLine(itf, argv, "MolPropSDData")) {
                oechem.OEThrow.Fatal("Unable to interpret command line!");
            }

            String iname = itf.GetString("-in");
            String oname = itf.GetString("-out");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(iname)) {
                oechem.OEThrow.Fatal("Cannot open input file!");
            }

            oemolostream ofs = new oemolostream();
            if (!ofs.open(oname)) {
                oechem.OEThrow.Fatal("Cannot create output file!");
            }

            int fmt = ofs.GetFormat();
            if (fmt != OEFormat.SDF && fmt != OEFormat.OEB && fmt != OEFormat.CSV)
                oechem.OEThrow.Fatal("Only SD, OEB, and CSV formats preserve SD data");

            int ftype = oemolprop.OEGetFilterType(itf);
            OEFilter filter = new OEFilter(ftype);

            int ver = itf.GetInt("-verbose");
            oechem.OEThrow.SetLevel(ver);

            filter.SetTable(oechem.getOenul());
            filter.SetSDTag(true);

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                filter.call(mol);
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
