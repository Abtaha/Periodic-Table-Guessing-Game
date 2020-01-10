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

public class ProteinLigandPB { 
    public static void main(String[] args)
    {
        if (args.length != 3) {
            oechem.OEThrow.Usage("ProteinLigandPB ligand_file protein_file output_file (SDF or OEB)");
        }

        String ligandFile = args[0];
        String proteinFile = args[1];
        String outFile = args[2];

        oemolostream ofs = new oemolostream();
        if (!ofs.open(outFile)) {
            oechem.OEThrow.Fatal("Unable to open " + outFile + " for writing");
        }

        if (!oechem.OEIsSDDataFormat(ofs.GetFormat())) {
            oechem.OEThrow.Fatal("Output file does not support SD data used by this example");
        }

        // Szybki options for VdW-Coulomb calculations
        OESzybkiOptions optsC = new OESzybkiOptions();
        optsC.GetProteinOptions().SetProteinElectrostaticModel(OEProteinElectrostatics.ExactCoulomb);
        optsC.SetRunType(OERunType.CartesiansOpt);

        // Szybki options for PB calculations
        OESzybkiOptions optsPB = new OESzybkiOptions();
        optsPB.GetProteinOptions().SetProteinElectrostaticModel(OEProteinElectrostatics.SolventPBForces);
        optsPB.SetRunType(OERunType.SinglePoint);

        // Szybki objects
        OESzybki szC = new OESzybki(optsC);
        OESzybki szPB = new OESzybki(optsPB);

        // read and setup protein
        oemolistream pfs = new oemolistream();
        if (!pfs.open(proteinFile)) {
            oechem.OEThrow.Fatal("Unable to open " + proteinFile + " for reading");
        }
        OEGraphMol protein = new OEGraphMol();
        oechem.OEReadMolecule(pfs, protein);
        pfs.close();
        szC.SetProtein(protein);
        szPB.SetProtein(protein);

        // process molecules
        oemolistream lfs = new oemolistream();
        if (!lfs.open(ligandFile)) { 
            oechem.OEThrow.Fatal("Unable to open " + ligandFile + " for reading");
        }

        OEMol mol = new OEMol();
        while(oechem.OEReadMolecule(lfs, mol)) {
            OESzybkiResultsIter resultsiter = szC.call(mol); // optimize mol

            if (!resultsiter.IsValid()) {
                oechem.OEThrow.Warning("No results processing molecule: " + mol.GetTitle());
                continue;
            }

            // do single point with better electrostatics and output results
            resultsiter = szPB.call(mol);
            OEConfBaseIter confiter = mol.GetConfs();
            while(resultsiter.hasNext()) {
                OEConfBase conf = confiter.next();
                OESzybkiResults results =  resultsiter.next();
                for (int i = 0; i < OEPotentialTerms.Max; ++i) {
                    if (i == OEPotentialTerms.ProteinLigandInteraction || i == OEPotentialTerms.VdWProteinLigand || 
                        i == OEPotentialTerms.CoulombProteinLigand     || i == OEPotentialTerms.ProteinDesolvation ||
                        i == OEPotentialTerms.LigandDesolvation        || i == OEPotentialTerms.SolventScreening)
                    {
                        String energy = String.format("%9.4f", results.GetEnergyTerm(i));
                        oechem.OEAddSDData(conf, oeszybkilib.OEGetEnergyTermName(i), energy);
                    }
                }
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
        ofs.close();
        lfs.close();
    }
}
