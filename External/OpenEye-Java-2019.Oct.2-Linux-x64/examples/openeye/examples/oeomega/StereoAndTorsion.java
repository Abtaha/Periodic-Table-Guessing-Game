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
package openeye.examples.oeomega;

import openeye.oechem.*;
import openeye.oeomega.*;

public class StereoAndTorsion { 

    public static void main(String[] args) {
        if (args.length != 2)
            oechem.OEThrow.Usage("StereoAndTorsion <infile> <outfile>");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(args[0]))
            oechem.OEThrow.Fatal("Unable to open " + args[1] + " for reading");

        oemolostream ofs = new oemolostream();
        if (!ofs.open(args[1])) { 
            ifs.close();
            oechem.OEThrow.Fatal("Unable to open " +  args[2] + " for writing");
        }

        OEOmegaOptions omegaOpts = new OEOmegaOptions();
        OEOmega omega = new OEOmega(omegaOpts);
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEThrow.Info("Title: " + mol.GetTitle());

            OEMolBaseIter stereo = oeomegalib.OEFlipper(mol.GetActive(), 12, true);
            while(stereo.hasNext()) {
                mol = new OEMol(stereo.next());
                if (omega.call(mol))
                    oechem.OEWriteMolecule(ofs, mol);
            }
        }
        ifs.close();
        ofs.close();
    }
}
