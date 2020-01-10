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

import java.io.*;
import openeye.oechem.*;

public class SimpleResCountDoc {

    static void CalcResCounts(OEMolBase mol) {
        if (! oechem.OEHasResidues(mol))
            oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

        int resCt = 0;
        int hetCt = 0;
        OEResidue prevRes = new OEResidue();
        for (OEAtomBase atom : mol.GetAtoms())
        {
            OEResidue thisRes = oechem.OEAtomGetResidue(atom);
            if (! oechem.OESameResidue(prevRes, thisRes)) {
                ++resCt;
                if (thisRes.IsHetAtom()) {
                    ++hetCt;
                }
                prevRes = thisRes;
            }
        }
        System.out.println("Molecule: " + mol.GetTitle());
        System.out.println("Residues: " + resCt + " (" + hetCt + " hets)");
    }
    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("SimpleResCount <mol-infile>");
        }
        oemolistream ims = new oemolistream();
        if (! ims.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ims, mol))
        {
            CalcResCounts(mol);
        }
        ims.close();
    }
}
//@ </SNIPPET>
