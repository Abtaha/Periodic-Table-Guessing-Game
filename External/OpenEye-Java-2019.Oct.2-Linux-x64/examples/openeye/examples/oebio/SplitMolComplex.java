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
// Split a mol complex (a PDB structure, for example) into basic categories

package openeye.examples.oebio;

import java.io.IOException;
import java.net.URL;

import openeye.oebio.*;
import openeye.oechem.*;

public class SplitMolComplex {

    public static void main(String[] argv) {
        URL fileURL = SplitMolComplex.class.getResource("SplitMolComplex.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        oebio.OEConfigureSplitMolComplexOptions(itf);

        if (!oechem.OEParseCommandLine(itf, argv, "SplitMolComplex"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        String iname = itf.GetString("-in");
        String oname = itf.GetString("-out");

        oemolistream ims = new oemolistream();
        if (itf.GetUnsignedInt("-modelnum") != 1)
            ims.SetFlavor(OEFormat.PDB,
                          oechem.OEGetDefaultIFlavor(OEFormat.PDB)
                                            & ~OEIFlavor.PDB.ENDM);
        if (!ims.open(iname))
            oechem.OEThrow.Fatal("Cannot open input file!");

        oemolostream oms = new oemolostream();
        if (!oms.open(oname))
            oechem.OEThrow.Fatal("Cannot open output file!");

        OEGraphMol inmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ims, inmol))
            oechem.OEThrow.Fatal("Unable to read molecule from " + iname);
        ims.close();

        OESplitMolComplexOptions opts = new OESplitMolComplexOptions();
        oebio.OESetupSplitMolComplexOptions(opts, itf);

        if (itf.GetBool("-verbose")) {
            // don't bother counting sites unless we're going to print them
            int numSites = oebio.OECountMolComplexSites(inmol, opts);
            oechem.OEThrow.SetLevel(OEErrorLevel.Verbose);
            oechem.OEThrow.Verbose("sites " + numSites);
        }

        OEGraphMol lig   = new OEGraphMol();
        OEGraphMol prot  = new OEGraphMol();
        OEGraphMol wat   = new OEGraphMol();
        OEGraphMol other = new OEGraphMol();

        if (! oebio.OESplitMolComplex(lig, prot, wat, other, inmol, opts))
            oechem.OEThrow.Fatal("Unable to split mol complex from " + iname);

        if (! (lig.NumAtoms() == 0)) {
          oechem.OEThrow.Verbose("  lig " + lig.GetTitle());
          oechem.OEWriteMolecule(oms, lig);
        }

        if (! (prot.NumAtoms() == 0)) {
          oechem.OEThrow.Verbose(" prot " + prot.GetTitle());
          oechem.OEWriteMolecule(oms, prot);
        }

        if (! (wat.NumAtoms() == 0)) {
          oechem.OEThrow.Verbose("  wat " + wat.GetTitle());
          oechem.OEWriteMolecule(oms, wat);
        }

        if (! (other.NumAtoms() == 0)) {
          oechem.OEThrow.Verbose("other " + other.GetTitle());
          oechem.OEWriteMolecule(oms, other);
        }

        oms.close();
    }
}
