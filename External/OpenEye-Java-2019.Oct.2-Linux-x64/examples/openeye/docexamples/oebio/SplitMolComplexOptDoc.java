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
import java.net.URL;

import openeye.oebio.*;
import openeye.oechem.*;

public class SplitMolComplexOptDoc {

    private static void WriteResultMols(oemolostream oms,
                                        OEMolBase lig,
                                        OEMolBase prot,
                                        OEMolBase wat,
                                        OEMolBase other) {
        if (! (lig.NumAtoms() == 0))
          oechem.OEWriteMolecule(oms, lig);

        if (! (prot.NumAtoms() == 0))
          oechem.OEWriteMolecule(oms, prot);

        if (! (wat.NumAtoms() == 0))
          oechem.OEWriteMolecule(oms, wat);

        if (! (other.NumAtoms() == 0))
          oechem.OEWriteMolecule(oms, other);
    }

    private static void SplitMolComplex(oemolostream oms,
                                        OEMolBase inmol,
                                        boolean splitCovalent,
                                        boolean separateResidues) {
        // limiting lig, prot and wat to just near the first binding site
        // (a very common pattern)

        if ((! splitCovalent) && (! separateResidues)) {
            // for input molecule inmol ...
            OEGraphMol lig   = new OEGraphMol();
            OEGraphMol prot  = new OEGraphMol();
            OEGraphMol wat   = new OEGraphMol();
            OEGraphMol other = new OEGraphMol();

            if (oebio.OESplitMolComplex(lig, prot, wat, other, inmol))
                //work with the output molecules lig, prot, ...
                WriteResultMols(oms, lig, prot, wat, other);
        }
        else if (splitCovalent && (! separateResidues)) {
            OEGraphMol lig   = new OEGraphMol();
            OEGraphMol prot  = new OEGraphMol();
            OEGraphMol wat   = new OEGraphMol();
            OEGraphMol other = new OEGraphMol();

            // given input and output molecules ...
            OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
            opt.SetSplitCovalent();

            if (oebio.OESplitMolComplex(lig, prot, wat, other, inmol, opt))
                // ...
                WriteResultMols(oms, lig, prot, wat, other);
        }
        else if ((! splitCovalent) && separateResidues) {
            OEGraphMol lig   = new OEGraphMol();
            OEGraphMol prot  = new OEGraphMol();
            OEGraphMol wat   = new OEGraphMol();
            OEGraphMol other = new OEGraphMol();

            OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
            opt.SetSeparateResidues();

            if (! oebio.OESplitMolComplex(lig, prot, wat, other, inmol, opt))
                oechem.OEThrow.Fatal("Unable to split mol complex from " + inmol.GetTitle());
            else
              WriteResultMols(oms, lig, prot, wat, other);
        }
        else {
            OEGraphMol lig   = new OEGraphMol();
            OEGraphMol prot  = new OEGraphMol();
            OEGraphMol wat   = new OEGraphMol();
            OEGraphMol other = new OEGraphMol();

            OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
            opt.SetSplitCovalent(splitCovalent);
            opt.SetSeparateResidues(separateResidues);

            if (! oebio.OESplitMolComplex(lig, prot, wat, other, inmol, opt))
                oechem.OEThrow.Fatal("Unable to split mol complex from " + inmol.GetTitle());
            else
                WriteResultMols(oms, lig, prot, wat, other);
        }
    }

    private static void SplitMolComplexAllSites(oemolostream oms,
                                                OEMolBase inmol,
                                                boolean splitCovalent) {
        OEGraphMol lig   = new OEGraphMol();
        OEGraphMol prot  = new OEGraphMol();
        OEGraphMol wat   = new OEGraphMol();
        OEGraphMol other = new OEGraphMol();

        if (splitCovalent) {
            OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
            int allSites = 0;
            opt.ResetFilters(allSites);
            opt.SetSplitCovalent();

            if (oebio.OESplitMolComplex(lig, prot, wat, other, inmol, opt))
                WriteResultMols(oms, lig, prot, wat, other);
        }
        else {
            OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
            int allSites = 0;
            opt.ResetFilters(allSites);
            if (oebio.OESplitMolComplex(lig, prot, wat, other, inmol, opt))
                WriteResultMols(oms, lig, prot, wat, other);
        }
    }

    private static void SplitMolComplexCombineFilters(oemolostream oms,
                                                      OEMolBase inmol) {
        OEGraphMol lig   = new OEGraphMol();
        OEGraphMol prot  = new OEGraphMol();
        OEGraphMol wat   = new OEGraphMol();
        OEGraphMol other = new OEGraphMol();

        OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
        OEUnaryRoleSetPred p = opt.GetProteinFilter();
        OEUnaryRoleSetPred w = opt.GetWaterFilter();
        opt.SetProteinFilter(new OEOrRoleSet(p, w));

        opt.SetWaterFilter(
             oebio.OEMolComplexFilterFactory(OEMolComplexFilterCategory.Nothing));

        if (oebio.OESplitMolComplex(lig, prot, wat, other, inmol, opt))
            WriteResultMols(oms, lig, prot, wat, other);
    }

    public static void main(String[] argv) {
        URL fileURL = SplitMolComplexOptDoc.class.getResource("SplitMolComplexOptDoc.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        oebio.OEConfigureSplitMolComplexOptions(itf);

        if (!oechem.OEParseCommandLine(itf, argv, "SplitMolComplexOptDoc"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        String iname = itf.GetString("-in");
        String oname = itf.GetString("-out");

        oemolistream ims = new oemolistream();
        if (!ims.open(iname))
            oechem.OEThrow.Fatal("Cannot open input file!");

        oemolostream oms = new oemolostream();
        if (!oms.open(oname))
            oechem.OEThrow.Fatal("Cannot open output file!");

        OEGraphMol inmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ims, inmol))
            oechem.OEThrow.Fatal("Unable to read molecule from " + iname);
        ims.close();

        boolean allSites         = itf.GetBool("-a");
        boolean splitCovalent    = itf.GetBool("-c");
        boolean separateResidues = itf.GetBool("-s");
        boolean combineFilters   = itf.GetBool("-pw");
        if (combineFilters)
            SplitMolComplexCombineFilters(oms, inmol);
        else if (allSites)
            SplitMolComplexAllSites(oms, inmol, splitCovalent);
        else
            SplitMolComplex(oms, inmol, splitCovalent, separateResidues);

        oms.close();
    }
}
//@ </SNIPPET>
