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

public class FindSimpleMatchedPairs {
    static String InterfaceData =
            "!CATEGORY FindSimpleMatchedPairs\n" +
                    "    !CATEGORY I/O\n" +
                    "        !PARAMETER -input 1\n" +
                    "          !ALIAS -i\n" +
                    "          !TYPE string\n" +
                    "          !REQUIRED true\n" +
                    "          !BRIEF Input filename of structures to analyze\n" +
                    "          !KEYLESS 1\n" +
                    "        !END\n" +
                    "        !PARAMETER -output 2\n" +
                    "          !ALIAS -o\n" +
                    "          !TYPE string\n" +
                    "          !BRIEF Output filename for simple pair transformations\n" +
                    "        !END\n" +
                    "        !PARAMETER -maxrec 3\n" +
                    "          !TYPE int\n" +
                    "          !DEFAULT 0\n" +
                    "          !BRIEF Only process maxrec records from input file\n" +
                    "        !END\n" +
                    "    !END\n" +
                    "!END\n";

    private static void FindSimpleMatchedPairs(OEInterface itf) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-input")))
            oechem.OEThrow.Fatal("Unable to open %s for reading: " +
                    itf.GetString("-input"));

        int maxrecs = itf.GetInt("-maxrec");

        oechem.OEThrow.SetLevel(OEErrorLevel.Warning);

        // create options class with defaults
        OEMatchedPairAnalyzerOptions mmpOpts = new OEMatchedPairAnalyzerOptions();

        // for 'simple' pairs, alter default indexing options
        // - single cuts only, heavy atom substituents only (HMember indexing off)
        mmpOpts.SetOptions(OEMatchedPairOptions.SingleCuts |
                OEMatchedPairOptions.ComboCuts |
                OEMatchedPairOptions.UniquesOnly);
        // - limit substituent size to no more than 20% of input structure
        mmpOpts.SetIndexableFragmentRange(80.f, 100.f);

        // create analyzer class with nondefault options
        OEMatchedPairAnalyzer mmpAnalyzer = new OEMatchedPairAnalyzer(mmpOpts);

        // ignore common index status returns
        String sIgnoreStatus = "FragmentRangeFilter,DuplicateStructure,FragmentationLimitFilter,HeavyAtomFilter";

        // index the input structures
        int recindex = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            ++recindex;

            // consider only the largest input fragment
            oechem.OEDeleteEverythingExceptTheFirstLargestComponent(mol);

            // ignore stereochemistry
            oechem.OEUncolorMol(mol,
                    (OEUncolorStrategy.RemoveAtomStereo | OEUncolorStrategy.RemoveBondStereo));

            // explicitly provide a 1-based index to refer to indexed structures
            //   - to allow references back to external data elsewhere
            int status = mmpAnalyzer.AddMol(mol, recindex);
            if (status != recindex) {
                if (!sIgnoreStatus.contains(oemedchem.OEMatchedPairIndexStatusName(status))) {
                    oechem.OEThrow.Warning(recindex + ": molecule indexing error, status=" +
                            oemedchem.OEMatchedPairIndexStatusName(status));
                }
            }
            // if limiting input, quit after limit
            if (maxrecs != 0 && recindex >= maxrecs)
                break;
        }

        System.out.println("Index complete, matched pairs = " + mmpAnalyzer.NumMatchedPairs());

        OEMatchedPairTransformExtractOptions extractOptions = new OEMatchedPairTransformExtractOptions();
        // specify amount of chemical context at the site of the substituent change
        //   in the transform
        extractOptions.SetContext(OEMatchedPairContext.Bond0);
        // specify how transforms are extracted (direction and allowed properties)
        extractOptions.SetOptions(OEMatchedPairTransformExtractMode.Sorted |
                                  OEMatchedPairTransformExtractMode.NoSMARTS |
                                  OEMatchedPairTransformExtractMode.AddMCSCorrespondence);

        // walk the transforms and print the matched pairs
        int xfmidx = 0;
        for (OEMatchedPairTransform mmpxform :
                 oemedchem.OEMatchedPairGetTransforms(mmpAnalyzer, extractOptions)) {
            ++xfmidx;
            System.out.println(xfmidx + ": " + mmpxform.GetTransform());
            // dump matched molecular pairs and index identifiers
            //   (recindex from indexing loop above)
            for (OEMatchedPair mmppair : mmpxform.GetMatchedPairs()) {
                System.out.println("\tmatched pair molecule indices=(" + mmppair.FromIndex() + "," + mmppair.ToIndex() + ")");
            }

        }
    }

    public static void main(String argv[]) {
        OEInterface itf = new OEInterface();

        if (!oechem.OEConfigure(itf, InterfaceData))
            oechem.OEThrow.Fatal("Problem configuring OEInterface!");

        if (!oechem.OEParseCommandLine(itf, argv, "FindSimpleMatchedPairs"))
            oechem.OEThrow.Fatal("Unable to parse command line");

        FindSimpleMatchedPairs(itf);
    }
}
