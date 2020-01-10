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
package openeye.docexamples.oebio;

import java.io.*;
import openeye.oechem.*;
import openeye.oebio.*;

public class AtomMatchResidue {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("AtomMatchResidue <pdb>");
        }
        oemolistream ifs = new oemolistream();
        if(!ifs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        OEGraphMol mol = new OEGraphMol();
        oechem.OEReadMolecule(ifs, mol);

        OEAtomMatchResidueID resAla = new OEAtomMatchResidueID();
        resAla.SetName("ALA");
        OEAtomMatchResidue predAla = new OEAtomMatchResidue(resAla);
        System.out.print("Number of atoms matching residue name ALA " + oechem.OECount(mol, predAla));

        OEAtomMatchResidueID resChainA = new OEAtomMatchResidueID();
        resChainA.SetChainID("A");
        OEAtomMatchResidue predChainA = new OEAtomMatchResidue(resChainA);
        System.out.print("Number of atoms matching chain A = " + oechem.OECount(mol, predChainA));

        OEAtomMatchResidueID resHis = new OEAtomMatchResidueID();
        resHis.SetName("HIS");
        resHis.SetChainID("A");
        resHis.SetResidueNumber("88");
        OEAtomMatchResidue predHis = new OEAtomMatchResidue(resHis);
        System.out.print("Number of atoms matching residue (HIS A 88) = " + oechem.OECount(mol, predHis));

        // alternative way to initialize as regex
        OEAtomMatchResidue predHis2 = new OEAtomMatchResidue("HIS:88:.*:A:.*");
        System.out.print("Number of atoms matching residue (HIS A 88) = " + oechem.OECount(mol, predHis2));

        System.out.print("Backbone atoms of residue (HIS A 88): ");

        for(OEAtomBase atom : mol.GetAtoms(new OEAndAtom(predHis, new OEIsBackboneAtom())))
        {
            OEResidue res = oechem.OEAtomGetResidue(atom);
            System.out.print(atom.GetName() + " ");
            System.out.println(res.GetName() + " " + res.GetChainID() + " " + res.GetResidueNumber());
        }
    }
}
