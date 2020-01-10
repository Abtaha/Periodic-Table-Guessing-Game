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
 * Randomly reorder molecules and optionally obtain a random subset
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import openeye.oechem.*;

public class RandomSample {

    public static void LoadDatabase(oemolistream ifs, List<OEMol> mList, int count) {
        int readcount = 0;
        OEMol mol = new OEMol(OEMCMolType.OEDBMCMol);
        while (oechem.OEReadMolecule(ifs, mol)) {
            readcount++;
            mol.Compress();
            mList.add(mol);
            mol = new OEMol();
            if (readcount == count) {
                break;
            }
        }
    }

    public static void WriteDatabase(oemolostream ofs, List<OEMol> mList, int count) {
        int outcount = 0;
        for (OEMol dbmol : mList) {
            dbmol.UnCompress();
            oechem.OEWriteMolecule(ofs, dbmol);
            outcount++;
            if (outcount == count) {
                break;
            }
        }
    }

    public static void RandomizePercent(oemolistream ifs, oemolostream ofs, float percent, Random r) {
        List<OEMol> mList = new ArrayList<OEMol>();
        LoadDatabase(ifs, mList, 0);

        Collections.shuffle(mList, r);

        int size = mList.size();
        int count = (int)(percent * 0.01 * size);
        if (count < 1) {
            count = 1;
        }
        WriteDatabase(ofs, mList, count);
    }

    public static void Randomize(oemolistream ifs, oemolostream ofs, Random r) {
        float wholedb = 100;
        RandomizePercent(ifs, ofs, wholedb, r);
    }

    public static void RandomizeN(oemolistream ifs, oemolostream ofs, int count, Random r) {
        List<OEMol> mList = new ArrayList<OEMol>();
        LoadDatabase(ifs, mList, count);

        int readcount = 0;
        OEMol mol = new OEMol(OEMCMolType.OEDBMCMol);
        while (oechem.OEReadMolecule(ifs, mol)) {
            if ((float)count/(count + readcount + 1) > r.nextFloat()) {
                int idx = (int) ((float)count * r.nextFloat());
                mol.Compress();
                mList.set(idx, mol);
                mol = new OEMol();
            }
            readcount++;
        }

        Collections.shuffle(mList, r);
        WriteDatabase(ofs, mList, count);
    }

    public static void main(String argv[]) {
        URL fileURL = RandomSample.class.getResource("RandomSample.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "RandomSample", argv);

            if (itf.HasFloat("-p") && itf.HasInt("-n")) {
                oechem.OEThrow.Usage("Give only one option, -p or -n");
            }

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

            Random r = null;
            if (itf.HasInt("-seed")) {
                r = new Random(itf.GetInt("-seed"));
            }
            else {
                r = new Random();
            }

            if (itf.HasInt("-n")) {
                RandomizeN(ifs, ofs, itf.GetInt("-n"), r);
            }
            else if (itf.HasFloat("-p")) {
                RandomizePercent(ifs, ofs, itf.GetFloat("-p"), r);
            }
            else {
                Randomize(ifs, ofs, r);
            }
            ifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
