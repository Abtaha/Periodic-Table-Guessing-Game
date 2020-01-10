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
package openeye.docexamples.oedepict;

import openeye.oechem.*;
import openeye.oedepict.*;

public class AtomVisibility {

    static class OEAtomVisibilityShowRxnRole extends OEUnaryAtomPred {
        private int rxnrole = OERxnRole.None;

        public OEAtomVisibilityShowRxnRole(int role) { rxnrole = role; }

        @Override
        public boolean constCall(OEAtomBase atom) {
            if (rxnrole == OERxnRole.None) {
                    return true;
            }
            return (rxnrole == atom.GetRxnRole());
        }

        @Override
        public OEUnaryAtomPred CreateCopy() {
            OEUnaryAtomPred copy = new OEAtomVisibilityShowRxnRole(rxnrole);
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("AtomVisibility <rxnfile> <imagefile>");
        }
        oemolistream ifs = new oemolistream(argv[0]);

        OEGraphMol mol = new OEGraphMol();
        if (!oechem.OEReadRxnFile(ifs,mol)) {
            oechem.OEThrow.Fatal("error reading rxnfile");
        }
        ifs.close();

        oedepict.OEPrepareDepiction(mol);

        OEImage image = new OEImage(900, 100);

        int rows = 1;
        int cols = 3;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(), grid.GetCellHeight(), OEScale.AutoScale);

        OEImageBase cell = grid.GetCell(1,1);
        mol.SetTitle("Reaction display");
        OEUnaryAtomPred allatoms = new OEIsTrueAtom(); // explicitly set the default
        opts.SetAtomVisibilityFunctor(allatoms);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        double rxnscale = disp.GetScale();
        oedepict.OERenderMolecule(cell, disp);

        cell = grid.GetCell(1,2);
        mol.SetTitle("Reactant display");
        OEAtomVisibilityShowRxnRole showreacs = new OEAtomVisibilityShowRxnRole(OERxnRole.Reactant);
        opts.SetAtomVisibilityFunctor(showreacs);
        opts.SetScale(rxnscale);
        disp = new OE2DMolDisplay(mol, opts);
        oedepict.OERenderMolecule(cell, disp);

        cell = grid.GetCell(1,3);
        mol.SetTitle("Product display");
        OEAtomVisibilityShowRxnRole showprods = new OEAtomVisibilityShowRxnRole(OERxnRole.Product);
        opts.SetAtomVisibilityFunctor(showprods);
        opts.SetScale(rxnscale);
        disp = new OE2DMolDisplay(mol, opts);
        oedepict.OERenderMolecule(cell, disp);

        oedepict.OEWriteImage(argv[1], image);
    }
}

