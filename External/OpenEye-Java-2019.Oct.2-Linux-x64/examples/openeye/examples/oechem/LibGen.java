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
/****************************************************************************
 * Perform library generation with SMIRKS
 ****************************************************************************/
package openeye.examples.oechem;

import java.util.Set;
import java.util.HashSet;
import java.net.URL;
import openeye.oechem.*;

public class LibGen {

    public static void libGen(OELibraryGen libgen, oemolostream ofs,
            boolean isomeric, boolean unique) {
        int smiflag = OESMILESFlag.DEFAULT;
        if (isomeric) {
            smiflag |= OESMILESFlag.ISOMERIC;
        }
        Set<String> uniqueProducts = new HashSet<String>();
        for (OEMolBase mol : libgen.GetProducts()) {
            String smiles = oechem.OECreateSmiString(mol, smiflag);
            if (!unique || !uniqueProducts.contains(smiles)) {
                uniqueProducts.add(smiles);
                oechem.OEWriteMolecule(ofs, mol);
            }
        }
    }

    public static void main(String argv[]) {
        URL fileURL = LibGen.class.getResource("LibGen.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "LibGen", argv);
            if (!(itf.HasString("-smirks") ^ itf.HasString("-rxn"))) {
                oechem.OEThrow.Fatal("Please provide SMIRKS string or MDL reaction file");
            }

            OEQMol reaction = new OEQMol();
            if (itf.HasString("-smirks")) {
                String smirks = itf.GetString("-smirks");
                if (!oechem.OEParseSmirks(reaction, smirks)) {
                    oechem.OEThrow.Fatal("Unable to parse SMIRKS: " + smirks);
                }
            }
            else {
                String rxn = itf.GetString("-rxn");
                oemolistream rfile = new oemolistream(rxn);
                int opt = OEMDLQueryOpts.ReactionQuery|OEMDLQueryOpts.SuppressExplicitH;
                if (!oechem.OEReadMDLReactionQueryFile(rfile, reaction, opt)) {
                    oechem.OEThrow.Fatal("Unable to read reaction file: " + rxn);
                }
            }
            boolean relax      = itf.GetBool("-relax");
            boolean unique     = itf.GetBool("-unique");
            boolean implicitH  = itf.GetBool("-implicitH");
            boolean valCorrect = itf.GetBool("-valence");
            boolean isomeric   = itf.GetBool("-isomeric");

            OELibraryGen libgen = new OELibraryGen();

            // Initialize library generation
            if (!libgen.Init(reaction, !relax)) {
                oechem.OEThrow.Fatal("failed to initialize library generator");
            }
            libgen.SetValenceCorrection(valCorrect);
            libgen.SetExplicitHydrogens(!implicitH);
            libgen.SetClearCoordinates(true);

            int nrReacts = 0;
            for (String filename : itf.GetStringList("-reactants")) {
                if (nrReacts >= libgen.NumReactants()) {
                    oechem.OEThrow.Fatal("Number of reactant files exceeds number of reactants specified in reaction");
                }
                oemolistream ifs = new oemolistream();
                if (!ifs.open(filename)) {
                    oechem.OEThrow.Fatal("Unable to open " + filename + " for reading");
                }
                OEGraphMol mol = new OEGraphMol();
                while (oechem.OEReadMolecule(ifs, mol)) {
                    libgen.AddStartingMaterial(mol, nrReacts, unique);
                }
                nrReacts++;
                ifs.close();
            }
            if (nrReacts != libgen.NumReactants()) {
                oechem.OEThrow.Fatal("Reactions requires " + libgen.NumReactants() + " reactant files!");
            }

            oemolostream ofs = new oemolostream(".ism");
            if (itf.HasString("-product")) {
                if (!ofs.open(itf.GetString("-product"))) {
                    oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-product") + " for writing");
                }
            }
            libGen(libgen, ofs, isomeric, unique);
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
