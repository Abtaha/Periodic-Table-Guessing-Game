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
 * Rename SDF molecules by specified field
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class SDFRename {

    public static void processFile(String field, oemolistream ifs, oemolostream ofs) {
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (oechem.OEHasSDData(mol, field)) {
                mol.SetTitle(oechem.OEGetSDData(mol, field));
            }
            else {
                oechem.OEThrow.Warning("Renaming of molecule " + mol.GetTitle() + " failed; no field " + field);
            }

            oechem.OEWriteMolecule(ofs, mol);
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 3) {
            oechem.OEThrow.Usage("SDFRename <fieldname> <infile> <outfile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[1])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for reading");
        }

        if (!oechem.OEIsSDDataFormat(ifs.GetFormat()))
        {
            oechem.OEThrow.Fatal("Only works for input file formats that support SD data (sdf,oeb,csv)");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(argv[2])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[2] + " for writing");
        }

        processFile(argv[0], ifs, ofs);
        ifs.close();
        ofs.close();
    }
}
