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
package openeye.docexamples.oemedchem;

import java.util.*;

import openeye.oechem.*;
import openeye.oemedchem.*;

//****************************************************************
//* USED TO GENERATE CODE SNIPPETS FOR THE OEMEDCHEM DOCUMENTATION
//* - please look at actual examples for useful utility code
//****************************************************************

public class ChEMBLSolubilityUsage {
    static String InterfaceData =
            "!CATEGORY ChEMBLSolubilityUsage\n" +
                    "    !CATEGORY I/O\n" +
                    "        !PARAMETER -input 1\n" +
                    "          !ALIAS -i\n" +
                    "          !TYPE string\n" +
                    "          !REQUIRED true\n" +
                    "          !BRIEF Input filename of structures to transform\n" +
                    "          !KEYLESS 1\n" +
                    "        !END\n" +
                    "        !PARAMETER -output 2\n" +
                    "          !ALIAS -o\n" +
                    "          !TYPE string\n" +
                    "          !REQUIRED true\n" +
                    "          !BRIEF Output filename for solubility transformed molecules\n" +
                    "          !KEYLESS 2\n" +
                    "        !END\n" +
                    "    !END\n" +
                    "!END\n";

    static private double average(ArrayList<Double> values) {
        if (values.size() == 0)
            return 0.0;
        if (values.size() == 1)
            return values.get(0);
        double sumval = 0.0;
        for (double dval : values) {
            sumval += dval;
        }
        return (sumval / (double)values.size());
    }

    static private double stddev(ArrayList<Double> values) {
        if (values.size() <= 1)
            return 0.0;
        double avg = average(values);

        ArrayList<Double> vars = new ArrayList<Double>();
        for (double dval : values) {
            vars.add(Math.pow((avg - dval),2));
        }
        return Math.sqrt(average(vars));
    }

    private static void ChEMBLSolubilityUsage(OEInterface itf) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-input"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-input") + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-output"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-output") + " for writing: ");
        }

        oechem.OEThrow.SetLevel(OEErrorLevel.Warning);

        // number of bonds of chemistry context at site of change
        //  for the applied transforms
        int totalmols = 0;
        int xformctxt = OEMatchedPairContext.Bond2;
        int molidx = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            ++molidx;

            // consider only the largest input fragment
            oechem.OEDeleteEverythingExceptTheFirstLargestComponent (mol);

            int smolcnt = 0;
            // only consider solubility transforms having at least 5 matched pairs
            for (OEMolBase solMol : oemedchem.OEApplyChEMBL24SolubilityTransforms(mol, xformctxt, 5)) {
                // compute net change in solubility from MMP data
                if (oechem.OEHasSDData(solMol, "OEMMP_normalized_value (uM)")) {
                    ArrayList<Double> deltasol = new ArrayList<Double>();
                    String sddata = oechem.OEGetSDData(solMol, "OEMMP_normalized_value (uM)");
                    String[] sdlines = sddata.split("\n", -1);

                    for (String sditem : sdlines) {
                        // fromIndex,toIndex,fromValue,toValue
                        String[] sdvalues = sditem.split(",");
                        if (sdvalues.length < 4)
                            continue;
                        if (sdvalues[2].length() == 0 || sdvalues[3].length() == 0)
                            continue;
                        deltasol.add(Double.parseDouble(sdvalues[3]) - Double.parseDouble(sdvalues[2]));
                    }
                    if (deltasol.size() == 0)
                        continue;

                    double avgsol = average(deltasol);

                    // reject examples with net decrease in solubility
                    if (avgsol < 0.0)
                        continue;

                    double sdev = stddev(deltasol);

                    // annotate with average,stddev,num
                    oechem.OEAddSDData(solMol,
                            "OEMMP_average_delta_normalized_value",
                            String.format("%.2f",avgsol) + "," + String.format("%.2f",sdev) + "," + deltasol.size());
                }


                // export solubility transformed molecule with SDData annotations
                if (oechem.OEWriteMolecule(ofs, solMol) == OEWriteMolReturnCode.Success)
                    smolcnt += 1;
            }

            oechem.OEThrow.Info(molidx + ": Exported molecule count, " + smolcnt);
            totalmols += smolcnt;
        }
        ifs.close();
        ofs.close();

        System.out.println("Exported molecule count = " + totalmols);
    }

    public static void main(String argv[]) {
        OEInterface itf = new OEInterface();

        if (!oechem.OEConfigure(itf, InterfaceData))
            oechem.OEThrow.Fatal("Problem configuring OEInterface!");

        if (!oechem.OEParseCommandLine(itf, argv, "ChEMBLSolubilityUsage"))
            oechem.OEThrow.Fatal("Unable to parse command line");

        ChEMBLSolubilityUsage(itf);
    }
}
