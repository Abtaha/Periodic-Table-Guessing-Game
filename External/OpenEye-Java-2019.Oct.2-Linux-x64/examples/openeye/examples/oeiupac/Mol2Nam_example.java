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

public class Mol2Nam_example {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    private void Mol2Nam(OEInterface itf) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-in")))
            oechem.OEThrow.Fatal("Unable to open input file: "+itf.GetString("-in"));

        oemolostream ofs = new oemolostream();
        String outname = null;
        if (itf.HasString("-out")) {
            outname = itf.GetString("-out");
            if (!ofs.open(outname))
                oechem.OEThrow.Fatal("Unable to open output file: "+outname);
        }

        int language = oeiupac.OEGetIUPACLanguage(itf.GetString("-language"));
        int charset  = oeiupac.OEGetIUPACCharSet(itf.GetString("-encoding"));
        short [] style = oeiupac.OEGetIUPACNamStyle(itf.GetString("-style"));

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            String name = oeiupac.OECreateIUPACName(mol, style);

            if (language > 0)
                name = oeiupac.OEToLanguage(name, language);
            if (itf.GetBool("-capitalize"))
                name = oeiupac.OECapitalizeName(name);

            if (charset == OECharSet.ASCII)
                name = oeiupac.OEToASCII(name);
            else if (charset == OECharSet.UTF8)
                name = oeiupac.OEToUTF8(name);
            else if (charset == OECharSet.HTML)
                name = oeiupac.OEToHTML(name);
            else if (charset == OECharSet.SJIS)
                name = oeiupac.OEToSJIS(name);
            else if (charset == OECharSet.EUCJP)
                name = oeiupac.OEToEUCJP(name);

            if (outname != null) {
                if (itf.HasString("-delim"))
                    name = mol.GetTitle() + itf.GetString("-delim") + name;
                if (itf.HasString("-tag"))
                    oechem.OESetSDData(mol, itf.GetString("-tag"), name);
                mol.SetTitle(name);
                oechem.OEWriteMolecule(ofs, mol);
            } else {
                System.out.printf("%s\n", name);
            }
        }
        ifs.close();
        ofs.close();
    }

    public static void main(String argv[]) {
        Mol2Nam_example app = new Mol2Nam_example();
        URL fileURL = app.getClass().getResource("Mol2Nam_example.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "Mol2Nam_example", argv);
            app.Mol2Nam(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
