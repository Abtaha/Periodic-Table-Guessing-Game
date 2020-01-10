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
// Prepare a molecule: alts, hydrogens, split ligand

package openeye.examples.oebio;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;

import openeye.oebio.*;
import openeye.oechem.*;

public class ProteinPrep {

    static int WaterProcess(String processName) {
        if (processName.equals("fullsearch"))
            return OEPlaceHydrogensWaterProcessing.FullSearch;
        else if (processName.equals("focused"))
            return OEPlaceHydrogensWaterProcessing.Focused;
        return OEPlaceHydrogensWaterProcessing.Ignore;
    }

    public static void main(String[] argv) {
        URL fileURL = ProteinPrep.class.getResource("ProteinPrep.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        oebio.OEConfigureSplitMolComplexOptions(itf,
                                       OESplitMolComplexSetup.All              &
                                   ~ ( OESplitMolComplexSetup.CovBondTreatment |
                                       OESplitMolComplexSetup.CovCofactor ) );

        if (! oechem.OEParseCommandLine(itf, argv, "ProteinPrep"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        boolean verbose = itf.GetBool("-verbose");
        if (verbose)
            oechem.OEThrow.SetLevel(OEErrorLevel.Verbose);

        String  altProcess  = itf.GetString("-alts");
        boolean keepAlts    = ! altProcess.equals("a");
        boolean highestOcc  =  (altProcess.equals("occupancy"));
        boolean compareAlts =  (altProcess.equals("compare"));

        int siteNum        = itf.GetUnsignedInt("-bindingsitenum");
        boolean allSites   = (siteNum == 0);
        boolean otherModel = (itf.GetUnsignedInt("-modelnum") != 1);

        boolean placeHyd = itf.GetBool("-placehydrogens");
        boolean splitlig = itf.HasString("-ligout");

        String watProcessName = itf.GetString("-waterprocessing");
        int waterProcess = WaterProcess(watProcessName);

        boolean standardize = itf.GetBool("-standardizehyd");
        double badclash     = itf.GetDouble("-clashcutoff");
        double flipbias     = itf.GetDouble("-flipbias");
        double maxStates    = itf.GetDouble("-maxsubstates");

        int flavor = OEIFlavor.PDB.Default | OEIFlavor.PDB.DATA;
        if (keepAlts)
          flavor |= OEIFlavor.PDB.ALTLOC;
        if (otherModel)
          flavor &= ~ OEIFlavor.PDB.ENDM;

        oemolistream ims = new oemolistream();
        ims.SetFlavor(OEFormat.PDB, flavor);

        String inputFile = itf.GetString("-in");
        if (! ims.open(inputFile))
            oechem.OEThrow.Fatal("Unable to open " +
                                  inputFile + " for reading.");

        if (! oechem.OEIs3DFormat(ims.GetFormat()))
            oechem.OEThrow.Fatal(inputFile + " is not in a 3D format.");

        int inftype=oechem.OEGetFileType(oechem.OEGetFileExtension(inputFile));
        if ((inftype == OEFormat.PDB) && (! keepAlts))
            oechem.OEThrow.Verbose("Default processing of alt locations " +
                                    "(keep just 'A' and ' ').");

        OESplitMolComplexOptions sopt = new OESplitMolComplexOptions();
        oebio.OESetupSplitMolComplexOptions(sopt, itf);

        OEGraphMol inmol = new OEGraphMol();
        if (! oechem.OEReadMolecule(ims, inmol))
            oechem.OEThrow.Fatal("Unable to read " + inputFile + ".");

        ims.close();

        if (inmol.NumAtoms() == 0)
            oechem.OEThrow.Fatal("Input molecule " + inputFile + " contains no atoms.");

        if (inmol.GetTitle().equals(""))
            inmol.SetTitle("input mol");

        oechem.OEThrow.Verbose("Processing " + inmol.GetTitle() + ".");

        if (! oechem.OEHasResidues(inmol))
            oechem.OEPerceiveResidues(inmol, OEPreserveResInfo.All);

        if (highestOcc || compareAlts) {
            OEAltLocationFactory alf = new OEAltLocationFactory(inmol);
            if (alf.GetGroupCount() != 0) {
                if (highestOcc) {
                    oechem.OEThrow.Verbose("Dropping alternate locations " +
                                           "from input.");
                    alf.MakePrimaryAltMol(inmol);
                }
                else if (compareAlts) {
                    oechem.OEThrow.Verbose("Fixing alternate location issues.");
                    inmol = new OEGraphMol(alf.GetSourceMol());
                }
            }
        }

        OEGraphMol outmol = new OEGraphMol();
        if (allSites)
            outmol = inmol;
        else {
          oechem.OEThrow.Verbose("Splitting out selected complex.");

          OESplitMolComplexOptions soptSiteSel =
                                             new OESplitMolComplexOptions(sopt);
          soptSiteSel.SetSplitCovalent(false); // do any cov lig splitting later

          OEAtomBondSetVector frags = new OEAtomBondSetVector();
          if (! oebio.OEGetMolComplexFragments(frags, inmol, soptSiteSel))
              oechem.OEThrow.Fatal("Unable to fragment " +
                                    inmol.GetTitle() + ".");

          int howManySites = oebio.OECountMolComplexSites(frags);
          if (howManySites < siteNum) {
            oechem.OEThrow.Warning("Binding site count (" + howManySites +
                                   ") less than requested site (" + siteNum +
                                   ") in " + inmol.GetTitle() + ".");
            return;
          }

          if (! oebio.OECombineMolComplexFragments(outmol, frags, soptSiteSel))
              oechem.OEThrow.Fatal("Unable to collect fragments from " +
                                    inmol.GetTitle() + ".");

          if (outmol.NumAtoms() == 0)
              oechem.OEThrow.Fatal("No fragments selected from " +
                                    inmol.GetTitle() + ".");
        }

        if (placeHyd) {
            oechem.OEThrow.Verbose("Adding hydrogens to complex.");

            OEPlaceHydrogensOptions hopt = new OEPlaceHydrogensOptions();
            hopt.SetAltsMustBeCompatible(compareAlts);
            hopt.SetStandardizeBondLen(standardize);
            hopt.SetWaterProcessing(waterProcess);
            hopt.SetBadClashOverlapDistance(badclash);
            hopt.SetFlipBiasScale(flipbias);
            hopt.SetMaxSubstateCutoff(maxStates);

            if (verbose) {
                OEPlaceHydrogensDetails details = new OEPlaceHydrogensDetails();
                if (! oebio.OEPlaceHydrogens(outmol, details, hopt))
                    oechem.OEThrow.Fatal("Unable to place hydrogens and get details on " +
                                          inmol.GetTitle() + ".");
                oechem.OEThrow.Verbose(details.Describe());
            }
            else {
                if (! oebio.OEPlaceHydrogens(outmol, hopt))
                    oechem.OEThrow.Fatal("Unable to place hydrogens on " +
                                          inmol.GetTitle() + ".");
            }
        }

        oemolostream oms1 = new oemolostream();
        String cplxFile = itf.GetString("-cplxout");
        if (! oms1.open(cplxFile))
            oechem.OEThrow.Fatal("Unable to open " +
                                  cplxFile + " for writing.");

        if (splitlig) {
            oechem.OEThrow.Verbose("Splitting ligand from complex.");

            int returnAllSites = 0;
            oebio.OESetupSplitMolComplexOptions(sopt, itf, returnAllSites);

            OEAtomBondSetVector frags = new OEAtomBondSetVector();
            if (! oebio.OEGetMolComplexFragments(frags, outmol, sopt))
                oechem.OEThrow.Fatal("Unable to fragment complex from " +
                                      inmol.GetTitle() + ".");

            OEUnaryRoleSetPred lfilter = sopt.GetLigandFilter();

            OEGraphMol protComplex = new OEGraphMol();

            if (! oebio.OECombineMolComplexFragments(protComplex,
                                                     frags,
                                                     sopt,
                                                     new OENotRoleSet(lfilter)))
                oechem.OEThrow.Fatal("Unable to collect complex from " +
                                      inmol.GetTitle() + ".");

            if (protComplex.NumAtoms() == 0)
                oechem.OEThrow.Warning("No complex identified in " +
                                        inmol.GetTitle() + ".");
            else
                oechem.OEWriteMolecule(oms1, protComplex);

            OEGraphMol lig = new OEGraphMol();

            if (! oebio.OECombineMolComplexFragments(lig, frags, sopt, lfilter))
                oechem.OEThrow.Fatal("Unable to collect ligand from " +
                                      inmol.GetTitle() + ".");

            if (lig.NumAtoms() == 0)
                oechem.OEThrow.Warning("No ligand identified in " +
                                        inmol.GetTitle() + ".");
            else {
                oemolostream oms2 = new oemolostream();
                if (splitlig) {
                    String ligFile = itf.GetString("-ligout");
                    if (! oms2.open(ligFile))
                        oechem.OEThrow.Fatal("Unable to open " +
                                              ligFile + " for writing.");
                }
                oechem.OEThrow.Verbose("Ligand: " + lig.GetTitle());
                oechem.OEWriteMolecule(oms2, lig);
                oms2.close();
            }
        }
        else
            oechem.OEWriteMolecule(oms1, outmol);

        oms1.close();
    }
}
