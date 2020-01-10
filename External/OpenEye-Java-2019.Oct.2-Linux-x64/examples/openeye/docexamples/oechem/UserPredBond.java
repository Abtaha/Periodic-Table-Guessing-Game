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

public class UserPredBond {

    private static class PredHasDoubleBondO extends OEUnaryAtomPred {
        public boolean constCall(OEAtomBase atom) {
            for (OEBondBase bond : atom.GetBonds()) {
                if (bond.GetOrder() == 2 && bond.GetNbr(atom).IsOxygen())
                    return true;
            }
            return false;
        }
        public  OEUnaryAtomBoolFunc CreateCopy() {
            OEUnaryAtomBoolFunc copy = new PredHasDoubleBondO();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    private static class PredAmideBond extends OEUnaryBondPred {
        public boolean constCall(OEBondBase bond) {      
            if (bond.GetOrder() != 1)
                return false;      
            OEAtomBase atomB = bond.GetBgn();
            OEAtomBase atomE = bond.GetEnd();    
            OEUnaryAtomBoolFunc pred =  new PredHasDoubleBondO();
            if (atomB.IsCarbon() && atomE.IsNitrogen() && pred. constCall(atomB))
                return true;
            if (atomB.IsNitrogen() && atomE.IsCarbon() && pred. constCall(atomE))
                return true;
            return false;
        }
        public  OEUnaryBondBoolFunc CreateCopy() {
            OEUnaryBondBoolFunc copy = new PredAmideBond();
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();    
        oechem.OESmilesToMol(mol,"CC(=O)Nc1c[nH]cc1");
        System.out.println("Number of amide bonds = " + 
                oechem.OECount(mol,new PredAmideBond()));
    }
}
//@ </SNIPPET>
