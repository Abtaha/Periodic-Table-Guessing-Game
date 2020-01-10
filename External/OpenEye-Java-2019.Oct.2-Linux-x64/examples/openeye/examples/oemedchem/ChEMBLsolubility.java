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
 * Utility to apply ChEMBL24 solubility transforms to an input set of structures
 * ---------------------------------------------------------------------------
 * ChEMBLsolubility [-i] input_mols [-o] output_mols [ -verbose ] [ -context [0|2] ] [ -minpairs # ]
 *
 * input_mols: filename of molecules to transform based on analysis
 * output_mols: filename to collect transformed molecules
 * [-verbose]: optional flag to request verbose progress
 * [-context #]: optional flag to request a specific chemistry context
 * [-minpairs #]: optional flag to request a minimum number of pairs to apply transforms
 *****************************************************************************/
package openeye.examples.oemedchem;

import openeye.oechem.*;
import openeye.oemedchem.*;

public class ChEMBLsolubility {
    public static void main(String[] args) {
        OEInterface itf = new OEInterface(interfaceData, "ChEMBLsolubility", args);

        boolean verbose = itf.GetBool("-verbose");

        // input structure(s) to transform;
        oemolistream ifsmols = new oemolistream();
        if (!ifsmols.open(itf.GetString("-i")))
            oechem.OEThrow.Fatal("Unable to open file for reading: " + itf.GetString("-i"));

        // save output structure(s) to this file;
        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-o")))
            oechem.OEThrow.Fatal("Unable to open file for writing: " + itf.GetString("-i"));

        // request a specific context for the transform activity, here 0-bonds;
        int chemctxt = OEMatchedPairContext.Bond0;
        String askcontext = itf.GetString("-context");
        char ctxt = askcontext.charAt(0);
        switch (ctxt) {
        case '0':
            chemctxt = OEMatchedPairContext.Bond0;
            break;
        case '2':
            chemctxt = OEMatchedPairContext.Bond2;
            break;
        default:
            oechem.OEThrow.Fatal("Invalid context specified: " + askcontext + ", only 0|2 allowed");
            break;
        }

        int minpairs = itf.GetInt("-minpairs");
        if (minpairs > 1 && verbose)
            oechem.OEThrow.Info("Requiring at least " + minpairs + " matched pairs to apply transformations");

        int irec = 0;
        int ocnt = 0;
        int ototal = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifsmols,mol)) {
            ++irec;
            oechem.OEDeleteEverythingExceptTheFirstLargestComponent(mol);

            ocnt = 0;
            for (OEMolBase outmol : oemedchem.OEApplyChEMBL24SolubilityTransforms(mol, chemctxt, minpairs)) {
                ++ocnt;
                oechem.OEWriteMolecule(ofs, outmol);
            }
            if (ocnt == 0) {
                String name = mol.GetTitle();
                if (name.length() == 0)
                    name = "Record " + irec;
                System.out.println(name + ": did not produce any output");
                System.out.println(oechem.OEMolToSmiles(mol));
            }
            else {
                ototal += ocnt;
                if (verbose)
                    System.out.printf("Record: %d transformation count=%d total mols=%d%n",
                                      irec, ocnt, ototal);
            }
        }

        if (irec == 0)
            oechem.OEThrow.Fatal("No records in input structure file to transform");

        if (ototal == 0)
            oechem.OEThrow.Warning("No transformed structures generated");
        else
            System.out.printf("Input molecules=%d output molecules=%d%n",
                              irec, ototal);
    }
    private static String interfaceData =
"!BRIEF [-i] <infile1> [-o] <infile2> [ -verbose ] [ -context [0|2]]\n" +
"!PARAMETER -i\n" +
"  !ALIAS -in\n" +
"  !ALIAS -input\n" +
"  !TYPE string\n" +
"  !REQUIRED true\n" +
"  !BRIEF Input file name\n" +
"  !KEYLESS 1\n" +
"!END\n" +
"!PARAMETER -o\n" +
"  !ALIAS -out\n" +
"  !ALIAS -output\n" +
"  !TYPE string\n" +
"  !REQUIRED true\n" +
"  !BRIEF Output file name\n" +
"  !KEYLESS 2\n" +
"!END\n" +
"!PARAMETER -verbose\n" +
"  !ALIAS -v\n" +
"  !TYPE bool\n" +
"  !DEFAULT false\n" +
"  !BRIEF Verbose output\n" +
"!END\n" +
"!PARAMETER -context\n" +
"  !ALIAS -c\n" +
"  !TYPE string\n" +
"  !DEFAULT 0\n" +
"  !BRIEF Chemistry context for output\n" +
"!END\n" +
"!PARAMETER -minpairs\n" +
"  !TYPE int\n" +
"  !DEFAULT 0\n" +
"  !BRIEF require at least -minpairs to apply the transformations (default: all)\n" +
"!END\n";
}
