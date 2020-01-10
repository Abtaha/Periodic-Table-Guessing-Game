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
 * Counts the number of rotatable bonds in the input compound file and
 * outputs the maximum number of rotomers and the rotomer distribution
 ****************************************************************************/
package openeye.examples.oechem;

import java.util.*;
import openeye.oechem.*;

public class RotorCount {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("RotorCount <infile>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }

        ArrayList<Integer> rotcounts = new ArrayList<Integer>();
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            int nrots = oechem.OECount(mol, new OEIsRotor());
            while (nrots >= rotcounts.size()) {
                rotcounts.add(0);
            }
            int newrots = rotcounts.get(nrots) + 1;
            rotcounts.set(nrots, newrots);
        }
        ifs.close();
        int maxrots = rotcounts.size()-1;
        System.out.println("Max rotors: " + maxrots +
                "\nRotorcount distribution:");
        for (int i = 0; i < rotcounts.size(); ++i) {
            System.out.println("\t" + i + ":\t" + rotcounts.get(i));
        }
    }
}
