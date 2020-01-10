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
 * Split molecule file into N chunks or chunks of size N
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import openeye.oechem.*;

public class MolChunk {

    public static void newOutputStream(oemolostream ofs, String outbase,
            String ext, int chunk) {
        String newname = outbase + "_" + String.format("%07d", chunk) + "." + ext;
        if (!ofs.open(newname)) {
            oechem.OEThrow.Fatal("Unable to open " + newname + " for writing");
        }
    }

    public static void splitNParts(oemolistream ifs, int nparts, boolean countconfs,
            String outbase, String ext) {
        int molconfcount = 0;
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (countconfs) {
                molconfcount += mol.NumConfs();
            }
            else {
                molconfcount += 1;
            }
        }
        ifs.rewind();

        int chunksize = molconfcount / nparts;
        int lft = molconfcount % nparts;
        if (lft != 0) {
            chunksize += 1;
        }
        int chunk = 1;
        int count = 0;

        oemolostream ofs = new oemolostream();
        newOutputStream(ofs, outbase, ext, chunk);
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (countconfs) {
                count += mol.NumConfs();
            }
            else {
                count += 1;
            }
            if (count > chunksize) {
                if (chunk == lft) {
                    chunksize -= 1;
                }
                ofs.close();
                chunk += 1;
                count = 1;
                newOutputStream(ofs, outbase, ext, chunk);
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
        ofs.close();
    }

    public static void splitChunk(oemolistream ifs, int chunksize, boolean countconfs,
            String outbase, String ext) {
        int chunk = 1;
        oemolostream ofs = new oemolostream();
        newOutputStream(ofs, outbase, ext, chunk);

        int count = 0;
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (count >= chunksize) {
                ofs.close();
                count = 0;
                chunk += 1;
                newOutputStream(ofs, outbase, ext, chunk);
            }
            if (countconfs) {
                count += mol.NumConfs();
            }
            else {
                count += 1;
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
        ofs.close();
    }

    public static void main(String argv[]) {
        URL fileURL = MolChunk.class.getResource("MolChunk.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "MolChunk", argv);

            if (!(itf.HasInt("-num") ^ itf.HasInt("-size"))) {
                oechem.OEThrow.Fatal("Number of chunks (-num) or the size of each chunk (-size) must be specified and are mutually exclusive.");
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(itf.GetString("-i"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-i") + " for reading");
            }

            int fmt = ifs.GetFormat();
            if (fmt != OEFormat.OEB) {
                ifs.SetConfTest(new OEIsomericConfTest(false));
            }

            String ofname = itf.GetString("-o");
            String outbase = null;
            String ext = oechem.OEGetFileExtension(ofname);
            if (ext != null) {
                int place = ofname.lastIndexOf(ext);
                outbase = ofname.substring(0, place-1);
            }
            else {
                oechem.OEThrow.Fatal("Failed to find file extension");
            }

            boolean countconfs = itf.GetBool("-confs");

            if (itf.HasInt("-num")) {
                int nparts = itf.GetInt("-num");
                splitNParts(ifs, nparts, countconfs, outbase, ext);
            }
            else {
                int chunksize = itf.GetInt("-size");
                splitChunk(ifs, chunksize, countconfs, outbase, ext);
            }
            ifs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
