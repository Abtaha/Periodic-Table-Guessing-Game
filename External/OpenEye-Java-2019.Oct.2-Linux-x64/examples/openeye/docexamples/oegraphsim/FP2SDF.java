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

import openeye.oechem.*;
import openeye.oegraphsim.*;

public class FP2SDF {

    public static void main(String argv[]) {

        if (argv.length != 2)
            oechem.OEThrow.Usage("FP2SDF <infile> <outfile>");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");

        oemolostream ofs =  new oemolostream();
        if (!ofs.open(argv[1]))
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for writing");
        if (ofs.GetFormat() != OEFormat.SDF)
            oechem.OEThrow.Fatal(argv[1] + " output file has to be an SDF file");

        OEFingerPrint fp = new OEFingerPrint();
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oegraphsim.OEMakeFP(fp, mol, OEFPType.Circular);
            String fptypestr = fp.GetFPTypeBase().GetFPTypeString();
            String fphexdata = fp.ToHexString();
            oechem.OESetSDData(mol, fptypestr, fphexdata);
            oechem.OEWriteMolecule(ofs, mol);
        }
        ifs.close();
        ofs.close();
    }
}
