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
import openeye.oeszybki.*;

public class FreeFormConfAdvRstr
{
    public static void main(String argv[])
    {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("FreeFormConfAdvRstr <input> <output>");
        }

        String inputFile  = argv[0];
        String outputFile = argv[1];

        oemolistream ifs = new oemolistream();
        if (!ifs.open(inputFile)) {
            oechem.OEThrow.Fatal("Unable to open " + inputFile + " for reading");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(outputFile)) {
            oechem.OEThrow.Fatal("Unable to open " + outputFile + " for writing");
        }

        OEMol mol = new OEMol();
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        OEFreeFormConfOptions opts = new OEFreeFormConfOptions();
        OEFreeFormConfAdvanced ffconf = new OEFreeFormConfAdvanced(opts);

        // Make a copy of our MCMol.  We will execute the FreeFormConf commands on
        // the copied molecule so that our original molecule stays intact.
        OEMol omol = new OEMol(mol);

        // Make further copies of our original molecule.  The copied molecule(s) would be used
        // as source on which retriction energies would be calculated
        OEMol rmol = new OEMol(mol);
        OEMol fmol = new OEMol(mol);

        // Prepare a comprehensive ensemble of molecule conformers.  For calculation
        // of restriction energies we want to make sure that all the corresponding free
        // conformers are also part of the comprehensive ensemble.  This will also
        // assign solvent charges on the molecule and check that the ensemble is
        // otherwise ready for FreeFormConf calculations. The resulting `fmol`
        // contains the correspondig free conformers.
        if(ffconf.PrepareEnsemble(omol, rmol, fmol)!=OEFreeFormReturnCode.Success) {
            oechem.OEThrow.Error("Failed to prepare ensemble for FreeFormConf calculations");
        }

        // Perform loose optimization of the ensemble conformers.  We will remove
        // duplicates based on the loose optimization, to reduce the time needed for
        // tighter, more stricter optimization
        if(ffconf.PreOptimizeEnsemble(omol)!=OEFreeFormReturnCode.Success){
            oechem.OEThrow.Error("Pre-optimization of the ensembles failed");
        }

        // Remove duplicates from the pre-optimized ensemble
        if(ffconf.RemoveDuplicates(omol)!=OEFreeFormReturnCode.Success){
            oechem.OEThrow.Error("Duplicate removal from the ensembles failed");
        }

        // Perform the desired optimization.  This uses a stricter convergence
        // criteria in the default settings.
        if(ffconf.Optimize(omol)!=OEFreeFormReturnCode.Success){
            oechem.OEThrow.Error("Optimization of the ensembles failed");
        }

        // Remove duplicates to obtain the set of minimum energy conformers
        if(ffconf.RemoveDuplicates(omol)!=OEFreeFormReturnCode.Success){
            oechem.OEThrow.Error("Duplicate removal from the ensembles failed");
        }

        // Perform FreeFormConf free energy calculations.  When all the above steps
        // have already been performed on the ensemble, this energy calculation
        // step is fast.
        if(ffconf.EstimateEnergies(omol)!=OEFreeFormReturnCode.Success){
            oechem.OEThrow.Error("Estimation of FreeFormConf energies failed");
        }

        // Gather results of calculation into a results object for ease of viewing, etc.
        OEFreeFormConfResults res = new OEFreeFormConfResults(omol);
        oechem.OEThrow.Info("Number of unique conformations: " + res.GetNumUniqueConfs());
        oechem.OEThrow.Info("Conf.  Delta_G   Vibrational_Entropy");
        oechem.OEThrow.Info("      [kcal/mol]     [J/(mol K)]");
        for (OESingleConfResult r : res.GetResultsForConformations()) {
            System.err.printf("%2d %10.2f %14.2f\n", r.GetConfIdx(), r.GetDeltaG(), r.GetVibrationalEntropy());
        }

        // Identify the corresponding conformer(s) to the free minimized conformer(s).
        // If identified, the corresponding (Conf)Free energy information is also
        // copied to the free conformers
        if(ffconf.IdentifyConformer(fmol, omol)!=OEFreeFormReturnCode.Success) {
            oechem.OEThrow.Error("Identification of free conformer(s) failed");
        }

        // Estimate restriction energies. Since both restricted and free conformer
        // energy components are already available, this operation is fast.
        if(ffconf.EstimateRestrictionEnergy(fmol, rmol)!=OEFreeFormReturnCode.Success) {
            oechem.OEThrow.Error("Restriction energy estimation failed");
        }

        // Gather restriction energies into a results object for ease of viewing, etc.
        OERestrictionEnergyResult rstrRes = new OERestrictionEnergyResult(fmol);
        oechem.OEThrow.Info("Global strain: " + rstrRes.GetGlobalStrain());
        oechem.OEThrow.Info("Local strain: " + rstrRes.GetLocalStrain());

        // Optionally it is desired to perform a restrained optimization of the
        // restricted conformer(s) to brush out any energy differences due to
        // force field constaints or the sources of coonformer coordinates.  Note: The
        // high level EstimateFreeEnergy method does not perform this opertion.
        if(ffconf.OptimizeRestraint(rmol)!=OEFreeFormReturnCode.Success){
            oechem.OEThrow.Error("Restraint optimization of the conformer(s) failed");
        }

        // Estimate restriction energies. Since both restricted and free conformer
        // energy components are already available, this operation is fast.
        if(ffconf.EstimateRestrictionEnergy(fmol, rmol)!=OEFreeFormReturnCode.Success) {
            oechem.OEThrow.Error("Restriction energy estimation failed");
        }

        // Gather restriction energies into a results object for ease of viewing, etc.
        OERestrictionEnergyResult rstrRes2 = new OERestrictionEnergyResult(fmol);
        oechem.OEThrow.Info("Global strain: " + rstrRes2.GetGlobalStrain());
        oechem.OEThrow.Info("Local strain: " + rstrRes2.GetLocalStrain());

        oechem.OEWriteMolecule(ofs, omol);
        ofs.close();
    }
}
