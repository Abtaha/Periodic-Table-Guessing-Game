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
package openeye.examples.oebio;

import openeye.oechem.*;
import openeye.oebio.*;

public class SubsetRes {

    private void displayUsage() {
        System.err.println("usage: SubsetRes <resname> [chain] <resnum> <inmol> <outmol>");
        System.err.println("   SubsetRes THR A 242 in.pdb out.pdb");
        System.exit(1);
    }
    private void subset(String args[]) {
        String infile, outfile;
        String resName = args[0];
        char chainID = ' ';
        int resNum=0;
        if (args.length==4) {
            resNum = args[1].charAt(0);
            infile = args[2];
            outfile = args[3];
        }
        else if (args.length == 5) {
            chainID = args[1].charAt(0);
            resNum = Integer.parseInt(args[2]);
            infile = args[3];
            outfile = args[4];      
        }
        else return;

        oemolistream ifs = new oemolistream();
        if (!ifs.open(infile)) {
            System.err.println("Unable to open input file: " + infile);
            return;
        }
        oemolostream ofs = new oemolostream();
        if (!ofs.open(outfile)) {
            System.err.println("Unable to open output file: " + outfile);
            return;
        }

        boolean adjustHCount = true;
        OEGraphMol mol = new OEGraphMol();
        OEGraphMol resMol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (! oechem.OEHasResidues(mol))
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);
            OEHierView hv = new OEHierView(mol);
            OEHierResidue res = hv.GetResidue(chainID, resName, resNum);
            OEAtomBaseIter atom = res.GetAtoms();
            OEIsAtomMember mbr = new OEIsAtomMember(atom);
            oechem.OESubsetMol(resMol, mol, mbr, adjustHCount);
            if (chainID==' ')
                mol.SetTitle(resName+" "+resNum);
            else
                mol.SetTitle(resName+" "+chainID+" "+resNum);

            oechem.OEWriteMolecule(ofs, resMol);
        }
        ifs.close();
        ofs.close();
    }

    public static void main(String args[]) {
        SubsetRes app = new SubsetRes();
        if (args.length < 4 || args.length > 5)
            app.displayUsage();
        app.subset(args);
    }
}
