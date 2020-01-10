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

/*
* Perform a ramachandran analysis on your protein, print outliers
*/

package openeye.docexamples.oebio;

import java.io.*;
import openeye.oechem.*;
import openeye.oebio.*;

public class RamaOutlierCheck {

  static void RamaCheck(OEMolBase mol) {
    // @ <SNIPPET-RAMA-OUTLIER-CHECK>

    // Loop over the CA atoms in the protein
    for (OEAtomBase atom : mol.GetAtoms(new OEIsCAlpha())) {
      OERamachandranAnalysis rama = new OERamachandranAnalysis(atom);
      // Print out information about outliers for further analysis
      if (rama.GetRamaCategory() == OERamaCategory.Outlier)
      {
        OEResidue res = oechem.OEAtomGetResidue(atom);
        System.out.println("Found: " + oebio.OEGetRamachandranCategoryName(rama.GetRamaCategory()));
        System.out.println("  Residue: " + res.GetName() + " " + String.valueOf(res.GetResidueNumber()) + " " + res.GetChainID());
        System.out.println("  Type: " + oebio.OEGetRamachandranTypeName(rama.GetRamaType()) + ", Score: " + String.valueOf(rama.GetRamaScore()));
      }
    }
    // @ </SNIPPET-RAMA-OUTLIER-CHECK>
  }

  static OEMolBase ReadProteinFromPDB(String pdb_file, OEMolBase mol)
  {
    oemolistream ifs = new oemolistream();
    int flavor = OEIFlavor.PDB.Default|OEIFlavor.PDB.DATA|OEIFlavor.PDB.ALTLOC;
    ifs.SetFlavor(OEFormat.PDB, flavor);

    if (!ifs.open(pdb_file)) {
        oechem.OEThrow.Fatal("Unable to open " + pdb_file + " for reading");
    }

    OEGraphMol temp_mol = new OEGraphMol();
    if (!oechem.OEReadMolecule(ifs, temp_mol)) {
        oechem.OEThrow.Fatal("Unable to read molecule from " +  pdb_file);
    }
    ifs.close();

    OEAltLocationFactory fact = new OEAltLocationFactory(temp_mol);
    mol.Clear();
    fact.MakePrimaryAltMol(mol);
    return (mol);
  }

  public static void main(String argv[]) {
    if (argv.length != 1) {
        oechem.OEThrow.Usage("RamaOutlierCheck <mol-infile>");
    }
    OEGraphMol mol = new OEGraphMol();
    ReadProteinFromPDB(argv[0], mol);
    RamaCheck(mol);
  }
}
