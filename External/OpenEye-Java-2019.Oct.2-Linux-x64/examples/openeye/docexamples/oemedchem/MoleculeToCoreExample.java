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

public class MoleculeToCoreExample {
            static String InterfaceData =
                "# MoleculeToCores docexample\n" +
                "!CATEGORY MoleculeToCores\n" +
                "    !CATEGORY I/O\n" +
                "        !PARAMETER -input 1\n" +
                "          !ALIAS -i\n" +
                "          !TYPE string\n" +
                "          !REQUIRED true\n" +
                "          !BRIEF Input filename of structures to index\n" +
                "          !KEYLESS 1\n" +
                "        !END\n" +
                "    !END\n" +
                "!END\n";

    private static void MoleculeToCoreExampleUsage(OEInterface itf) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-input"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-input") + " for reading");
        }

        OEGraphMol mol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ifs, mol))
            oechem.OEThrow.Fatal("Error reading: " + itf.GetString("-input"));

        oechem.OEThrow.SetLevel(OEErrorLevel.Info);

        {
            // create an MCS fragment database with defaults
            OEMCSFragDatabase fragdb = new OEMCSFragDatabase();
            // use the options from the frag database to fragment an arbitrary input molecule
            System.out.println("MoleculeToCores using default fragment database options:");
            List<String> cores = new ArrayList<String>();
            for (String c : fragdb.MoleculeToCores(mol))
                cores.add(c);
            java.util.Collections.sort(cores);
            int corenum = 0;
            for (String core : cores)
            {
                System.out.println(corenum + ": " + core);
                ++corenum;
            }
        }

        System.out.println("");
        {
            // set the MCS fragment database options from the command-line arguments
            OEMCSFragDatabaseOptions fragopts = new OEMCSFragDatabaseOptions();
            if (!oemedchem.OEConfigureMCSFragDatabaseOptions(itf))
                oechem.OEThrow.Fatal("Error configuring options");
            if (!oemedchem.OESetupMCSFragDatabaseOptions(fragopts, itf))
                oechem.OEThrow.Fatal("Error setting options");

            // use the custom options to fragment an arbitrary input molecule
            System.out.println("MoleculeToCores using command-line options:");
            List<String> cores = new ArrayList<String>();
            for (String c : oemedchem.OEMoleculeToCores(mol, fragopts))
                cores.add(c);
            java.util.Collections.sort(cores);
            int corenum = 0;
            for (String core : cores)
            {
                System.out.println(corenum + ": " + core);
                ++corenum;
            }
        }
    }

    public static void main(String argv[]) {
        OEInterface itf = new OEInterface();

        if (!oechem.OEConfigure(itf, InterfaceData))
            oechem.OEThrow.Fatal("Problem configuring OEInterface!");

        if (!oechem.OEParseCommandLine(itf, argv, "MoleculeToCoreExample"))
            oechem.OEThrow.Fatal("Unable to parse command line");

        MoleculeToCoreExampleUsage(itf);
    }
}
