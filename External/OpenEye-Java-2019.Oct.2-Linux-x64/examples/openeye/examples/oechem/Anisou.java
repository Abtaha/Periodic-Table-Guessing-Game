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
/****************************************************************************
 * Extract compound(s) from a file based on molecule title
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class Anisou {

    private static void analyze(OEInterface itf) {
      boolean verbose = itf.GetBool("-verbose");
      String ifname   = itf.GetString("-input");

      oemolistream ims = new oemolistream();
      if (! ims.open(ifname))
          oechem.OEThrow.Fatal("Unable to open " + ifname + " for reading");

       ims.SetFlavor(OEFormat.PDB, OEIFlavor.PDB.Default|OEIFlavor.PDB.DATA);

       OEGraphMol mol = new OEGraphMol();
       while (oechem.OEReadMolecule(ims, mol)) {
           if (verbose) {
              if (! oechem.OEHasResidues(mol))
                  oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

              for(OEAtomBase atom : mol.GetAtoms()) {
                  OEResidue res = oechem.OEAtomGetResidue(atom);

                  OEAnisoUij uij = new OEAnisoUij();
                  if (oechem.OEGetAnisou(uij, atom))
                      oechem.OEThrow.Info(mol.GetTitle() + " " +
                                          atom.GetName() +
                                          res.GetAlternateLocation() + " " +
                                          res.GetName() +
                                          res.GetResidueNumber() +
                                          res.GetInsertCode() + " " +
                                          res.GetChainID() + " (" +
                                          "u11=" + uij.GetU11() + ", " +
                                          "u22=" + uij.GetU22() + ", " +
                                          "u33=" + uij.GetU33() + ", " +
                                          "u12=" + uij.GetU12() + ", " +
                                          "u13=" + uij.GetU13() + ", " +
                                          "u23=" + uij.GetU23() + ")");
                  else
                      oechem.OEThrow.Info(mol.GetTitle() + " " +
                                          atom.GetName() +
                                          res.GetAlternateLocation() + " " +
                                          res.GetName() +
                                          res.GetResidueNumber() +
                                          res.GetInsertCode() + " " +
                                          res.GetChainID() +
                                          " -no-anisou-");
              }
          }
          oechem.OEThrow.Info(mol.GetTitle() + " " +
                              oechem.OECount(mol, new OEHasAnisou()) +
                              " atoms with anisou data (out of " +
                              mol.NumAtoms() + ")");
      }
    }

    public static void main(String argv[]) {
        URL fileURL = Anisou.class.getResource("Anisou.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "Anisou", argv);
            analyze(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
