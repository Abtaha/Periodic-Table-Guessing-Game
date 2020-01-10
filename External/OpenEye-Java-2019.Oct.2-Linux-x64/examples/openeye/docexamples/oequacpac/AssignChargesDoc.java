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
package openeye.docexamples.oequacpac;

import java.io.IOException;
import java.net.URL;

import openeye.oequacpac.*;
import openeye.oechem.*;

public class AssignChargesDoc {

    static boolean AssignChargesByName(OEMol mol, String name) {
      if (name.equals( "noop"))
          return oequacpac.OEAssignCharges(mol, new OEChargeEngineNoOp());
      else if (name.equals( "clear"))
          return oequacpac.OEAssignCharges(mol, new OEClearCharges());
      else if (name.equals( "formal"))
          return oequacpac.OEAssignCharges(mol, new OEFormalToPartialCharges());
      else if (name.equals( "initial"))
          return oequacpac.OEAssignCharges(mol, new OEInitialCharges());
      else if (name.equals( "gasteiger"))
          return oequacpac.OEAssignCharges(mol, new OEGasteigerCharges());
      else if (name.equals( "mmff") || name.equals( "mmff94")) {
          if (oequacpac.OEAssignCharges(mol, new OEMMFF94Charges()))
              //...
              return true;
          else
              return false;
      }
      else if (name.equals( "am1")) {
          boolean optimizationSetting = false;
          if (oequacpac.OEAssignCharges(mol, new OEAM1Charges(optimizationSetting)))
              //...
              return true;
          else
              return false;
      }
      else if (name.equals( "am1bcc")) {
          OEAM1BCCCharges chargeEngine = new OEAM1BCCCharges();
          chargeEngine.SetSymmetrize(false);
          if (oequacpac.OEAssignCharges(mol, chargeEngine))
              //...
              return true;
          else
              return false;
      }
      else if (name.equals( "amber") || name.equals( "amberff94"))
          return oequacpac.OEAssignCharges(mol, new OEAmberFF94Charges());
      else if (name.equals( "am1bccelf10"))
          return oequacpac.OEAssignCharges(mol, new OEAM1BCCELF10Charges());
      return false;
    }

    public static void main(String[] argv) {
        URL fileURL = AssignChargesDoc.class.getResource("AssignChargesDoc.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        if (! oechem.OEParseCommandLine(itf, argv, "AssignChargesDoc"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        oemolistream ims = new oemolistream();

        String inputFile = itf.GetString("-in");
        if (! ims.open(inputFile))
            oechem.OEThrow.Fatal("Unable to open " + inputFile + " for reading");

        OEMol mol = new OEMol();
        if (! oechem.OEReadMolecule(ims, mol))
            oechem.OEThrow.Fatal("Cannot read from " + inputFile);
        ims.close();

        oemolostream oms = new oemolostream();

        String outFile = itf.GetString("-out");
        if (! oms.open(outFile))
            oechem.OEThrow.Fatal("Unable to open " + outFile + " for writing");

        String chargeName = itf.GetString("-charge");

        if (! AssignChargesByName(mol, chargeName))
            oechem.OEThrow.Fatal("Unable to assign " +
                                  chargeName + " charges to " + inputFile);
        else
            oechem.OEWriteMolecule(oms, mol);

        oms.close();
    }
}
