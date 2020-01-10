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
package openeye.examples.oequacpac;

import openeye.oechem.*;
import openeye.oequacpac.*;

public class EnumerateTautomers {
    public void processFile(oemolistream ifs, oemolostream ofs) {
        OETautomerOptions tautomerOptions = new OETautomerOptions();
        boolean pKaNorm = true;

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            for (OEMolBase tautomer : oequacpac.OEGetReasonableTautomers(mol, tautomerOptions, pKaNorm)) {
                oechem.OEWriteMolecule(ofs, tautomer);
            }
        }
    }

    private void displayUsage() {
        System.err.println("usage: EnumerateTautomers <mol-infile> <mol-outfile>");
        System.exit(1);
    }

    public static void main(String argv[]) {
        EnumerateTautomers app = new EnumerateTautomers();

        if (argv.length != 2)
            app.displayUsage();

        oemolistream ifs = new oemolistream();
        if (! ifs.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");

        oemolostream ofs = new oemolostream();
        if (! ofs.open(argv[1]))
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");

        app.processFile(ifs, ofs);
    }
}
