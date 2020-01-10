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
 * Program to convert from one molecule format to another
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class Convert {

    public static void processFile(oemolistream ifs, oemolostream ofs) {
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEWriteMolecule(ofs, mol);
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("Convert <infile> <outfile>");
        }

        oemolistream ifs = new oemolistream();
        oemolostream ofs = new oemolostream();

        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        if (!ofs.open(argv[1])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");
        }

        processFile(ifs, ofs);
        ofs.close(); // make sure to flush output stream
    }
}
