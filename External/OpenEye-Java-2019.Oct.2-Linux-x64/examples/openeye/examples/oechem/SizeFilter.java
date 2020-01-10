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
 * Filter out molecules by their molecular weight or heavy atom count
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class SizeFilter {

    public static boolean isBetween(int min, int max, int val) {
        if (val >= min && val <= max) {
            return true;
        }
        return false;
    }

    public static boolean isBetween(double min, double max, double val) {
        if (val >= min && val <= max) {
            return true;
        }
        return false;
    }

    public static boolean isMoleculeInHeavyAtomCountRange(int minhac, int maxhac, OEMol mol) {

        int count = oechem.OECount(mol, new OEIsHeavy());
        return isBetween(minhac, maxhac, count);
    }

    public static boolean isMoleculeInMolWtRange(double minwt, double maxwt, OEMol mol) {

        double molwt = oechem.OECalculateMolecularWeight(mol);
        return isBetween(minwt, maxwt, molwt);
    }

    public static void main(String argv[]) {
        URL fileURL = SizeFilter.class.getResource("SizeFilter.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "SizeFilter", argv);

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }

            oemolostream ofs = new oemolostream(".ism");
            if (itf.HasString("-o")) {
                if (!ofs.open(itf.GetString("-o"))) {
                    oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
                }
            }

            int minhac = Integer.MIN_VALUE;
            if (itf.HasInt("-minhac")) {
                minhac = itf.GetInt("-minhac");
            }

            int maxhac = Integer.MAX_VALUE;
            if (itf.HasInt("-maxhac")) {
                maxhac = itf.GetInt("-maxhac");
            }

            double minwt = Double.MIN_VALUE;
            if (itf.HasDouble("-minwt")) {
                minwt = itf.GetDouble("-minwt");
            }

            double maxwt = Double.MAX_VALUE;
            if (itf.HasDouble("-maxwt")) {
                maxwt = itf.GetDouble("-maxwt");
            }

            OEMol mol = new OEMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                if (!isMoleculeInHeavyAtomCountRange(minhac, maxhac, mol)) {
                    continue;
                }

                if (!isMoleculeInMolWtRange(minwt, maxwt, mol)) {
                    continue;
                }

                oechem.OEWriteMolecule(ofs, mol);
            }
            ofs.close();
            ifs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
