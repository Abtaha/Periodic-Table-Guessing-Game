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
package openeye.docexamples.oequacpac;

import openeye.oechem.*;
import openeye.oequacpac.*;

public class EnumerateTautomersWithOptions {
    public void processFile(oemolistream ifs, oemolostream ofs) {
        OEGraphMol mol = new OEGraphMol();

        OETautomerOptions tautomer_options = new OETautomerOptions();
        tautomer_options.SetMaxTautomersGenerated(4096);
        tautomer_options.SetMaxTautomersToReturn(16);
        tautomer_options.SetCarbonHybridization(true);
        tautomer_options.SetMaxZoneSize(50);
        tautomer_options.SetApplyWarts(true);

        boolean pKaNorm = true;

        while (oechem.OEReadMolecule(ifs, mol)) {
            for (OEMolBase tautomer : oequacpac.OEGetReasonableTautomers(mol, tautomer_options, pKaNorm)) {
                // work with tautomer
                oechem.OEWriteMolecule(ofs, tautomer);
            }
        }
    }

    private void displayUsage() {
        System.err.println("usage: EnumerateTautomersWithOptions <infile> <outfile>");
        System.exit(1);
    }

    public static void main(String argv[]) {
        EnumerateTautomersWithOptions app = new EnumerateTautomersWithOptions();

        if (argv.length != 2)
            app.displayUsage();

        oemolistream ifs = new oemolistream(argv[0]);
        oemolostream ofs = new oemolostream(argv[1]);

        app.processFile(ifs, ofs);

        ifs.close();
        ofs.close();
    }
}
