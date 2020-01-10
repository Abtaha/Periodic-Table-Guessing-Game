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
import openeye.oebio.*;

public class ResCountDoc {

    static void CalcResCounts(OEMolBase mol) {
        OEHierView hv = new OEHierView(mol);
        int chainCt = 0;
        int fragCt  = 0;
        int resCt   = 0;
        int watCt   = 0;
        for (OEHierChain chain : hv.GetChains()) {
            ++chainCt;
            for (OEHierFragment frag : chain.GetFragments()) {
                ++fragCt;
                for (OEHierResidue hres : frag.GetResidues()) {
                    ++resCt;
                    if (oechem.OEGetResidueIndex(hres.GetOEResidue()) == OEResidueIndex.HOH) {
                        ++watCt;
                    }
                }
            }
        }
        System.out.println("Molecule : " + mol.GetTitle());
        System.out.println("Chains   : " + chainCt);
        System.out.println("Fragments: " + fragCt);
        System.out.println("Residues : " + resCt   + " (" + watCt + " waters)");
    }
    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("ResCountDoc <mol-infile>");
        }
        oemolistream ims = new oemolistream();
        if (! ims.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ims, mol)) {
            if (! oechem.OEHasResidues(mol))
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);
            CalcResCounts(mol);
        }
        ims.close();
    }
}
//@ </SNIPPET>
