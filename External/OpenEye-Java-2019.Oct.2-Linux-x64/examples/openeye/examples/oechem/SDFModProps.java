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
 * Modifies the SD data of a set of input molecules by clearing all tags,
 * defining which tags to keep or defining which tags to remove
 ****************************************************************************/
package openeye.examples.oechem;

import java.util.Set;
import java.util.HashSet;
import java.net.URL;
import openeye.oechem.*;

public class SDFModProps {

    public static void clearProps(oemolistream ifs,
            oemolostream ofs) {
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEClearSDData(mol);
            oechem.OEWriteMolecule(ofs, mol);
        }
    }

    public static void keepProps(OEStringIter proplist,
            oemolistream ifs,
            oemolostream ofs) {
        Set<String> props = new HashSet<String>();
        proplist.ToFirst();
        for (String prop : proplist) {
            props.add(prop);
        }

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            for (OESDDataPair dp : oechem.OEGetSDDataPairs(mol)) {
                if (!props.contains(dp.GetTag())) {
                    oechem.OEDeleteSDData(mol, dp.GetTag());
                }
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
    }

    public static void removeProps(OEStringIter proplist,
            oemolistream ifs,
            oemolostream ofs) {
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            proplist.ToFirst();
            for (String prop : proplist) {
                oechem.OEDeleteSDData(mol, prop);
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
    }

    public static void modProps(OEInterface itf,
            oemolistream ifs,
            oemolostream ofs) {
        if (itf.HasString("-keep")) {
            OEStringIter proplist = itf.GetStringList("-keep");
            keepProps(proplist, ifs, ofs);
        }
        else if (itf.HasString("-remove")) {
            OEStringIter proplist = itf.GetStringList("-remove");
            removeProps(proplist, ifs, ofs);
        }
        else if (itf.GetBool("-clearAll")) {
            clearProps(ifs, ofs);
        }
    }

    public static void main(String argv[]) {
        URL fileURL = SDFModProps.class.getResource("SDFModProps.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "SDFModProps", argv);

            int numoption = 0;
            if (itf.HasString("-keep")) {
                numoption++;
            }
            if (itf.HasString("-remove")) {
                numoption++;
            }
            if (itf.GetBool("-clearAll")) {
                numoption++;
            }
            if (numoption != 1) {
                oechem.OEThrow.Usage("Need to pick one from -keep, -remove, or -clearAll");
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }
            if (!oechem.OEIsSDDataFormat(ifs.GetFormat()))
            {
                oechem.OEThrow.Fatal("Only works for input file formats that support SD data (sdf,oeb,csv)");
            }

            oemolostream ofs = new oemolostream();
            if (!ofs.open(itf.GetString("-o"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
            }
            if (!oechem.OEIsSDDataFormat(ofs.GetFormat()))
            {
                oechem.OEThrow.Fatal("Only works for output file formats that support SD data (sdf,oeb,csv)");
            }

            modProps(itf, ifs, ofs);
            ifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
