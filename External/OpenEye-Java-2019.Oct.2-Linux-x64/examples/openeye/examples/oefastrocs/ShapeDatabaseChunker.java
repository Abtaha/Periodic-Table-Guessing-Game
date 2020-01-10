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
package openeye.examples.oefastrocs;

import openeye.oechem.*;
import openeye.oefastrocs.*;

import java.util.ArrayList;
import java.lang.Integer;

public class ShapeDatabaseChunker {
    public static void main(String argv[]) {
        if(argv.length != 3) {
            oechem.OEThrow.Usage("openeye.examples.oefastrocs.ShapeDatabaseChunker <database> <prefix> <n_servers>");
            System.exit(0);
        }

        // input - preserve rotor-offset-compression
        oemolistream ifs = new oemolistream();
        oechem.OEPreserveRotCompress(ifs);

        String ifname = argv[0];
        if(!ifs.open(ifname))
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");

        // output
        String prefix = argv[1];
        String ext = oechem.OEGetFileExtension(prefix);
        boolean pfx = true;
        if(ext == null) {
            pfx = false;
            ext = oechem.OEGetFileExtension(ifname);
        }
    
        StringBuilder base = new StringBuilder();
        base.append(prefix);

        int nservers = Integer.parseInt(argv[2]);
        ArrayList<oemolostream> outstrms = new ArrayList<oemolostream>();
        for(int i = 1; i < nservers + 1; i++) {
            oemolostream ofs = new oemolostream();
            if(!ofs.open(base + "_" + i + "." + ext))
                oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");

            outstrms.add(ofs);
        }

        OEDots dots = new OEDots(10000, 200, "molecules");
        OEMol mol = new OEMol();
        while(oechem.OEReadMolecule(ifs, mol)) {
            oefastrocs.OEPrepareFastROCSMol(mol);

            int nhvyatoms = oechem.OECount(mol, new OEIsHeavy());
            
            oemolostream ofs = outstrms.get(nhvyatoms % nservers);
            oechem.OEWriteMolecule(ofs, mol);

            dots.Update();
        }

        dots.Total();

        for(int i = 0; i < outstrms.size(); i++) {
            oemolostream strm = outstrms.get(i);
            String fname = strm.GetFileName();
            strm.close();
            oechem.OEThrow.Info("Indexing " + fname);
            if(!oechem.OECreateMolDatabaseIdx(fname))
                oechem.OEThrow.Fatal("Failed to index " + fname);
        }

        System.exit(0);
    }
}
