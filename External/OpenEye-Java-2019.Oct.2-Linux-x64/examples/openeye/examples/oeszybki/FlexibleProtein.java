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

public class FlexibleProtein {
    public static void main(String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);
        oechem.OEParseCommandLine (itf, args, "FlexibleProtein");

        oemolistream lfs = new oemolistream();
        if (!lfs.open(itf.GetString("-in")))
          oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-in") + " for reading");

        oemolistream pfs = new oemolistream();
        if (!pfs.open(itf.GetString("-p")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-p") + " for reading");

        oemolostream olfs = new oemolostream();
        if (!olfs.open(itf.GetString("-out")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-out") + " for writing");

        oemolostream opfs = new oemolostream();
        if (itf.HasString("-s")) {
            if (!opfs.open(itf.GetString("-s")))
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-s") + " for writing");
        }

        oeostream logfile = oechem.oeout;
        if (itf.HasString("-log")) {
            if (!logfile.open(itf.GetString("-log")))
              oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-log") + " for writing");
        }

        // Szybki options
        OESzybkiOptions opts = new OESzybkiOptions();

        // select optimization type
        String opt = itf.GetString("-opt");
        if (opt.equals("Cartesian"))
            opts.SetRunType(OERunType.CartesiansOpt);
        else if (opt.equals("Torsion"))
            opts.SetRunType(OERunType.TorsionsOpt);
        else if (opt.equals("SolidBody"))
            opts.SetRunType(OERunType.SolidBodyOpt);

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

        // use smooth potential and tight convergence
        if (emodel.equals("VdW") || emodel.equals("ExactCoulomb")) {
            opts.GetProteinOptions().SetExactVdWProteinLigand(true);
            opts.GetOptOptions().SetMaxIter(1000);
            opts.GetOptOptions().SetGradTolerance(1e-6);
        }

        // protein flexibility
        opts.GetProteinOptions().SetProteinFlexibilityType(OEProtFlex.SideChains);
        opts.GetProteinOptions().SetProteinFlexibilityRange(itf.GetDouble("-d"));

        // Szybki object
        OESzybki sz = new OESzybki(opts);

        // read and setup protein
        OEGraphMol protein = new OEGraphMol();
        OEGraphMol oprotein = new OEGraphMol();  // optimized protein
        oechem.OEReadMolecule(pfs, protein);
        sz.SetProtein(protein);
        pfs.close();

        // process molecules
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(lfs, mol)) {
            logfile.write("\nMolecule " + mol.GetTitle() + "\n");
            for (OESzybkiResults res:  sz.call(mol))
                res.Print(logfile);

            oechem.OEWriteMolecule(olfs, mol);
            if (itf.HasString("-s")) {
                sz.GetProtein(oprotein);
                oechem.OEWriteMolecule(opfs, oprotein);
            }
        }
        opfs.close();
    }

    private static String interfaceData =
"!BRIEF -in input_molecule -p protein -out output_molecule\n" +
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
"!PARAMETER -opt\n" +
"!TYPE string\n" +
"!DEFAULT Cartesian\n" +
"!LEGAL_VALUE Cartesian\n" +
"!LEGAL_VALUE Torsion\n" +
"!LEGAL_VALUE SolidBody\n" +
"!BRIEF Optimization method\n" +
"!END\n" +
"\n" +
"!PARAMETER -d\n" +
"!TYPE double\n" +
"!DEFAULT 5.0\n" +
"!BRIEF Distance criteria from protein side-chains flexibility.\n" +
"!END\n" +
"\n" +
"!PARAMETER -s\n" +
"!TYPE string\n" +
"!REQUIRED false\n" +
"!BRIEF File name the partially optimized protein will be saved.\n"+
"!END";
}
