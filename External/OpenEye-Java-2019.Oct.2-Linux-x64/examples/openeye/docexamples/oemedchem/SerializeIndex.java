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

import openeye.oechem.*;
import openeye.oemedchem.*;

//****************************************************************
//* USED TO GENERATE CODE SNIPPETS FOR THE OEMEDCHEM DOCUMENTATION
//* - please look at actual examples for useful utility code
//****************************************************************

public class SerializeIndex {
    static String InterfaceData =
            "!CATEGORY SerializeIndex\n" +
                    "    !CATEGORY I/O\n" +
                    "        !PARAMETER -input 1\n" +
                    "          !ALIAS -i\n" +
                    "          !TYPE string\n" +
                    "          !REQUIRED true\n" +
                    "          !BRIEF Input filename of structures to index\n" +
                    "          !KEYLESS 1\n" +
                    "        !END\n" +
                    "        !PARAMETER -output 2\n" +
                    "          !ALIAS -o\n" +
                    "          !TYPE string\n" +
                    "          !REQUIRED true\n" +
                    "          !BRIEF Output filename for serialized index\n" +
                    "          !KEYLESS 2\n" +
                    "        !END\n" +
                    "    !END\n" +
                    "!END\n";

    private static void SerializeInput(OEInterface itf) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-input")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-input") + " for reading");

        oechem.OEThrow.SetLevel(OEErrorLevel.Warning);

        {
            // create analyzer class with defaults
            //   - compression option disabled by default
            OEMatchedPairAnalyzer mmpAnalyzer = new OEMatchedPairAnalyzer();
            // for serialization, enable export compression to
            //   remove singleton index nodes by modifying analyzer
            mmpAnalyzer.ModifyOptions(OEMatchedPairOptions.ExportCompression, 0);
        }

        {
            // create options class with defaults
            //   - compression option disabled by default
            OEMatchedPairAnalyzerOptions mmpOpts = new OEMatchedPairAnalyzerOptions();
            // for serialization, enable export compression to
            //   remove singleton index nodes by modifying analyzer
            mmpOpts.SetOptions(OEMatchedPairOptions.Default |
                    OEMatchedPairOptions.ExportCompression);
            // create analyzer class with compression option enabled
            OEMatchedPairAnalyzer mmpAnalyzer = new OEMatchedPairAnalyzer(mmpOpts);
        }

        OEMatchedPairAnalyzer mmp = new OEMatchedPairAnalyzer();
        int recindex = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            ++recindex;

            int status = mmp.AddMol(mol, recindex);
            if (status != recindex) {
                oechem.OEThrow.Warning(recindex + ": molecule indexing error, status=" +
                        oemedchem.OEMatchedPairIndexStatusName(status));
            }
        }

        System.out.println("Index complete, matched pairs = " + mmp.NumMatchedPairs());

        // check for output filename with .mmpidx extension
        String mmpexport = itf.GetString("-output");
        if (!oemedchem.OEIsMatchedPairAnalyzerFileType(mmpexport))
            oechem.OEThrow.Info("Not a valid matched pair index output file, " + mmpexport);
        else if (!oemedchem.OEWriteMatchedPairAnalyzer(mmpexport, mmp))
            oechem.OEThrow.Fatal("Index serialization failed");
        else
            oechem.OEThrow.Info("Index serialization complete");
        System.out.println("Index serialization complete");

        String mmpimport = mmpexport;
        // now try to reload serialized index
        OEMatchedPairAnalyzer mmpimp = new OEMatchedPairAnalyzer();
        if (!oemedchem.OEIsMatchedPairAnalyzerFileType(mmpimport))
            oechem.OEThrow.Fatal("Not a valid matched pair index input file, " + mmpimport);
        else if (!oemedchem.OEReadMatchedPairAnalyzer(mmpimport, mmpimp))
            oechem.OEThrow.Fatal("Index deserialization failed");
        else
            oechem.OEThrow.Info("Index deserialization complete");
        System.out.println("Index deserialization complete");
    }

    public static void main(String argv[]) {
        OEInterface itf = new OEInterface();

        if (!oechem.OEConfigure(itf, InterfaceData))
            oechem.OEThrow.Fatal("Problem configuring OEInterface!");

        if (!oechem.OEParseCommandLine(itf, argv, "SerializeInput"))
            oechem.OEThrow.Fatal("Unable to parse command line");

        SerializeInput(itf);
    }
}
