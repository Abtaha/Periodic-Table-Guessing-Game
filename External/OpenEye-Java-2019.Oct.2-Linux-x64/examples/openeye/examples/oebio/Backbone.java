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

public class Backbone {

    private void displayUsage() {
        System.err.println("usage: Backbone <inmol> <outmol>");
        System.exit(1);
    }

    public void makeBackbone(String infile, String outfile) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(infile)) {
            System.err.println("Unable to open infile: " + infile);
            return;
        }
        oemolostream ofs = new oemolostream();
        if (!ofs.open(outfile)) {
            System.err.println("Unable to open outfile: " + outfile);
            return;
        }

        OEGraphMol mol = new OEGraphMol();
        boolean adjustHCount = true;

        while (oechem.OEReadMolecule(ifs, mol)) {
            if (! oechem.OEHasResidues(mol))
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);
            OEAtomBaseIter atomiter = mol.GetAtoms(new OEIsBackboneAtom());
            OEIsAtomMember member = new OEIsAtomMember(atomiter);

            OEGraphMol backboneMol = new OEGraphMol();
            oechem.OESubsetMol(backboneMol, mol, member, adjustHCount);
            oechem.OEWriteMolecule(ofs, backboneMol);
        }
        ifs.close();
        ofs.close();
    }

    public static void main(String argv[]) {
        Backbone app = new Backbone();
        if (argv.length != 2) {
            app.displayUsage();
        }
        app.makeBackbone(argv[0], argv[1]);
    }
}
