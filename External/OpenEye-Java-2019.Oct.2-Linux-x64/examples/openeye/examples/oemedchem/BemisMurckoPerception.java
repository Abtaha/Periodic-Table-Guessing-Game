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
 * Utility to fragment the input structures by Bemis-Murcko rules
 * ---------------------------------------------------------------------------
 * BemisMurckoPerception.py [ -uncolor ] [-i] <input_mols> [-o] <output_mols>
 *
 * input_mols: filename of molecules to fragment and uncolor
 * output_mols: filename of ouput structures annotated with SD data of perceived regions
 * [ -uncolor ]: optional arg to request uncoloring of output fragment info
 *****************************************************************************/
package openeye.examples.oemedchem;

import openeye.oechem.*;
import openeye.oemedchem.*;

public class BemisMurckoPerception {
    public static void main(String[] args) {
        OEInterface itf = new OEInterface(interfaceData, "BemisMurckoPerception", args);

        // flag on command line indicates uncoloring option or not;
        boolean bUncolor = itf.GetBool("-uncolor");

        // input structure(s) to transform;
        oemolistream ifsmols = new oemolistream();
        if (!ifsmols.open(itf.GetString("-i")))
            oechem.OEThrow.Fatal("Unable to open file for reading: " + itf.GetString("-i"));

        // save output structure(s) to this file;
        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-o")))
            oechem.OEThrow.Fatal("Unable to open file for writing: " + itf.GetString("-o"));
        if (!oechem.OEIsSDDataFormat(ofs.GetFormat()))
            oechem.OEThrow.Fatal("Output file format does not support SD data: " + itf.GetString("-o"));

        int irec = 0;
        int ototal = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifsmols,mol)) {
            ++irec;
            oechem.OEDeleteEverythingExceptTheFirstLargestComponent(mol);

            int regions = 0;
            OEGraphMol frag = new OEGraphMol();
            for (OEAtomBondSet abset : oemedchem.OEGetBemisMurcko(mol)) {
                ++regions;
                // create a fragment from the perceived region;
                oechem.OESubsetMol(frag, mol, abset, true);
                if (bUncolor) {
                    // ignore 3D stereo parities
                    if (frag.GetDimension() == 3) {
                        frag.SetDimension(0);
                    }
                    // uncolor the fragment;
                    oechem.OEUncolorMol(frag);
                }
                final String smi = oechem.OEMolToSmiles(frag);
                // annotate the input molecule with the role information;
                for (OERole role : abset.GetRoles())
                    oechem.OEAddSDData(mol, role.GetName(), smi);

            }
            if (regions == 0) {
                String name = mol.GetTitle();
                if (name.length() == 0)
                    name = "Record " + irec;
                oechem.OEThrow.Warning(name + ": no perceived regions");
            }
            else {
                ++ototal;
                oechem.OEWriteMolecule(ofs, mol);
            }
        }
        if (irec == 0)
            oechem.OEThrow.Fatal("No records in input structure file to perceive");

        if (ototal ==0)
            oechem.OEThrow.Warning("No annotated structures generated");

        System.out.printf("Input molecules=%d, output annotated %smolecules=%d%n",
                          irec, ((bUncolor) ? "(uncolored) " : ""), ototal);

    }
    private static String interfaceData =
"!BRIEF  [ -uncolor ] [-i] <infile1> [-o] <infile2>\n" +
"!PARAMETER -i\n" +
"  !ALIAS -in\n" +
"  !ALIAS -input\n" +
"  !TYPE string\n" +
"  !REQUIRED true\n" +
"  !BRIEF Input structure file name\n" +
"  !KEYLESS 1\n" +
"!END\n" +
"!PARAMETER -o\n" +
"  !ALIAS -out\n" +
"  !ALIAS -output\n" +
"  !TYPE string\n" +
"  !REQUIRED true\n" +
"  !BRIEF Output SD file name\n" +
"  !KEYLESS 2\n" +
"!END\n" +
"!PARAMETER -uncolor\n" +
"  !ALIAS -u\n" +
"  !TYPE bool\n" +
"  !DEFAULT false\n" +
"  !BRIEF Uncolor output molecules\n" +
"!END\n";
}
