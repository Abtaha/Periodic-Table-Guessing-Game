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
 * Counts molecule (and conformers) in input files
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class MolCount {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    public static void main(String argv[]) {
        URL fileURL = MolCount.class.getResource("MolCount.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "MolCount", argv);

            boolean confs = itf.GetBool("-conf");

            long total = 0;
            long totconfs = 0;
            long nfiles = 0;

            for (String filename : itf.GetStringList("-i")) {
                oemolistream ifs = new oemolistream();
                if (!ifs.open(filename)) {
                    oechem.OEThrow.Warning("Unable to open " + filename + " for reading");
                    continue;
                }
                else {
                    nfiles++;
                    long count = 0;
                    long nconfs = 0;

                    OEMol mol = new OEMol();
                    while (oechem.OEReadMolecule(ifs, mol)) {
                        count++;
                        if (confs) {
                            nconfs += mol.NumConfs();
                        }
                    }
                    ifs.close();
                    System.out.println(filename + " contains " + count + " molecule(s).");
                    if (confs) {
                        System.out.println("Total # of conformers:   " + nconfs);
                        System.out.println("Average # of conformers: " + (float)nconfs / (float)count);
                        System.out.println("-----------------------------------------------------------");
                    }

                    total += count;
                    totconfs += nconfs;
                }
            }
            System.out.println("===========================================================");
            System.out.println("Total " + total + " molecules");
            if (confs && (nfiles > 0) ) {
                System.out.println("Total # of conformers:   " + totconfs);
                System.out.println("Average # of conformers: " + (float)totconfs / (float)total);
            }
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
