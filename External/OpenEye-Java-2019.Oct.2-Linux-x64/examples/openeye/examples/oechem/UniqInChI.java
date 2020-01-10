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
 * Read molecules and write out the unique ones. Two molecules are considered
 * identical if their InChIs are identical.
 ****************************************************************************/
package openeye.examples.oechem;

import java.util.Set;
import java.util.HashSet;
import openeye.oechem.*;

public class UniqInChI {

    public static void processFile(oemolistream ifs, oemolostream ofs) {
        OEGraphMol mol = new OEGraphMol();

        Set<String> uniqInChIs = new HashSet<String>();
        while (oechem.OEReadMolecule(ifs, mol)) {
            String inchi = oechem.OECreateInChI(mol);
            if (uniqInChIs.add(inchi)) {
                oechem.OEWriteMolecule(ofs, mol);
            }
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("UniqInChI <infile> <outfile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(argv[1])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");
        }

        processFile(ifs, ofs);
        ifs.close();
        ofs.close();
    }
}
