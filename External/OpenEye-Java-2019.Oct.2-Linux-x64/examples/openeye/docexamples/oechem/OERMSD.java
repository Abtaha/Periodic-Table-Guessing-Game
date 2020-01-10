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
package openeye.docexamples.oechem;

import openeye.oechem.*;

/**************************************************************
* USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
**************************************************************/

public class OERMSD {
  private static void OERMSD_Array(OEDoubleArray refcrds, OEDoubleArray fitcrds, int size) {
    double rmsd1 = oechem.OERMSD(refcrds, fitcrds, size);

    boolean overlay = true;
    double rmsd2 = oechem.OERMSD(refcrds, fitcrds, size, overlay);

    OEDoubleArray rotmat = new OEDoubleArray(9);
    double rmsd3 = oechem.OERMSD(refcrds, fitcrds, size, overlay, rotmat);

    OEDoubleArray transvec = new OEDoubleArray(3);
    double rmsd4 = oechem.OERMSD(refcrds, fitcrds, size, overlay, rotmat, transvec);
  }


  private static void OERMSD_Full_MolBase(OEMolBase ref, OEMolBase fit) {
    double rmsd1 = oechem.OERMSD(ref, fit);

    boolean automorf = true;
    double rmsd2 = oechem.OERMSD(ref, fit, automorf);

    boolean heavyOnly = true;
    double rmsd3 = oechem.OERMSD(ref, fit, automorf, heavyOnly);

    boolean overlay = true;
    double rmsd4 = oechem.OERMSD(ref, fit, automorf, heavyOnly, overlay);

    OEDoubleArray rotmat = new OEDoubleArray(9);
    double rmsd5 = oechem.OERMSD(ref, fit, automorf, heavyOnly, overlay, rotmat);

    OEDoubleArray transvec = new OEDoubleArray(3);
    double rmsd6 = oechem.OERMSD(ref, fit, automorf, heavyOnly, overlay, rotmat, transvec);
  }

  private static void OERMSD_Full_MCMolBase(OEMolBase ref, OEMCMolBase fit) {
    int nConfs = fit.GetMaxConfIdx();
    OEDoubleArray vecRmsd = new OEDoubleArray(nConfs);
    oechem.OERMSD(ref, fit, vecRmsd);

    boolean automorf = true;
    oechem.OERMSD(ref, fit, vecRmsd, automorf);

    boolean heavyOnly = true;
    oechem.OERMSD(ref, fit, vecRmsd, automorf, heavyOnly);

    boolean overlay = true;
    oechem.OERMSD(ref, fit, vecRmsd, automorf, heavyOnly, overlay);

    OEDoubleArray rotmat = new OEDoubleArray(9*nConfs);
    oechem.OERMSD(ref, fit, vecRmsd, automorf, heavyOnly, overlay, rotmat);

    OEDoubleArray transvec = new OEDoubleArray(3*nConfs);
    oechem.OERMSD(ref, fit, vecRmsd, automorf, heavyOnly, overlay, rotmat, transvec);
  }


  private static void OERMSD_Part_MolBase(OEMolBase ref, OEMolBase fit) {
    OEMatch match = new OEMatch();
    OEAtomBaseIter aRef = ref.GetAtoms();
    OEAtomBaseIter aFit = fit.GetAtoms();
    while (aRef.IsValid() && aFit.IsValid()) {
        match.AddPair(aRef.Target(), aFit.Target());
        aRef.Increment();
        aFit.Increment();
    }

    double rmsd1 = oechem.OERMSD(ref, fit, match);

    boolean overlay = true;
    double rmsd2 = oechem.OERMSD(ref, fit, match, overlay);

    OEDoubleArray rotmat = new OEDoubleArray(9);
    double rmsd3 = oechem.OERMSD(ref, fit, match, overlay, rotmat);

    OEDoubleArray transvec = new OEDoubleArray(3);
    double rmsd4 = oechem.OERMSD(ref, fit, match, overlay, rotmat, transvec);
  }

  private static void OERMSD_Part_MCMolBase(OEMolBase ref, OEMCMolBase fit) {
    OEMatch match = new OEMatch();
    OEAtomBaseIter aRef = ref.GetAtoms();
    OEAtomBaseIter aFit = fit.GetAtoms();
    while (aRef.IsValid() && aFit.IsValid()) {
        match.AddPair(aRef.Target(), aFit.Target());
        aRef.Increment();
        aFit.Increment();
    }

    int nConfs = fit.GetMaxConfIdx();
    OEDoubleArray vecRmsd = new OEDoubleArray(nConfs);
    oechem.OERMSD(ref, fit, vecRmsd, match);

    boolean overlay = true;
    oechem.OERMSD(ref, fit, vecRmsd, match, overlay);

    OEDoubleArray rotmat = new OEDoubleArray(9*nConfs);
    oechem.OERMSD(ref, fit, vecRmsd, match, overlay, rotmat);

    OEDoubleArray transvec = new OEDoubleArray(3*nConfs);
    oechem.OERMSD(ref, fit, vecRmsd, match, overlay, rotmat, transvec);
  }



  ///////////////////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////////////////
  public static void main(String argv[]) {
    OEGraphMol mol1 = new OEGraphMol();
    OEDoubleArray vecCoords1 = new OEDoubleArray(mol1.GetMaxAtomIdx() * 3);
    mol1.GetCoords(vecCoords1);

    OEGraphMol mol2 = new OEGraphMol();
    OEDoubleArray vecCoords2 = new OEDoubleArray(mol2.GetMaxAtomIdx() * 3);
    mol2.GetCoords(vecCoords2);

    OERMSD_Array(vecCoords1, vecCoords2, mol1.GetMaxAtomIdx());
    OERMSD_Full_MolBase(mol1, mol2);

    OEMol mcmol = new OEMol();
    OERMSD_Full_MCMolBase(mol1, mcmol);

    OERMSD_Part_MolBase(mol1, mol2);
    OERMSD_Part_MCMolBase(mol1, mcmol);
  }
}
//@ </SNIPPET>
