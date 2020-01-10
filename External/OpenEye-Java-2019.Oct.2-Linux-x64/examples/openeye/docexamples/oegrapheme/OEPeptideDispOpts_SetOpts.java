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
package openeye.docexamples.oegrapheme;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
 **************************************************************/

public class OEPeptideDispOpts_SetOpts {

    private static void DepictPeptide(OEPeptideDisplayOptions opts, String smiles, String basefilename) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, smiles);
        oedepict.OEPrepareDepiction(mol);

        OEImage image = new OEImage(400, 200);
        oegrapheme.OEDrawPeptide(image, mol, opts);

        oedepict.OEWriteImage(basefilename+".svg", image);
    }

    public static void main(String argv[]) {

        String smiles = "CCC(C)[C@H]1C(=O)N[C@@H](C(=O)N[C@H](C(=O)N[C@@H](C(=O)N[C@H](C(=O)NCCCC[C@@H](C(=O)N[C@@H](C(=O)N1)CCCN)NC(=O)[C@H](C(C)CC)NC(=O)[C@@H](CCC(=O)O)NC(=O)[C@H](CC(C)C)NC(=O)C2CSC(=N2)NC(C(C)CC)N)CC(=O)N)CC(=O)O)CC3=CN=C[NH]3)Cc4ccccc4 bacitracin";

        {
        OEPeptideDisplayOptions opts = new OEPeptideDisplayOptions();
        DepictPeptide(opts, smiles, "OEPeptideDisplayOptions_Default");
        }

        {
        OEPeptideDisplayOptions opts = new OEPeptideDisplayOptions();
        opts.SetLabelStyle(OEPeptideLabelStyle.SingleLetter);
        DepictPeptide(opts, smiles, "OEPeptideDisplayOptions_SetLabelStyle");
        }

        {
        OEPeptideDisplayOptions opts = new OEPeptideDisplayOptions();
        opts.SetInteractive(true);
        DepictPeptide(opts, smiles, "OEPeptideDisplayOptions_SetInteractive");
        }

        {
        OEPeptideDisplayOptions opts = new OEPeptideDisplayOptions();
        opts.SetInteractive(true);
        opts.SetAminoAcidScale(0.75);
        DepictPeptide(opts, smiles, "OEPeptideDisplayOptions_SetAminoAcidScale");
        }
    }
}
