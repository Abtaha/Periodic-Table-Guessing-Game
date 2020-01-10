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

import openeye.oechem.*;
import openeye.oeszybki.*;

public class BoundEntropy
{
    public static void main(String argv[])
    {
        if (argv.length != 3) {
            oechem.OEThrow.Usage("BoundEntropy bound_ligand protein opt_ligand");
        }

        String ligandFile    = argv[0];
        String proteinFile   = argv[1];
        String optligandFile = argv[2];

        oemolistream lfs = new oemolistream();
        if (!lfs.open(ligandFile)) {
            oechem.OEThrow.Fatal("Unable to open " + ligandFile + " for reading");
        }

        oemolistream pfs = new oemolistream();
        if (!pfs.open(proteinFile)) {
            oechem.OEThrow.Fatal("Unable to open " + proteinFile + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(optligandFile)) {
            oechem.OEThrow.Fatal("Unable to open " + optligandFile + " for writing");
        }

        OEMol ligand = new OEMol();
        oechem.OEReadMolecule(lfs, ligand);
        lfs.close();

        OEGraphMol protein = new OEGraphMol();
        oechem.OEReadMolecule(pfs, protein);
        pfs.close();

        OESzybkiOptions opts = new OESzybkiOptions();
        OESzybki sz = new OESzybki(opts);
        sz.SetProtein(protein);

        OESzybkiEnsembleResults eres = new OESzybkiEnsembleResults();
        double entropy = sz.GetEntropy(ligand, eres, OEEntropyMethod.Analytic, OEEnvType.Protein);

        System.err.printf("Estimated entropy of the bound ligand is: %5.1f J/(mol*K)\n", entropy);

        oechem.OEWriteMolecule(ofs, ligand);
        ofs.close();
    }
}
