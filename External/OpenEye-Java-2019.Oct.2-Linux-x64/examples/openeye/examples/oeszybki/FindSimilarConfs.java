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
package openeye.examples.oeszybki;

import java.text.DecimalFormat;

import openeye.oechem.*;
import openeye.oeszybki.*;

public class FindSimilarConfs
{
    public static void main(String argv[])
    {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("FindSimilarConfs <input> <output>");
        }

        String inputFile  = argv[0];
        String outputFile = argv[1];

        oemolistream ifs = new oemolistream();
        if (!ifs.open(inputFile)) {
            oechem.OEThrow.Fatal("Unable to open " + inputFile + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(outputFile)) {
            oechem.OEThrow.Fatal("Unable to open " + outputFile + " for writing");
        }

        OEMol mol = new OEMol();
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        OEFreeFormConfOptions opts = new OEFreeFormConfOptions();
        OEFreeFormConf ffconf = new OEFreeFormConf(opts);

        // Estimate free energies to ontain the minimum energy conformers
        OEMol omol = new OEMol(mol);
        if (!(ffconf.EstimateFreeEnergies(omol) == OEFreeFormReturnCode.Success)) {
            oechem.OEThrow.Error("Failed to estimate conformational free energies for molecule " + mol.GetTitle());
        }

        // Find similar conformers to the ones we started with, from the
        // pool of minimum energy conformers
        OEMol fmol = new OEMol(mol);
        for (OEConfBase conf : mol.GetConfs())
            ffconf.FindSimilarConfs(fmol, omol, conf, new OESimilarByRMSD(0.05));

        oechem.OEWriteMolecule(ofs, fmol);
        ofs.close();
    }
}
