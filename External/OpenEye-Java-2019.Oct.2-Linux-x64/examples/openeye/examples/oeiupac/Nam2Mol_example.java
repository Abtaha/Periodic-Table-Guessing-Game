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
package openeye.examples.oeiupac;

import java.net.URL;

import openeye.oechem.*;
import openeye.oeiupac.*;

public class Nam2Mol_example {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    private void Nam2Mol(OEInterface itf) {
        oeifstream ifs = new oeifstream();
        if (!ifs.open(itf.GetString("-in")))
            oechem.OEThrow.Fatal("Unable to open input file: "+itf.GetString("-in"));

        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-out")))
            oechem.OEThrow.Fatal("Unable to open output file: "+itf.GetString("-out"));

        int language = oeiupac.OEGetIUPACLanguage(itf.GetString("-language"));
        int charset = oeiupac.OEGetIUPACCharSet(itf.GetString("-charset"));

        OEGraphMol mol = new OEGraphMol();
        StringBuffer name = new StringBuffer();
        while (ifs.getline(name)) {
            mol.Clear();

            // Speculatively reorder CAS permuted index names
            String str = oeiupac.OEReorderIndexName(name.toString());
            if (str.length() == 0)
                str=name.toString();

            if (charset == OECharSet.HTML)
                str = oeiupac.OEFromHTML(str);
            else if (charset == OECharSet.UTF8)
                str = oeiupac.OEFromUTF8(str);

            str = oeiupac.OELowerCaseName(str);

            if (language != OELanguage.AMERICAN)
                str = oeiupac.OEFromLanguage(str,language);

            boolean done = oeiupac.OEParseIUPACName(mol,str);

            if (!done && itf.GetBool("-empty")) {
                mol.Clear();
                done = true;
            }

            if (done) {
                if (itf.HasString("-tag"))
                    oechem.OESetSDData(mol, itf.GetString("-tag"),name.toString());
                mol.SetTitle(name.toString());
                oechem.OEWriteMolecule(ofs,mol);
            }
        }
        ifs.close();
        ofs.close();
    }

    public static void main(String argv[]) {
        Nam2Mol_example app = new Nam2Mol_example();
        URL fileURL = app.getClass().getResource("Nam2Mol_example.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "Nam2Mol_example", argv);
            app.Nam2Mol(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
