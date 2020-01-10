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

public class SimplePrepScript {
    public static void main(String argv[]) {
        if(argv.length < 2) {
            oechem.OEThrow.Usage("openeye.examples.oefastrocs.SimplePrepScript input.oeb output_prepped_database.oeb");
            System.exit(0);
        }   
    
        // Input mol stream
        oemolistream ifs = new oemolistream();
        ifs.open(argv[0]);

        // Pre-Compress output mol stream
        oemolostream ofs = new oemolostream();
        oechem.OEPRECompress(ofs);
        ofs.open(argv[1]);

        // Prepare mol & write to stream
        OEMol mol = new OEMol();
        while(oechem.OEReadMolecule(ifs, mol)) {
            oefastrocs.OEPrepareFastROCSMol(mol);
            OEMol halfMol = new OEMol(mol, OEMCMolType.HalfFloatCartesian);
            oechem.OEWriteMolecule(ofs, halfMol);
        }

        ofs.close();
        System.exit(0);
    }
}
