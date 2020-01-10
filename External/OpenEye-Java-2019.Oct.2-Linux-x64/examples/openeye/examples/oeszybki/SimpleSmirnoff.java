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
package openeye.examples.oeszybki;

import openeye.oechem.*;
import openeye.oeszybki.*;

public class SimpleSmirnoff {

    public static void main(String[] args) {
        if (args.length!=2) {
            oechem.OEThrow.Usage("SimpleSmirnoff <molfile> <outfile>");
        }

        oemolistream ifs = new oemolistream();
        oemolostream ofs = new oemolostream();

        if (!ifs.open(args[0])) {
            oechem.OEThrow.Fatal("Unable to open " + args[0] + " for reading");
        }
        if (!ofs.open(args[1])) {
            oechem.OEThrow.Fatal("Unable to open " + args[1] + " for writing");
        }

        OEMol mol = new OEMol();
        oechem.OEReadMolecule(ifs, mol);

        OESzybkiOptions opts = new OESzybkiOptions();

        // Selection of SMIRNOFF force field
        opts.GetGeneralOptions().SetForceFieldType(OEForceFieldType.SMIRNOFF);

        OESzybki sz = new OESzybki(opts);
        OESzybkiResults res = new OESzybkiResults();

        if (!sz.call(mol, res)) {
            return;
        }

        oechem.OEWriteMolecule(ofs, mol);
        ofs.close();
        res.Print(oechem.getOeout());

        res.delete();
        sz.delete();
    }
}
