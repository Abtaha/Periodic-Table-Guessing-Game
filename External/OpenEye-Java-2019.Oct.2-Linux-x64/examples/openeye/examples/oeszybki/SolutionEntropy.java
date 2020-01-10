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

import java.text.DecimalFormat;

import openeye.oechem.*;
import openeye.oequacpac.*;
import openeye.oeszybki.*;

public class SolutionEntropy
{
    public static void main(String argv[])
    {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("SolutionEntropy input_molecule output_molecule");
        }

        String inputFile = argv[0];
        String outputFile = argv[1];

        oemolistream ifs = new oemolistream();
        if (!ifs.open(inputFile)) {
            oechem.OEThrow.Fatal("Unable to open " + inputFile + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(outputFile)) {
            oechem.OEThrow.Fatal("Unable to open " + outputFile + " for writing");
        }

        // input set of conformations : solution;
        OEMol mol = new OEMol();
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        OESzybkiOptions opts = new OESzybkiOptions();
        opts.GetSolventOptions().SetChargeEngine(new OEChargeEngineNoOp());

        OESzybki sz = new OESzybki(opts);
        OESzybkiEnsembleResults eres = new OESzybkiEnsembleResults();
        double entropy = sz.GetEntropy(mol, eres, OEEntropyMethod.Analytic);

        
        DecimalFormat df = new DecimalFormat("####.#");
        oechem.OEThrow.Info("Estimated molar solution entropy of the input compound is: " + df.format(entropy) + " J/(mol*K)");
        oechem.OEThrow.Info("Vibrational entropies (in J/(mol*K)) for all conformations:");

        OEConfBaseIter conf = mol.GetConfs();
         for (OESzybkiResults res : eres.GetResultsForConformations()) {
             if (!conf.IsValid()) {
                continue;
             }

            System.out.printf("%2d %5.1f\n", conf.Target().GetIdx(), res.GetVibEntropy());
            conf.Increment();
        }

        // ensemble of unique conformations;
        oechem.OEWriteMolecule(ofs, mol);
        ofs.close();
    }
}
