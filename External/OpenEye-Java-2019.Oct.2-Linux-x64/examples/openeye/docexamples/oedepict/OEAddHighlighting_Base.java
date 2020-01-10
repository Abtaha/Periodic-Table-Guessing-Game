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

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
 **************************************************************/

public class OEAddHighlighting_Base {
    static class Pred6MemAromAtom extends OEUnaryAtomPred {
        @Override
        public boolean constCall(OEAtomBase atom) {
            return atom.IsAromatic() && oechem.OEAtomIsInAromaticRingSize(atom, 6);
        }

        @Override
        public OEUnaryAtomBoolFunc CreateCopy() {
            OEUnaryAtomBoolFunc copy = new Pred6MemAromAtom();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    static class Pred6MemAromBond extends OEUnaryBondPred {
        @Override
        public boolean constCall(OEBondBase bond) {
            return bond.IsAromatic() && oechem.OEBondIsInAromaticRingSize(bond, 6);
        }

        @Override
        public OEUnaryBondBoolFunc CreateCopy() {
            OEUnaryBondBoolFunc copy = new Pred6MemAromBond();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    private static void OEAddHighlighting_Predicate(OE2DMolDisplay disp) {
        OEHighlightByBallAndStick highlightstyle = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        oedepict.OEAddHighlighting(disp, highlightstyle, new Pred6MemAromAtom());
        oedepict.OEAddHighlighting(disp, highlightstyle, new Pred6MemAromBond());
    }

    private static void OEAddHighlighting_AtomAndBondPredicate(OE2DMolDisplay disp) {
        OEHighlightByBallAndStick highlightstyle = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        oedepict.OEAddHighlighting(disp, highlightstyle, new Pred6MemAromAtom(), new Pred6MemAromBond());
    }


    private static void OEAddHighlighting_OEMatch(OE2DMolDisplay disp) {
        OESubSearch subs = new OESubSearch("a1aaaaa1");
        boolean unique = true;
        OEHighlightByBallAndStick highlightstyle = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        for (OEMatchBase match : subs.Match(disp.GetMolecule(), unique)) {
            oedepict.OEAddHighlighting(disp, highlightstyle, match);
        }
    }


    private static void OEAddHighlighting_OEAtomBondSet(OE2DMolDisplay disp) {
        OEMolBase mol = disp.GetMolecule();
        OEAtomBondSet abset = new OEAtomBondSet(mol.GetAtoms(new Pred6MemAromAtom()),
                mol.GetBonds(new Pred6MemAromBond()));
        OEHighlightByBallAndStick highlightstyle = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        oedepict.OEAddHighlighting(disp, highlightstyle, abset);
    }


    private static void OEAddHighlighting_OESubSearch(OE2DMolDisplay disp) {
        OESubSearch subsearch = new OESubSearch("a1aaaaa1");
        OEHighlightByBallAndStick highlightstyle = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        oedepict.OEAddHighlighting(disp, highlightstyle, subsearch);
    }

    ///////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1ccnc2c1[nH]c3c2cccc3");
        oedepict.OEPrepareDepiction(mol);

        int width  = 250;
        int height = 180;
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        opts.SetTitleLocation(OETitleLocation.Hidden);

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddHighlighting_Predicate(disp);
            oedepict.OERenderMolecule("OEAddHighlighting-OEHighlightBase-Predicate.png", disp);
        }

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddHighlighting_AtomAndBondPredicate(disp);
            oedepict.OERenderMolecule("OEAddHighlighting-OEHighlightBase-AtomAndBondPredicate.png", disp);
        }

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddHighlighting_OEMatch(disp);
            oedepict.OERenderMolecule("OEAddHighlighting-OEHighlightBase-OEMatch.png", disp);
        }

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddHighlighting_OEAtomBondSet(disp);
            oedepict.OERenderMolecule("OEAddHighlighting-OEHighlightBase-OEAtomBondSet.png", disp);
        }

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddHighlighting_OESubSearch(disp);
            oedepict.OERenderMolecule("OEAddHighlighting-OEHighlightBase-OESubSearch.png", disp);
        }
    }
}
