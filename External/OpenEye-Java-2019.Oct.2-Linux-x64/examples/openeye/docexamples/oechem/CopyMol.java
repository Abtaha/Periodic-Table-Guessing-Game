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

public class CopyMol {
    public static void main(String argv[]) {
        OEGraphMol src = new OEGraphMol(); 

        oechem.OESmilesToMol(src, "c1ccccc1");

        // make holes in the molecule index space and juggle things around
        oechem.OEAddExplicitHydrogens(src);
        oechem.OESuppressHydrogens(src);
        oechem.OEAddExplicitHydrogens(src);
        oechem.OECanonicalOrderAtoms(src);

        OEAtomBasePtrArray atommap = new OEAtomBasePtrArray(src.GetMaxAtomIdx());
        
        OEGraphMol dst = new OEGraphMol();
        oechem.OECopyMol(dst, src, atommap);

        for (OEAtomBase srcatom : src.GetAtoms()) {
            OEAtomBase dstatom = atommap.getItem(srcatom.GetIdx());
            System.out.println(srcatom.GetIdx() + " -> " + dstatom.GetIdx());
        }
    }
}
//@ </SNIPPET>
