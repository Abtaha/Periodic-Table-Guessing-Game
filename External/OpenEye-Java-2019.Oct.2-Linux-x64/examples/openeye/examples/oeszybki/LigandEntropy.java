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

public class LigandEntropy {
    public static void main(String[] args) {
        if (args.length != 1) { 
            oechem.OEThrow.Usage("LigandEntropy <lig>");
        }

        oemolistream ifs = new oemolistream();
        if (!ifs.open(args[0])) { 
            oechem.OEThrow.Fatal("Unable to open " + args[0] + " for reading");
        }

        OEMol lig = new OEMol();
        oechem.OEReadMolecule(ifs, lig);
        ifs.close();

        OESzybkiOptions opts = new OESzybkiOptions();
        opts.GetSolventOptions().SetSolventModel(OESolventModel.Sheffield);
        OESzybki sz = new OESzybki(opts);
        OESzybkiEnsembleResults res = new OESzybkiEnsembleResults();
        double solutionEntropy = sz.GetEntropy(lig, res, OEEntropyMethod.Analytic);

        System.out.printf("Configurational entropy %10.2f\n", res.GetConfigurationalEntropy());
        System.out.printf("Solvation entropy       %10.2f\n", res.GetEnsembleLigSolvEntropy());
        System.out.printf("                            ======\n");
        System.out.printf("Total solution entropy  %10.2f J/(mol K)\n", solutionEntropy);
    }
}
