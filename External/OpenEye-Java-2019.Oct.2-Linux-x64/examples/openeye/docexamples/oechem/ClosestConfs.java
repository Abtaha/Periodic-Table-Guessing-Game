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
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class ClosestConfs {
  public static void main(String argv[]) {
    oemolistream ifs = new oemolistream();
    if (!ifs.open("drugs.oeb"))
      oechem.OEThrow.Fatal("Unable to open file for reading");

    //@ <SNIPPET>
    OEMol mol = new OEMol();
    oechem.OEReadMolecule(ifs, mol);
    OEConfRMSD crmsd = new OEConfRMSD(mol);

    double minRMSD = Double.MAX_VALUE;
    OEConfBase confi;
    OEConfBase confj;
    int confiIdx = 0;
    int confjIdx = 0;
    for (OEConfBaseIter ciiter = mol.GetConfs(); ciiter.hasNext();  ciiter.Increment()) {
      for (OEConfBaseIter cjiter = ciiter.Increment(); cjiter.hasNext();  cjiter.Increment()) {
        confi = ciiter.next();
        confj = cjiter.next();
        OETrans trans = new OETrans();
        double dist = crmsd.MinimizeRMSD(confi, confj, trans);
        if (dist < minRMSD)
        {
          minRMSD = dist;
          confiIdx = confi.GetIdx();
          confjIdx = confj.GetIdx();
        }
      }
    }

    System.out.println("The closest conformers are " + confiIdx + " and " + confjIdx +
          " with RMSD = " + minRMSD);
    //@ </SNIPPET>
  }
}

