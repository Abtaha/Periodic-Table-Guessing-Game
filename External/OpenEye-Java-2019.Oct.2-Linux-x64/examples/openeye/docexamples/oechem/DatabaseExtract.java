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
//@ <SNIPPET>
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class DatabaseExtract {
    public static void main(String argv[]) {
        if (argv.length != 3)
            oechem.OEThrow.Usage("DatabaseExtract <input> <output> <title>");

        OEMolDatabase moldb = new OEMolDatabase();
        if (!moldb.Open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0]);

        String ext = oechem.OEGetFileExtension(argv[1]);
        if (moldb.GetFormat() != oechem.OEGetFileType(ext))
            oechem.OEThrow.Fatal("Output format does not match input format: " +
                                 oechem.OEGetFileExtension(argv[0]) + " != " + ext);
        
        oemolostream ofs = new oemolostream();
        if (!ofs.open(argv[1]))
            oechem.OEThrow.Fatal("Unable to open " + argv[1]);

        String title = argv[2];
        for (int idx = 0; idx < moldb.GetMaxMolIdx(); ++idx) {
            if (title.equals(moldb.GetTitle(idx)))
                moldb.WriteMolecule(ofs, idx);
        }

        ofs.close();
    }
}
//@ </SNIPPET>
