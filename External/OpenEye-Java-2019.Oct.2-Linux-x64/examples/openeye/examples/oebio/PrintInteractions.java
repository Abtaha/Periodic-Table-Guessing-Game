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
/****************************************************************************
 * Print protein-ligand interactions
 ****************************************************************************/

package openeye.examples.oebio;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.net.URL;

import openeye.oechem.*;
import openeye.oebio.*;

public class PrintInteractions {

    private static OEInteractionHintComponentTypeBase ligtype = new OELigandInteractionHintComponent();
    private static OEInteractionHintComponentTypeBase protype = new OEProteinInteractionHintComponent();

    public static void main(String argv[]) {
        URL fileURL = PrintInteractions.class.getResource("PrintInteractions.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        oebio.OEConfigureSplitMolComplexOptions(itf, OESplitMolComplexSetup.LigName);

        if (!oechem.OEParseCommandLine(itf, argv, "PrintInteractions"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        String iname = itf.GetString("-complex");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(iname))
            oechem.OEThrow.Fatal("Cannot open input file!");

        OEGraphMol complexmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ifs, complexmol))
            oechem.OEThrow.Fatal("Unable to read molecule from " + iname);
        ifs.close();

        if (!oechem.OEHasResidues(complexmol))
            oechem.OEPerceiveResidues(complexmol, OEPreserveResInfo.All);

        // Separate ligand and protein

        OESplitMolComplexOptions sopts = new OESplitMolComplexOptions();
        oebio.OESetupSplitMolComplexOptions(sopts, itf);

        OEGraphMol lig   = new OEGraphMol();
        OEGraphMol prot  = new OEGraphMol();
        OEGraphMol water = new OEGraphMol();
        OEGraphMol other = new OEGraphMol();

        OEUnaryRoleSetPred pfilter = sopts.GetProteinFilter();
        OEUnaryRoleSetPred wfilter = sopts.GetWaterFilter();
        sopts.SetProteinFilter(new OEOrRoleSet(pfilter, wfilter));
        sopts.SetWaterFilter(oebio.OEMolComplexFilterFactory(OEMolComplexFilterCategory.Nothing));

        oebio.OESplitMolComplex(lig, prot, water, other, complexmol, sopts);

        if (lig.NumAtoms() == 0)
            oechem.OEThrow.Fatal("Cannot separate complex!");

        // Perceive interactions

        OEInteractionHintContainer asite = new OEInteractionHintContainer(prot, lig);
        if (!oebio.OEIsValidActiveSite(asite))
            oechem.OEThrow.Fatal("Cannot initialize active site!");

        oebio.OEPerceiveInteractionHints(asite);

        System.out.println("Number of interactions:" + asite.NumInteractions());

        for (OEInteractionHintTypeBase itype : oebio.OEGetActiveSiteInteractionHintTypes()) {
            System.out.println(itype.GetName() + " :");
            for (OEInteractionHint inter : asite.GetInteractions(new OEHasInteractionHintType(itype))) {
                printInteraction(inter);
            }
        }
        System.out.println("\nResidue interactions:");
        OEMolBase protein = asite.GetMolecule(new OEProteinInteractionHintComponent());
        for (OEResidue residue : oebio.OEGetResidues(protein)) {
            printResidueInteraction(asite, residue);
        }
        System.out.println("\nLigand atom interactions:");
        OEMolBase ligand = asite.GetMolecule(new OELigandInteractionHintComponent());
        for (OEAtomBase atom : ligand.GetAtoms()) {
            printLigandAtomInteractions(asite, atom);
        }
    }

    public static String getResidueName(OEResidue residue) {
        String name = String.format("%3s %4d %s", residue.GetName(),
                                                  residue.GetResidueNumber(),
                                                  residue.GetChainID());
        return name;
    }

    public static String getAtomName(OEAtomBase atom) {
        String name = String.format("%2d %s", atom.GetIdx(),
                                              oechem.OEGetAtomicSymbol(atom.GetAtomicNum()));
        return name;
    }

    public static void printFragment(OEInteractionHintFragment frag) {
        OEInteractionHintComponentTypeBase fragtype = frag.GetComponentType();
        if (fragtype.equals(ligtype)) {
            System.out.print("ligand: ");
        }
        else if (fragtype.equals(protype)) {
            System.out.print("protein: ");
        }
        for (OEAtomBase atom : frag.GetAtoms()) {
            System.out.print(getAtomName(atom) + " ");
        }
    }

    public static void printInteraction(OEInteractionHint inter) {
        OEInteractionHintFragment bgnfrag = inter.GetBgnFragment();
        if (bgnfrag != null) {
            printFragment(bgnfrag);
        }
        OEInteractionHintFragment endfrag = inter.GetEndFragment();
        if (endfrag != null) {
            printFragment(endfrag);
        }
        System.out.println("");
    }

    public static void printResidueInteraction(OEInteractionHintContainer asite, OEResidue residue)
    {
        Set<OEAtomBase> ligatoms = new HashSet<OEAtomBase>();
        OEHasResidueInteractionHint ipred = new OEHasResidueInteractionHint(residue);
        for (OEInteractionHint inter : asite.GetInteractions(ipred)) {
            OEInteractionHintFragment ligfrag = inter.GetFragment(new OELigandInteractionHintComponent());
            if (ligfrag != null) {
                for (OEAtomBase atom : ligfrag.GetAtoms()) {
                    ligatoms.add(atom);
                }
            }
        }
        if (!ligatoms.isEmpty()) {
            System.out.print(getResidueName(residue) + " :");
            for (OEAtomBase atom : ligatoms) {
                System.out.print(getAtomName(atom) + " ");
            }
            System.out.println("");
        }
    }

    public static void printLigandAtomInteractions(OEInteractionHintContainer asite, OEAtomBase atom) {
          Set<String> resnames = new HashSet<String>();
          OEHasInteractionHint ipred = new OEHasInteractionHint(atom);
          for (OEInteractionHint inter : asite.GetInteractions(ipred)) {
                OEInteractionHintFragment profrag = inter.GetFragment(new OEProteinInteractionHintComponent());
                if (profrag != null) {
                    for (OEAtomBase patom : profrag.GetAtoms()) {
                        OEResidue res = oechem.OEAtomGetResidue(patom);
                        resnames.add(getResidueName(res));
                    }
                }
          }
          if (!resnames.isEmpty()) {
            System.out.print(getAtomName(atom) + " :");
            for (String resname : resnames) {
                System.out.print(resname + " ");
            }
            System.out.println("");
          }
    }
}
