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

import java.io.*;
import openeye.oechem.*;
import openeye.oebio.*;

public class ResAtomsDoc {

    static void LoopOverResAtoms(oemolistream ims) {
        OEGraphMol mol = new OEGraphMol();
        while(oechem.OEReadMolecule(ims, mol)) {
            if (! oechem.OEHasResidues(mol)) {
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);
            }
            OEHierView    hv   = new OEHierView(mol);
            OEHierResidue hres = hv.GetResidue('A', "LEU", 27);
            for(OEAtomBase atom : hres.GetAtoms()) {// only this residue's atoms
                OEResidue res = oechem.OEAtomGetResidue(atom);
                System.out.println(res.GetSerialNumber() + " " + atom.GetName());
            }
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("ResAtomsDoc <mol-infile>");
        }
        oemolistream ims = new oemolistream();
        if(! ims.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        LoopOverResAtoms(ims);
        ims.close();
    }
}
//@ </SNIPPET>
