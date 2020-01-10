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

public class FlexResidues {
    public static void main(String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);
        oechem.OEParseCommandLine (itf, args, "FlexResidues");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-in")))
          oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-in") + " for reading");

        oemolistream pfs = new oemolistream();
        if (!pfs.open(itf.GetString("-protein")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-protein") + " for reading");

        oemolostream olfs = new oemolostream();
        if (!olfs.open(itf.GetString("-outl")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-outl") + " for writing");

        oemolostream opfs = new oemolostream();
        if (!opfs.open(itf.GetString("-outp")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-outp") + " for writing");

        OEGraphMol protein = new OEGraphMol();
        OEGraphMol ligand = new OEGraphMol();
        oechem.OEReadMolecule(pfs, protein);
        oechem.OEReadMolecule(ifs, ligand);
        pfs.close();
        ifs.close();

        OESzybkiOptions opts = new OESzybkiOptions();
        opts.GetGeneralOptions().SetForceFieldType(OEForceFieldType.MMFF94S);
        opts.GetProteinOptions().SetProteinElectrostaticModel(OEProteinElectrostatics.ExactCoulomb);
        opts.GetProteinOptions().SetProteinFlexibilityType(OEProtFlex.SideChainsList);
        opts.SetRunType(OERunType.CartesiansOpt);
        opts.GetOptOptions().SetMaxIter(2000);
        opts.GetOptOptions().SetGradTolerance(1.0e-6);
        
        OEStringIter res_num = itf.GetStringList("-residues");
        res_num.ToFirst();
        for (String rn : res_num) {
            for (OEAtomBase atom : protein.GetAtoms()) {
                OEResidue res = oechem.OEAtomGetResidue(atom);
                if(Integer.toString(res.GetResidueNumber()).equals(rn)) {
                    opts.AddFlexibleResidue(res);
                    break;
                }
            }
        }

        //optimization
        OESzybki sz = new OESzybki(opts);
        sz.SetProtein(protein);
        OESzybkiResults res = new OESzybkiResults();
        sz.call(ligand, res);
  
        oechem.OEWriteMolecule(olfs, ligand);
        sz.GetProtein(protein);
        oechem.OEWriteMolecule(opfs, protein);
  
        olfs.close();
        opfs.close();
    }

    private static String interfaceData =
"!PARAMETER -in\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Input ligand file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -protein\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Input protein file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -outl\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Output ligand file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -outp\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Output protein file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -residues\n" +
"!TYPE int\n" +
"!LIST true\n" +
"!REQUIRED true\n" +
"!BRIEF List of residues numbers to be optimized along with the ligand.\n" +
"!END";
}
