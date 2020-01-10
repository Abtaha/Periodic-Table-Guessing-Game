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

public class CustomColorFFPrep {
    public static void main(String argv[]) {
        String COLOR_FORCE_FIELD = "#\n" + 
                                   "TYPE negative\n" + 
                                   "#\n" + 
                                   "#\n" + 
                                   "PATTERN negative [-]\n" + 
                                   "PATTERN negative [OD1+0]-[!#7D3]~[OD1+0]\n" + 
                                   "PATTERN negative [OD1+0]-[!#7D4](~[OD1+0])~[OD1+0]\n" + 
                                   "#\n" + 
                                   "#\n" + 
                                   "INTERACTION negative negative attractive gaussian weight=1.0 radius=1.0";

        if(argv.length < 2) {
            oechem.OEThrow.Usage("openeye.examples.oefastrocs.CustomColorFFPrep <input> <output>");
            System.exit(0);
        }

        // input - preserve rotor-offset-compression
        oemolistream ifs = new oemolistream();
        OEBinaryIOHandlerBase ihand = ifs.GetBinaryIOHandler();
        ihand.Clear();
        oechem.OEInitHandler(ihand, oechem.OEBRotCompressOpts(), oechem.OEBRotCompressOpts());

        String ifname = argv[0];
        if(!ifs.open(ifname)) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for reading");
        }

        // output
        String ofname = argv[1];
        long oformt = oechem.OEGetFileType(oechem.OEGetFileExtension(ofname));
        if(oformt != OEFormat.OEB)
            oechem.OEThrow.Fatal("Output file format must be OEB");

        oemolostream ofs = new oemolostream();
        if(!ofs.open(ofname))
            oechem.OEThrow.Fatal("Unable to open " + ofname + " for writing");

        oeisstream iss = new oeisstream(COLOR_FORCE_FIELD);
        OEColorForceField cff = new OEColorForceField();
        if(!cff.Init(iss))
            oechem.OEThrow.Fatal("Unable to initialize OEColorForceField");

        OEDots dots = new OEDots(10000, 200, "molecules");
        OEMol mol = new OEMol();
        while(oechem.OEReadMolecule(ifs, mol)) {
            oefastrocs.OEPrepareFastROCSMol(mol, cff);
            oechem.OEWriteMolecule(ofs, mol);
            dots.Update();
        }

        dots.Total();
        ofs.close();
        oechem.OEThrow.Info("Indexing " + ofname);
        if(!oechem.OECreateMolDatabaseIdx(ofname))
            oechem.OEThrow.Fatal("Failed to index " + argv[2]);

        System.exit(0);
    }
}
