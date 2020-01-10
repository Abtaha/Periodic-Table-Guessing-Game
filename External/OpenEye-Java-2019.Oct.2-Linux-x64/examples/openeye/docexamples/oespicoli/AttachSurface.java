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
package openeye.docexamples.oespicoli;

import openeye.oechem.*;
import openeye.oespicoli.*;

public class AttachSurface {
    public static void main(String argv[]) {
        if (argv.length != 1)
            oechem.OEThrow.Usage("AttachSurface <input>");

        oemolistream ims = new oemolistream();
        if (!ims.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0]);

        OEGraphMol mol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ims, mol))
            oechem.OEThrow.Fatal("Unable to read a molecule");
        ims.close();
        oechem.OEAssignBondiVdWRadii(mol);

        OESurface surf = new OESurface();
        oespicoli.OEMakeMolecularSurface(surf, mol);
        oespicoli.OESetSurface(mol, "surface", surf);

        oemolostream ofs = new oemolostream("foo.oeb");
        oechem.OEWriteMolecule(ofs, mol);
        ofs.close();

        oemolistream ifs = new oemolistream("foo.oeb");
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        OESurface msrf = oespicoli.OEGetSurface(mol, "surface");
    }
}
