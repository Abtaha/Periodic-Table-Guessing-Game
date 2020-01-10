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
/****************************************************************************
 *  Aligns molecules to a smarts pattern.
 ****************************************************************************/
package openeye.examples.oedepict;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class SMARTSAlign2D {

    public static void main(String argv[]) {
        URL fileURL = SMARTSAlign2D.class.getResource("SMARTSAlign2D.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface(fileURL, "SMARTSAlign2D", argv);
        }
        catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        String iname = itf.GetString("-in");
        String oname = itf.GetString("-out");
        String smarts = itf.GetString("-smarts");

        OEQMol qmol = new OEQMol();
        if (!oechem.OEParseSmarts(qmol, smarts))
            oechem.OEThrow.Fatal("Invalid SMARTS: " + smarts);

        oechem.OEGenerate2DCoordinates(qmol);

        OESubSearch ss = new OESubSearch(qmol);
        if (!ss.IsValid())
            oechem.OEThrow.Fatal("Unable to initialize substructure search!");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(iname))
            oechem.OEThrow.Fatal("Cannot open reference molecule file!");

        oemolostream ofs = new oemolostream();
        if (!ofs.open(oname))
            oechem.OEThrow.Fatal("Cannot open output file!");
        if (!oechem.OEIs2DFormat(ofs.GetFormat()))
            oechem.OEThrow.Fatal("Invalid output format for 2D coordinates");

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEPrepareSearch(mol, ss);

            OEAlignmentResult alignres = new OEAlignmentResult(oedepict.OEPrepareAlignedDepiction(mol, ss));
            if (!alignres.IsValid())
            {
                oechem.OEThrow.Warning("Substructure is not found in input molecule!");
                oedepict.OEPrepareDepiction(mol);
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
        ifs.close();
        ofs.close();
    }
}
