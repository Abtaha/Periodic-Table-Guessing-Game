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

import java.util.*;
import openeye.oechem.*;

public class AtomSubsetEvil {
    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol(); // initialized somehow

        ArrayList<Integer> acache = new ArrayList<Integer>(); // cache of atoms
        for (OEAtomBase atom : mol.GetAtoms(new OEHasAlphaBetaUnsat()))
            acache.add(atom.GetIdx()); // evil!

        // pretend this code is deep in some inner loop that needs to go fast
        for (int aidx : acache) {
            OEAtomBase catom = mol.GetAtom(new OEHasAtomIdx(aidx)); // O(n) lookup!

            // do something with the cached atom "catom"
            catom.SetName("Hello World");
        }
    }
}
//@ </SNIPPET>