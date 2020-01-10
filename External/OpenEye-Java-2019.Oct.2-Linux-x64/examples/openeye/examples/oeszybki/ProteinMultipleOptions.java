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

public class ProteinMultipleOptions
{
    public static void main(String argv[])
    {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);
        oechem.OEParseCommandLine (itf, argv, "ProteinMultipleOptions");

        oemolistream lfs = new oemolistream();
        if (!lfs.open(itf.GetString("-in"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-in") + " for reading");
        }

        oemolistream pfs = new oemolistream();
        if (!pfs.open(itf.GetString("-p"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-p") + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-out"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-out") + " for writing");
        }

        oeostream logfile = oechem.oeout;
        if (itf.HasString("-log")) {
            if (!logfile.open(itf.GetString("-log")))
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-log") + " for writing");
        }

        // Szybki options
        OESzybkiOptions opts = new OESzybkiOptions();

        // select optimization type
        if (itf.GetBool("-t")) {
            opts.SetRunType(OERunType.TorsionsOpt);
        }
        else {
            opts.SetRunType(OERunType.CartesiansOpt);
        }

        // select protein-electrostatic model
        String emodel = itf.GetString("-e");
        int elecModel = OEProteinElectrostatics.NoElectrostatics;
        if (emodel.equals("VdW"))
            elecModel = OEProteinElectrostatics.NoElectrostatics;
        else if (emodel.equals("PB"))
            elecModel = OEProteinElectrostatics.GridPB;
        else if (emodel.equals("Coulomb"))
            elecModel = OEProteinElectrostatics.GridCoulomb;
        else if (emodel.equals("ExactCoulomb"))
            elecModel = OEProteinElectrostatics.ExactCoulomb;
        opts.GetProteinOptions().SetProteinElectrostaticModel(elecModel);

        opts.GetProteinOptions().SetExactVdWProteinLigand(true);
        opts.GetOptOptions().SetMaxIter(1000);
        opts.GetOptOptions().SetGradTolerance(1e-6);

        // Szybki object
        OESzybki sz = new OESzybki(opts);

        // read and setup protein
        OEGraphMol protein = new OEGraphMol();
        oechem.OEReadMolecule(pfs, protein);
        sz.SetProtein(protein);

        // save or load grid potential
        if (emodel.equals("PB") || emodel.equals("Coulomb")) {
            if(itf.HasString("-s"))
                sz.SavePotentialGrid(itf.GetString("-s"));
            if(itf.HasString("-l"))
                sz.LoadPotentialGrid(itf.GetString("-l"));
        }

        // process molecules
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(lfs, mol)) {
            logfile.write("\nMolecule " + mol.GetTitle() + "\n");
            boolean no_res = true;
            for (OESzybkiResults res : sz.call(mol)) {
                res.Print(logfile);
                no_res = false;
            }

            if (no_res) {
                oechem.OEThrow.Warning("No results processing molecule: " + mol.GetTitle());
                continue;
            }

            oechem.OEWriteMolecule(ofs, mol);
        }
    }
    private static String interfaceData =
"!PARAMETER -in\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Input molecule file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -p\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Input protein file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -out\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Output molecule file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -log\n" +
"!TYPE string\n" +
"!REQUIRED false\n" +
"!BRIEF Log file name. Defaults to standard out.\n" +
"!END\n" +
"\n" +
"!PARAMETER -e\n" +
"!TYPE string\n" +
"!DEFAULT VdW\n" +
"!LEGAL_VALUE VdW\n" +
"!LEGAL_VALUE PB\n" +
"!LEGAL_VALUE Coulomb\n" +
"!LEGAL_VALUE ExactCoulomb\n" +
"!BRIEF Protein ligand electrostatic model.\n" +
"!END\n" +
"\n" +
"!PARAMETER -t\n" +
"!TYPE bool\n" +
"!DEFAULT false\n" +
"!REQUIRED false\n" +
"!BRIEF Torsions added to the optimized variables.\n" +
"!END\n" +
"\n" +
"!PARAMETER -l\n" +
"!TYPE string\n" +
"!REQUIRED false\n" +
"!BRIEF File name of the potential grid to be read.\n" +
"!END\n" +
"\n" +
"!PARAMETER -s\n" +
"!TYPE string\n" +
"!REQUIRED false\n" +
"!BRIEF File name of the potential grid to be saved.\n" +
"!END";
}
