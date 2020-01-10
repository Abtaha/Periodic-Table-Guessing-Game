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
 * Perform substructure search on molecule file
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class MolGrep {

    public static void subSearch(OEInterface itf, OESubSearch ss,
            oemolistream ifs, oemolostream ofs) {

        boolean reverseflag = itf.GetBool("-r");
        boolean countflag   = itf.GetBool("-c");
        int count = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEPrepareSearch(mol, ss);
            if (ss.SingleMatch(mol) != reverseflag) {
                if (countflag) {
                    count++;
                }
                else {
                    oechem.OEWriteMolecule(ofs, mol);
                }
            }
        }
        if (countflag) {
            System.out.println(count + " matching molecules");
        }
    }

    public static void main(String argv[]) {
        URL fileURL = MolGrep.class.getResource("MolGrep.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "MolGrep", argv);
            if (!(itf.GetBool("-c") ^ itf.HasString("-o"))) {
                oechem.OEThrow.Fatal("Counting (-c) or output (-o) must be specified and are mutually exclusive.");
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }

            oemolostream ofs = new oemolostream();
            if (!itf.GetBool("-c")) {
                if (!ofs.open(itf.GetString("-o"))) {
                    oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
                }
            }

            String smarts = itf.GetString("-p");
            OESubSearch ss = new OESubSearch();
            if (!ss.Init(smarts)) {
                oechem.OEThrow.Fatal("Unable to parse SMARTS: " + smarts);
            }

            subSearch(itf, ss, ifs, ofs);
            ifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
