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
package openeye.docexamples.oegraphsim;

import java.util.ArrayList;
import openeye.oechem.*;
import openeye.oegraphsim.*;

public class SimFunc {
    static class SimpsonSimFunc extends OESimFuncBase {

        @Override
        public float constCall(OEFingerPrint fpA, OEFingerPrint fpB) {

            OEUIntArray onlyA  = new OEUIntArray(1);
            OEUIntArray onlyB  = new OEUIntArray(1);
            OEUIntArray bothAB = new OEUIntArray(1);
            oechem.OEGetBitCounts(fpA, fpB, onlyA, onlyB, bothAB);

            float sim = (float)bothAB.getItem(0);
            sim /= (float)Math.min((onlyA.getItem(0) + bothAB.getItem(0)),
                    (onlyB.getItem(0) + bothAB.getItem(0)));
            return sim;
        }
        @Override
        public OESimFuncBase CreateCopy() {
            OESimFuncBase copy = new SimpsonSimFunc();
            copy.swigReleaseOwnership();
            return copy;
        }
        @Override
        public String GetSimTypeString() {
            return "Simpson";
        }
    }

    public static void main(String argv[]) {

        ArrayList<String> smiles = new ArrayList<String>();
        smiles.add("c1ccc2c(c1)c(c(oc2=O)OCCSC(=N)N)Cl");
        smiles.add("COc1cc2ccc(cc2c(=O)o1)NC(=N)N");
        OEFPDatabase fpdb = new OEFPDatabase(OEFPType.Path);
        fpdb.SetSimFunc(new SimpsonSimFunc());
        OEGraphMol mol = new OEGraphMol();
        for (int i=0; i < smiles.size(); i++) {
            mol.Clear();
            oechem.OESmilesToMol(mol, smiles.get(i));
            fpdb.AddFP(mol);
        }

        OEGraphMol query =  new OEGraphMol();
        oechem.OESmilesToMol(query, "COc1c(c2ccc(cc2c(=O)o1)NC(=N)N)Cl");

        for (OESimScore score : fpdb.GetScores(query)) {
            System.out.format("%.3f\n", score.GetScore());
        }
    }
}
