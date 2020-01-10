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

import java.io.IOException;
import java.net.URL;

import openeye.oebio.*;
import openeye.oechem.*;

public class PlaceHydrogensPredDoc {

    private static class IsSameResidue extends OEUnaryAtomPred {
        public IsSameResidue(OEResidue res)
        {
            referenceResidue = new OEResidue(res);
        }
        public boolean constCall(OEAtomBase atom) {
            OEResidue res = oechem.OEAtomGetResidue(atom);
            return oechem.OESameResidue(res, referenceResidue);
        }
        public OEUnaryAtomBoolFunc CreateCopy() {
            OEUnaryAtomBoolFunc copy = new IsSameResidue(referenceResidue);
            copy.swigReleaseOwnership();
            return copy;
        }
        private OEResidue referenceResidue;
    }

    public static void main(String[] argv) {
        URL fileURL = PlaceHydrogensPredDoc.class.getResource("PlaceHydrogensPredDoc.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        if (!oechem.OEParseCommandLine(itf, argv, "PlaceHydrogensPredDoc"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        oemolistream ims = new oemolistream();
        ims.SetFlavor(OEFormat.PDB, OEIFlavor.PDB.ALTLOC);

        String inputFile = itf.GetString("-in");
        if (! ims.open(inputFile))
            oechem.OEThrow.Fatal("Unable to open " +
                                  inputFile + " for reading.");

        if (! oechem.OEIs3DFormat(ims.GetFormat()))
            oechem.OEThrow.Fatal(inputFile + " is not in a 3D format.");

        OEGraphMol mol = new OEGraphMol();
        if (! oechem.OEReadMolecule(ims, mol))
            oechem.OEThrow.Fatal("Unable to read " + inputFile + ".");

        if (! oechem.OEHasResidues(mol))
            oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

        OEAltLocationFactory alf = new OEAltLocationFactory(mol);
        if (alf.GetGroupCount() != 0)
            alf.MakePrimaryAltMol(mol);

        // in our example, we will select the first histidine
        OEResidue selectedResidue = new OEResidue();
        for (OEAtomBase atom : mol.GetAtoms()) {
          OEResidue res = oechem.OEAtomGetResidue(atom);
          if (oechem.OEGetResidueIndex(res) == OEResidueIndex.HIS) {
            selectedResidue = res;
            break;
          }
        }

        // given predicate IsSameResidue, residue selectedResidue and molecule mol...
        OEPlaceHydrogensOptions opt = new OEPlaceHydrogensOptions();
        opt.SetNoFlipPredicate(new IsSameResidue(selectedResidue));

        if (oebio.OEPlaceHydrogens(mol, opt)) {
            // selectedResidue will not be flipped...
        }

        ims.close();
    }
}
//@ </SNIPPET>
