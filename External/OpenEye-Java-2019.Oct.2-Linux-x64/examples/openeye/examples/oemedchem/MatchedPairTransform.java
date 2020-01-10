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
/*****************************************************************************
 * Utility to load a previously generated MMP index
 *  and use the transformations discovered to alter a second set of structures
 * ---------------------------------------------------------------------------
 * MatchedPairTransform mmp_index input_mols output_mols
 *
 * mmp_index: filename of matched pair index
 * input_mols: filename of molecules to transform based on analysis
 * output_mols: filename to collect transformed molecules
 *****************************************************************************/
package openeye.examples.oemedchem;

import openeye.oechem.*;
import openeye.oemedchem.*;

public class MatchedPairTransform {
    private static void MMPTransform(OEInterface itf) {
        // input structure(s) to process
        oemolistream ifsmols = new oemolistream() ;
        if (!ifsmols.open(itf.GetString("-input"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-input") + " for reading");
        }

        // check MMP index
        String mmpimport = itf.GetString("-mmpindex");
        if (!oemedchem.OEIsMatchedPairAnalyzerFileType(mmpimport)) {
            oechem.OEThrow.Fatal("Not a valid matched pair index input file, " +
                                 mmpimport);
        }

        // load MMP index
        OEMatchedPairAnalyzer mmp = new OEMatchedPairAnalyzer();
        if (!oemedchem.OEReadMatchedPairAnalyzer(mmpimport, mmp)) {
            oechem.OEThrow.Fatal("Unable to load index " + mmpimport);
        }

        if (mmp.NumMols() == 0) {
            oechem.OEThrow.Fatal("No records in loaded MMP index file: " + mmpimport);
        }

        if (mmp.NumMatchedPairs() == 0) {
            oechem.OEThrow.Fatal("No matched pairs found from indexing, " +
                                 "use -fragGe,-fragLe options to extend index range");
        }

        // output (transformed) structure(s)
        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-output"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-output") + " for writing");
        }

        // request a specific context for the transform activity, here 0-bonds;
        int chemctxt = OEMatchedPairContext.Bond0;
        String askcontext = itf.GetString("-context");
        char ctxt = askcontext.charAt(0);
        switch (ctxt) {
        case '0':
            chemctxt = OEMatchedPairContext.Bond0;
            break;
        case '1':
            chemctxt = OEMatchedPairContext.Bond1;
            break;
        case '2':
            chemctxt = OEMatchedPairContext.Bond2;
            break;
        case '3':
            chemctxt = OEMatchedPairContext.Bond3;
            break;
        case 'a':
        case 'A':
            chemctxt = OEMatchedPairContext.AllBonds;
            break;
        default:
            oechem.OEThrow.Fatal("Invalid context specified: " + askcontext + ", only 0|1|2|3|A allowed");
            break;
        }

        boolean verbose = itf.GetBool("-verbose");

        if (verbose) {
            // return some status information;
            oechem.OEThrow.Info(mmpimport+ ": molecules: " + mmp.NumMols() + " matched pairs: " + mmp.NumMatchedPairs());
        }

        int minpairs = itf.GetInt("-minpairs");
        if (minpairs > 1 && verbose)
            oechem.OEThrow.Info("Requiring at least " + minpairs + " matched pairs to apply transformations");

        int orec = 0;
        int ocnt = 0;
        int ototal = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifsmols,mol)) {
            ++orec ;
            ocnt = 0;
            for (OEMolBase outmol : oemedchem.OEMatchedPairApplyTransforms(mol, mmp, chemctxt, minpairs)) {
                ++ocnt ;
                oechem.OEWriteMolecule(ofs, outmol);
            }
            ototal += ocnt ;
            if (verbose && ocnt == 0) {
                // as minpairs increases, fewer transformed mols are generated - output if requested
                String name = mol.GetTitle();
                if (name.length() == 0)
                    name = "Record " + orec;
                oechem.OEThrow.Info(name + ": did not produce any output");
            }
        }
        if (orec == 0)
            oechem.OEThrow.Fatal("No records in input structure file to transform");

        if (ototal == 0)
            oechem.OEThrow.Fatal("No transformed structures generated");

        System.out.printf("Input molecules=%d Output molecules=%d%n", orec, ototal);
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);

        if (oechem.OEParseCommandLine (itf, args, "MatchedPairTransform")) {
            MMPTransform(itf);
        }
    }

    private static String interfaceData =
"!CATEGORY MatchedPairTransform\n" +
"    !CATEGORY I/O\n" +
"        !PARAMETER -mmpindex 1\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of serialized matched pair index to load\n" +
"          !KEYLESS 1\n" +
"        !END\n" +
"        !PARAMETER -input 2\n" +
"          !ALIAS -i\n" +
"          !ALIAS -in\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of structures to process using matched pairs from -mmpindex\n" +
"          !KEYLESS 2\n" +
"        !END\n" +
"        !PARAMETER -output 3\n" +
"          !ALIAS -o\n" +
"          !ALIAS -out\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Output filename\n" +
"          !KEYLESS 3\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY options\n" +
"        !PARAMETER -context 1\n" +
"           !ALIAS -c\n" +
"           !TYPE string\n" +
"           !DEFAULT 0\n" +
"           !BRIEF chemistry context to use for the transformation [0|1|2|3|A]\n" +
"        !END\n" +
"        !PARAMETER -minpairs 2\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !LEGAL_RANGE 0 inf\n" +
"           !BRIEF require at least -minpairs to apply the transformations (default: all)\n" +
"        !END\n" +
"        !PARAMETER -verbose 3\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate verbose output\n" +
"        !END\n" +
"        !PARAMETER -nowarnings 4\n" +
"           !TYPE bool\n" +
"           !DEFAULT 1\n" +
"           !BRIEF suppress warning messages from applying transformations\n" +
"        !END\n" +
"    !END\n" +
"!END\n";
}
