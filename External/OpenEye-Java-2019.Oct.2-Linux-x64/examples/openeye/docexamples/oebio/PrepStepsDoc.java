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

public class PrepStepsDoc {

    public static void main(String[] argv) {
        URL fileURL = PrepStepsDoc.class.getResource("PrepStepsDoc.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        if (!oechem.OEParseCommandLine(itf, argv, "PrepStepsDoc"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        oemolistream ims = new oemolistream();
        ims.SetFlavor(OEFormat.PDB, OEIFlavor.PDB.ALTLOC);

        String inputFile = itf.GetString("-in");
        if (! ims.open(inputFile))
            oechem.OEThrow.Fatal("Unable to open " +
                                  inputFile + " for reading.");

        if (! oechem.OEIs3DFormat(ims.GetFormat()))
            oechem.OEThrow.Fatal(inputFile + " is not in a 3D format.");

        OEGraphMol mol = new OEGraphMol();
        if (! oechem.OEReadMolecule(ims, mol))
            oechem.OEThrow.Fatal("Unable to read " + inputFile + ".");

        if (! oechem.OEHasResidues(mol))
            oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

        OEAltLocationFactory alf = new OEAltLocationFactory(mol);
        if (alf.GetGroupCount() != 0)
            alf.MakePrimaryAltMol(mol);

        if (oebio.OEPlaceHydrogens(mol)) {
            // ....
        }
        OEPlaceHydrogensOptions opt = new OEPlaceHydrogensOptions();
        opt.SetStandardizeBondLen(false);

        // given molecule mol and OEPlaceHydrogensOptions opt...
        OEPlaceHydrogensDetails details = new OEPlaceHydrogensDetails();
        if (oebio.OEPlaceHydrogens(mol, details, opt))
            System.out.println(details.Describe());

        ims.close();
    }
}
//@ </SNIPPET>
