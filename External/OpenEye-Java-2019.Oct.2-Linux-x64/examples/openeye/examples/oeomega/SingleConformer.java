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

public class SingleConformer {

    public static void main(String[] args) {
        if (args.length!=2) {
            oechem.OEThrow.Usage("SingleConformer <infile> <outfile>");
        }

        oechem.OEThrow.SetLevel(OEErrorLevel.Info); 

        oemolistream ifs = new oemolistream(args[0]);
        oemolostream ofs = new oemolostream(args[1]);

        OEOmegaOptions omegaOpts = new OEOmegaOptions();
        omegaOpts.SetMaxConfs(1);
        OEOmega omega = new OEOmega(omegaOpts);

        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            System.out.println(mol.GetTitle());
            omega.call(mol);
            oechem.OEWriteMolecule(ofs, mol);
        }
        ifs.close();
        ofs.close();
    }
}
