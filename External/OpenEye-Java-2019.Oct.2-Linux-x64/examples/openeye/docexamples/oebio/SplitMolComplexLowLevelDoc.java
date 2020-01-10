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
//@ <SNIPPET>
package openeye.docexamples.oebio;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;

import openeye.oebio.*;
import openeye.oechem.*;

public class SplitMolComplexLowLevelDoc {

    public static void main(String[] argv) {
        URL fileURL = SplitMolComplexLowLevelDoc.class.getResource("SplitMolComplexLowLevelDoc.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        oebio.OEConfigureSplitMolComplexOptions(itf);

        if (!oechem.OEParseCommandLine(itf, argv, "SplitMolComplexLowLevelDoc"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        String inputFile  = itf.GetString("-in");
        String outputFile = itf.GetString("-out");

        oemolistream ims = new oemolistream();
        if (itf.GetUnsignedInt("-modelnum") != 1)
            ims.SetFlavor(OEFormat.PDB,
                          oechem.OEGetDefaultIFlavor(OEFormat.PDB)
                                            & ~OEIFlavor.PDB.ENDM);
        if (!ims.open(inputFile))
            oechem.OEThrow.Fatal("Cannot open input file!");

        oemolostream oms = new oemolostream();
        if (!oms.open(outputFile))
            oechem.OEThrow.Fatal("Cannot open output file!");

        OEGraphMol mol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ims, mol))
            oechem.OEThrow.Fatal("Unable to read molecule from " + inputFile);
        ims.close();

        if (itf.GetBool("-verbose"))
            oechem.OEThrow.SetLevel(OEErrorLevel.Verbose);

        OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
        oebio.OESetupSplitMolComplexOptions(opt, itf);

        // for input molecule mol and options opt ...
        OEAtomBondSetVector frags = new OEAtomBondSetVector();

        if (oebio.OEGetMolComplexFragments(frags, mol, opt)) {
            //...
        }
        else
            oechem.OEThrow.Fatal("Unable to fragment mol complex from "+inputFile);

        // for options opt and OEAtomBondSetVector frags produced earlier ...
        int numSites = oebio.OECountMolComplexSites(frags);
        oechem.OEThrow.Verbose("sites " + numSites);

        OEGraphMol lig = new OEGraphMol();
        OEUnaryRoleSetPred l = opt.GetLigandFilter();

        if (! oebio.OECombineMolComplexFragments(lig, frags, opt, l))
            oechem.OEThrow.Warning("Unable to combine ligand frags from " +
                                    mol.GetTitle());

        OEGraphMol protComplex = new OEGraphMol();
        OEUnaryRoleSetPred p = opt.GetProteinFilter();
        OEUnaryRoleSetPred w = opt.GetWaterFilter();
        if (! oebio.OECombineMolComplexFragments(protComplex,
                                                 frags,
                                                 opt,
                                                 new OEOrRoleSet(p, w)) )
            oechem.OEThrow.Warning("Unable to combine complex frags from " +
                                    mol.GetTitle());

        if (! (lig.NumAtoms() == 0)) {
          oechem.OEThrow.Verbose("  lig " + lig.GetTitle());
          oechem.OEWriteMolecule(oms, lig);
        }

        if (! (protComplex.NumAtoms() == 0)) {
          oechem.OEThrow.Verbose(" prot " + protComplex.GetTitle());
          oechem.OEWriteMolecule(oms, protComplex);
        }

        oms.close();
    }
}
//@ </SNIPPET>
