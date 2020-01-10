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
package openeye.examples.oebio;

import java.io.*;
import java.util.ArrayList;
import openeye.oechem.*;
import openeye.oebio.*;

public class AltlocfactResidues {

// list alternate location code and occupancy by group and residue
static void PrintResidues(OEMolBase mol) {
    if (! oechem.OEHasResidues(mol))
        oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

    OEAltLocationFactory alf = new OEAltLocationFactory(mol);

    System.out.println(mol.GetTitle() + " - " + alf.GetGroupCount()
                                      + " alternate location groups:");

    for (OEAltGroup grp : alf.GetGroups()) {
        System.out.print((grp.GetGroupID() + 1) + ") "
                      + grp.GetLocationCount() + " alternate locations");

      OEResidue prev = new OEResidue();
      String prevCodes = "";
      ArrayList<Float>  sumOcc = new ArrayList<Float>();
      ArrayList<Integer> atNum = new ArrayList<Integer>();

      for (OEAtomBase atom : alf.GetAltAtoms(grp)) {
        OEResidue res = oechem.OEAtomGetResidue(atom);

        if (! oechem.OESameResidue(res, prev)) {
          for (int i = 0; i < prevCodes.length(); ++i) {
            long avg_occ = Math.round((sumOcc.get(i)*100.0)/atNum.get(i));
            System.out.print(prevCodes.charAt(i) + "(" + avg_occ + "%) ");
          }
          System.out.println("");
          prevCodes = "";
          sumOcc.clear();
          atNum.clear();
          System.out.print("\t" + res.GetName()
                                + res.GetResidueNumber()
                                + res.GetInsertCode()
                   + " chain '" + res.GetChainID() +"': ");
          prev = res;
        }
        char code = res.GetAlternateLocation();
        int whichCode = prevCodes.indexOf(code);
        if (whichCode < 0) {
          prevCodes += code;
          sumOcc.add(res.GetOccupancy());
          atNum.add(1);
        }
        else {
          float occ = sumOcc.get(whichCode) + res.GetOccupancy();
          sumOcc.set(whichCode, occ);
          int n = atNum.get(whichCode) + 1;
          atNum.set(whichCode, n);
        }
      }
      for (int i = 0; i < prevCodes.length(); ++i) {
        long avg_occ = Math.round((sumOcc.get(i)*100.0)/atNum.get(i));
        System.out.print(prevCodes.charAt(i) + "(" + avg_occ + "%) ");
      }
      System.out.println("");
    }
  }

  public static void main(String argv[]) {
    if (argv.length != 1) {
      oechem.OEThrow.Usage("AltlocfactResidues <mol-infile>");
    }
    oemolistream ims = new oemolistream();
    if(! ims.open(argv[0])) {
      oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
    }
    // need this flavor to read alt loc atoms
    ims.SetFlavor(OEFormat.PDB, OEIFlavor.PDB.ALTLOC);

    OEGraphMol mol = new OEGraphMol();
    while(oechem.OEReadMolecule(ims, mol)) {
      PrintResidues(mol);
    }
    ims.close();
  }
}
