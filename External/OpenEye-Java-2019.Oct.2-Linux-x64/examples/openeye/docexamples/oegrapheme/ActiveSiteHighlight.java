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

import openeye.oebio.*;
import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

/**************************************************************
* USED TO GENERATE CODE SNIPPETS FOR THE GRAPHEME DOCUMENTATION
**************************************************************/

public class ActiveSiteHighlight {

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

    private static void OEAddHighlighting_Predicate(OE2DActiveSiteDisplay adisp) {
        OEHighlightByBallAndStick highlight = new OEHighlightByBallAndStick(oechem.getOEBlueTint());

        oegrapheme.OEAddLigandHighlighting(adisp, highlight, new Pred6MemAromAtom());
        oegrapheme.OEAddLigandHighlighting(adisp, highlight, new Pred6MemAromBond());
    }

    private static void OEAddHighlighting_AtomAndBondPredicate(OE2DActiveSiteDisplay adisp) {
        OEHighlightByColor highlight = new OEHighlightByColor(oechem.getOEDarkGreen());
        oegrapheme.OEAddLigandHighlighting(adisp, highlight,
                                           new Pred6MemAromAtom(), new Pred6MemAromBond());
    }


    private static void OEAddHighlighting_OEMatch(OE2DActiveSiteDisplay adisp) {
        OEMolBase ligand = adisp.GetDisplayedLigand();
        OESubSearch subs = new OESubSearch("a1aaaaa1");
        OEColorIter citer = oechem.OEGetVividColors();
        boolean unique = true;
        for (OEMatchBase match : subs.Match(ligand, unique)) {
            if (citer.hasNext()) {
                OEHighlightByLasso highlight = new OEHighlightByLasso(citer.next());
                highlight.SetConsiderAtomLabelBoundingBox(true);
                oegrapheme.OEAddLigandHighlighting(adisp, highlight, match);
            }
        }
    }


    private static void OEAddHighlighting_OEAtomBondSet(OE2DActiveSiteDisplay adisp) {
        OEMolBase ligand = adisp.GetDisplayedLigand();
        OEAtomBondSet abset = new OEAtomBondSet(ligand.GetAtoms(new Pred6MemAromAtom()),
                                                ligand.GetBonds(new Pred6MemAromBond()));
        OEHighlightByCogwheel highlight = new OEHighlightByCogwheel(oechem.getOEPinkTint());
        highlight.SetInnerContour(false);
        oegrapheme.OEAddLigandHighlighting(adisp, highlight, abset);
    }

    private static void OEAddHighlighting_OEResidue(OE2DActiveSiteDisplay adisp) {
        OEColorIter citer = oechem.OEGetLightColors();
        for (OEResidue residue : adisp.GetDisplayedResidues()) {
            if (citer.hasNext()) {
                OEColor color = citer.next();
                OEPen pen = new OEPen(color, color, OEFill.On, 1.0);
                oegrapheme.OEAddResidueHighlighting(adisp, pen, residue);
            }
        }
    }


    public static OEGraphMol importMolecule (String filename) {

        oemolistream ifs = new oemolistream();
        OEGraphMol mol = new OEGraphMol();
        if (!ifs.open(filename)) {
            oechem.OEThrow.Fatal("Unable to open " + filename + " for reading");
        }

        if (!oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEThrow.Fatal("Unable to read molecule in " + filename);
        }
        ifs.close();
        return mol;
    }

    public static void main(String argv[]) {

        if (argv.length != 2)
            oechem.OEThrow.Usage("ActiveSiteHighlight <receptor> <ligand>");

        OEGraphMol receptor = importMolecule(argv[0]);
        OEGraphMol ligand = importMolecule(argv[1]);

        OEInteractionHintContainer asite =  new OEInteractionHintContainer(receptor, ligand);
        oebio.OEPerceiveInteractionHints(asite);
        oegrapheme.OEPrepareActiveSiteDepiction(asite);

        OE2DActiveSiteDisplayOptions opts = new OE2DActiveSiteDisplayOptions(600.0, 400.0);

        {
            OE2DActiveSiteDisplay adisp = new OE2DActiveSiteDisplay(asite, opts);
            OEAddHighlighting_Predicate(adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-Predicate.png", adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-Predicate.pdf", adisp);
        }
        {
            OE2DActiveSiteDisplay adisp = new OE2DActiveSiteDisplay(asite, opts);
            OEAddHighlighting_AtomAndBondPredicate(adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-AtomAndBondPredicate.png", adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-AtomAndBondPredicate.pdf", adisp);
        }
        {
            OE2DActiveSiteDisplay adisp = new OE2DActiveSiteDisplay(asite, opts);
            OEAddHighlighting_OEMatch(adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-OEMatch.png", adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-OEMatch.pdf", adisp);
        }
        {
            OE2DActiveSiteDisplay adisp = new OE2DActiveSiteDisplay(asite, opts);
            OEAddHighlighting_OEAtomBondSet(adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-OEAtomBondSet.png", adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-OEAtomBondSet.pdf", adisp);
        }

        {
            OE2DActiveSiteDisplay adisp = new OE2DActiveSiteDisplay(asite, opts);
            OEAddHighlighting_OEResidue(adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-OEResidue.png", adisp);
            oegrapheme.OERenderActiveSite("OEAddHighlighting-ActiveSite-OEResidue.pdf", adisp);
        }
    }
}
