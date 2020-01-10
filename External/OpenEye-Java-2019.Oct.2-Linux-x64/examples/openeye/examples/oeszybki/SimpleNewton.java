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

public class SimpleNewton
{
    public static void main(String[] args) {
        if (args.length != 2)
            oechem.OEThrow.Usage("SimpleNewton input_molecule output_molecule");

        String inputFile  = args[0];
        String outputFile = args[1];

        oemolistream ifs = new oemolistream();
        if (!ifs.open(inputFile))
            oechem.OEThrow.Fatal("Unable to open " + inputFile + " for reading");

        oemolostream ofs = new oemolostream();
        if (!ofs.open(outputFile))
            oechem.OEThrow.Fatal("Unable to open " + outputFile + " for writing");

        OEMol mol = new OEMol();
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        OESzybkiOptions opts = new OESzybkiOptions();
        opts.GetOptOptions().SetOptimizerType(OEOptType.NEWTON);
        opts.GetSolventOptions().SetSolventModel(OESolventModel.Sheffield);

        OESzybki sz = new OESzybki(opts);
        OESzybkiResults res = new OESzybkiResults();
        if (sz.call(mol, res)) {
            oechem.OEWriteMolecule(ofs, mol);
            res.Print(oechem.oeout);
        }
        ofs.close();
    }
}

