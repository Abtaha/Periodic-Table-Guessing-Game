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

public class CalcAveMolecularWeight {
    static double CalculateMolecularWeight(OEMolBase mol) {
        int elemno = 0;
        int mass = 0;
        int implicitH = 0;
        double weight = 0.0;

        for (OEAtomBase atom : mol.GetAtoms()) {
            elemno = atom.GetAtomicNum();
            mass = atom.GetIsotope();
            implicitH += atom.GetImplicitHCount();
            if ((elemno != 0) && (mass != 0) && oechem.OEIsCommonIsotope(elemno, mass)) {
                weight += oechem.OEGetIsotopicWeight(elemno, mass);
            } else {
                weight += oechem.OEGetAverageWeight(elemno);
            }
        }
        weight += implicitH * oechem.OEGetAverageWeight(OEElemNo.H);
        return weight;
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "C(O)(=O)c1cccc2c1[nH]c(C3CCCc4c3cccc4)c2");
        System.out.println("molecular weight= " + CalculateMolecularWeight(mol));
    }
}
//@ </SNIPPET>
