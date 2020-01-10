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
package openeye.docexamples.oemedchem;

import openeye.oechem.*;
import openeye.oemedchem.*;

public class OEGetBemisMurcko {
    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "CCOc1ccc(cc1)CC(OC)c2ccccc2CC(=O)N");
        for (OEAtomBondSet abset : oemedchem.OEGetBemisMurcko(mol)) {
            OEIsAtomMember fragatompred = new OEIsAtomMember(abset.GetAtoms());
            OEIsBondMember fragbondpred = new OEIsBondMember(abset.GetBonds());

            OEGraphMol fragment = new OEGraphMol();
            boolean  adjustHCount = true;
            oechem.OESubsetMol(fragment, mol, fragatompred, fragbondpred, adjustHCount);
            for (OERole role : abset.GetRoles()) {
                System.out.printf("%s %s%n", role.GetName(), oechem.OEMolToSmiles(fragment));
            }
        }
    }
}
//@ </SNIPPET>
