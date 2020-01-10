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

import openeye.oechem.*;
import openeye.oebio.*;

public class PhiPsi {

  private void displayUsage() {
    System.err.println("usage: PhiPsi <inmol>");
    System.exit(1);
  } 
  public void showPhiPsi(String infile) {
    oemolistream ifs = new oemolistream();
    if (!ifs.open(infile)) {
      System.err.println("Unable to open infile: " + infile);
      return;
    }
    
    double phi, psi;
    int molCt=0;
    OEGraphMol mol = new OEGraphMol();
    while (oechem.OEReadMolecule(ifs, mol)) {
      ++molCt;
      System.out.println("======================================");      
      System.out.println("Molecule: "+molCt+" ( "+mol.GetTitle()+" )");
      if (! oechem.OEHasResidues(mol))
        oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);
      
      OEHierView hv = new OEHierView(mol);
      for (OEHierResidue res : hv.GetResidues()) {
        if (!oebio.OEIsStandardProteinResidue(res)) 
          continue;
        phi = oebio.OEGetPhi(res);
        psi = oebio.OEGetPsi(res);
        
        OEResidue r = res.GetOEResidue();
        System.out.print("  "+r.GetName()+" "+r.GetChainID()+" "+r.GetResidueNumber());
        System.out.println(" ("+phi+","+psi+")");
      }
      System.out.println();
    }
    ifs.close();
  }
  public static void main(String argv[]) {
    PhiPsi app = new PhiPsi();
    if (argv.length != 1)
      app.displayUsage();
    app.showPhiPsi(argv[0]);
  }
}
