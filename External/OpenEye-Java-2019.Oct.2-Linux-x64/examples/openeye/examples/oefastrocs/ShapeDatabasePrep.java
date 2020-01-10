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

import java.util.List;

public class ShapeDatabasePrep {

    private static void TrimConformers(OEMol mol, int maxConfs) {
        List<OEConfBase> confs = mol.GetConfList();
        for(int i = 0; i < confs.size(); i++) {
            if(i >= maxConfs)
                mol.DeleteConf(confs.get(i));
        }
    }    

    public static void main(String argv[]) {
        if(argv.length != 2 && argv.length != 3) {
            oechem.OEThrow.Usage("openeye.examples.oefastrocs.ShapeDatabasePrep <database.oeb> <prepped_database.oeb> [max_confs]");
            System.exit(0);
        }

        int maxConfs = 0;
        if(argv.length == 3) {
            maxConfs = Integer.parseInt(argv[2]);
            if(maxConfs < 1)
                oechem.OEThrow.Fatal("Illegal number of conformer requested " + maxConfs);
        }

        // input - preserve rotor-offset-compression
        oemolistream ifs = new oemolistream();
        oechem.OEPreserveRotCompress(ifs);

        if(!ifs.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");

        // output - use PRE-compress for smaller files (no need to .gz the file)
        oemolostream ofs = new oemolostream();
        oechem.OEPRECompress(ofs);
        if(!ofs.open(argv[1]))
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");

        OEDots dots = new OEDots(10000, 200, "molecules");
        OEMol mol = new OEMol();
        while(oechem.OEReadMolecule(ifs, mol)) {
            if(maxConfs != 0)
                TrimConformers(mol, maxConfs);

            oefastrocs.OEPrepareFastROCSMol(mol);
            oechem.OEWriteMolecule(ofs, mol);
            dots.Update();
        }

        dots.Total();
        ofs.close();

        oechem.OEThrow.Info("Indexing " + argv[1]);
        if(!oechem.OECreateMolDatabaseIdx(argv[1]))
            oechem.OEThrow.Fatal("Failed to index " + argv[1]);

        System.exit(0);
    }
}
