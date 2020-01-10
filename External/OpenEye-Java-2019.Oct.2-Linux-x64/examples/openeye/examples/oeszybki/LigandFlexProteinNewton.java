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

public class LigandFlexProteinNewton
{
    public static void main(String argv[])
    {
        if (argv.length != 3)
            oechem.OEThrow.Usage("LigandFlexProteinNewton protein input_ligand output_ligand");

        String proteinFile = argv[0];
        String ligandFile  = argv[1];
        String outligandFile = argv[2];

        oemolistream pfs = new oemolistream();
        if (!pfs.open(proteinFile)) {
            oechem.OEThrow.Fatal("Unable to open " + proteinFile + " for reading");
        }

        oemolistream lfs = new oemolistream();
        if (!lfs.open(ligandFile)) {
            oechem.OEThrow.Fatal("Unable to open " + ligandFile + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(outligandFile)) {
            oechem.OEThrow.Fatal("Unable to open " + outligandFile + " for writing");
        }

        OEGraphMol mol = new OEGraphMol();
        OEGraphMol protein = new OEGraphMol();
        oechem.OEReadMolecule(lfs, mol);
        lfs.close();
        oechem.OEReadMolecule(pfs, protein);
        pfs.close();

        OESzybkiOptions opts = new OESzybkiOptions();
        opts.GetOptOptions().SetOptimizerType(OEOptType.NEWTON);
        opts.GetProteinOptions().SetProteinElectrostaticModel(OEProteinElectrostatics.ExactCoulomb);
        opts.GetProteinOptions().SetProteinFlexibilityType(OEProtFlex.Residues);
        opts.GetProteinOptions().SetProteinFlexibilityRange(2.0f);

        OESzybki sz = new OESzybki(opts);
        sz.SetProtein(protein);

        OESzybkiResults res = new OESzybkiResults();
        if (sz.call(mol, res)) {
            oechem.OEWriteMolecule(ofs, mol);
            res.Print(oechem.oeout);
        }
        ofs.close();
    }
}
