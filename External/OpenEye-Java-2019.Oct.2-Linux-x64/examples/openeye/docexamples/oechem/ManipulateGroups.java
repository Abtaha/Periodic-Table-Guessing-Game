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
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class ManipulateGroups {

    private static class IsAtomGroup extends OEUnaryGroupPred {
        public boolean constCall(OEGroupBase group) {
            return group.NumAtoms() > 0;
        }
        public OEUnaryGroupBoolFunc CreateCopy() {
            OEUnaryGroupBoolFunc copy = new IsAtomGroup();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    private static class IsBondGroup extends OEUnaryGroupPred {
        public boolean constCall(OEGroupBase group) {
            return group.NumBonds() > 0;
        }
        public OEUnaryGroupBoolFunc CreateCopy() {
            OEUnaryGroupBoolFunc copy = new IsBondGroup();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    private static void dumpGroup(OEGroupBase group) {
        System.out.print("type \"" + oechem.OEGetTag(group.GetGroupType()) + "\"");
        if (group.NumAtoms() > 0) {
            System.out.print(" atom indices: ");
            for (OEAtomBase atom : group.GetAtoms()) {
                System.out.print(atom.GetIdx() + " ");
            }
            System.out.println();
        }
        if (group.NumBonds() > 0) {
            System.out.print(" bond indices: ");
            for (OEBondBase bond : group.GetBonds()) {
                System.out.print(bond.GetIdx() + " ");
            }
            System.out.println();
        }
    }

    private static void dumpGroups(OEMolBase mol) {
        System.out.println("groups of " + mol.GetTitle());
        System.out.println("number of atom groups " + oechem.OECount(mol, new IsAtomGroup()));
        System.out.println("number of bond groups " + oechem.OECount(mol, new IsBondGroup()));
        System.out.println("number of aromatic atoms groups " +  oechem.OECount(mol, new OEHasGroupType(oechem.OEGetTag("aromatic atoms"))));
        System.out.println("number of aromatic bonds groups " +  oechem.OECount(mol, new OEHasGroupType(oechem.OEGetTag("aromatic bonds"))));

        // loop over groups
        for (OEGroupBase g : mol.GetGroups())
            dumpGroup(g);
        System.out.println();
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "CN1c2ccc(cc2C(=NCC1=O)c3ccccc3)Cl");
        mol.SetTitle("valium");

        // generate some groups
        OEAtomBaseVector atoms = new OEAtomBaseVector();
        for (OEAtomBase atom : mol.GetAtoms(new OEIsAromaticAtom())) {
            atoms.add(atom);
        }
        OEGroupBase atomgroup = mol.NewGroup(oechem.OEGetTag("aromatic atoms"), atoms);

        OEBondBaseVector bonds = new OEBondBaseVector();
        for (OEBondBase bond : mol.GetBonds(new OEIsAromaticBond())) {
            bonds.add(bond);
        }
        OEGroupBase bondgroup = mol.NewGroup(oechem.OEGetTag("aromatic bonds"), bonds);

        dumpGroups(mol);
        // delete a group
        mol.DeleteGroup(bondgroup);
        dumpGroups(mol);
    }
}
//@ </SNIPPET>
