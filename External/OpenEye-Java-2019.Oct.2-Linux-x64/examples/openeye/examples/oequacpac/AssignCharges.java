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
package openeye.examples.oequacpac;

import java.io.IOException;
import java.net.URL;

import openeye.oequacpac.*;
import openeye.oechem.*;

public class AssignCharges {

    static boolean AssignChargesByName(OEMol mol, String name) {
      boolean optimize   = true;
      boolean symmetrize = true;

      if (name.equals( "noop"))
          return oequacpac.OEAssignCharges(mol, new OEChargeEngineNoOp());
      else if (name.equals( "mmff") || name.equals( "mmff94"))
          return oequacpac.OEAssignCharges(mol, new OEMMFF94Charges());
      else if (name.equals( "am1bcc"))
          return oequacpac.OEAssignCharges(mol, new OEAM1BCCCharges());
      else if (name.equals( "am1bccnosymspt"))
          return oequacpac.OEAssignCharges(mol, new OEAM1BCCCharges(!optimize,
                                                                    !symmetrize));
      else if (name.equals( "amber") || name.equals( "amberff94"))
          return oequacpac.OEAssignCharges(mol, new OEAmberFF94Charges());
      else if (name.equals( "am1bccelf10"))
          return oequacpac.OEAssignCharges(mol, new OEAM1BCCELF10Charges());
      return false;
    }

    public static void main(String[] argv) {
        URL fileURL = AssignCharges.class.getResource("AssignCharges.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        if (! oechem.OEParseCommandLine(itf, argv, "AssignCharges"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        oemolistream ifs = new oemolistream();

        String inputFile = itf.GetString("-in");
        if (! ifs.open(inputFile))
            oechem.OEThrow.Fatal("Unable to open " + inputFile + " for reading");

        oemolostream ofs = new oemolostream();

        String outFile = itf.GetString("-out");
        if (! ofs.open(outFile))
            oechem.OEThrow.Fatal("Unable to open " + outFile + " for writing");

        String chargeName = itf.GetString("-method");

        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (! AssignChargesByName(mol, chargeName))
                oechem.OEThrow.Warning("Unable to assign " + chargeName +
                                       " charges to mol " + mol.GetTitle());

            oechem.OEWriteMolecule(ofs, mol);
        }

        ifs.close();
        ofs.close();
    }
}
