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
package openeye.docexamples.oeomega;

import openeye.oechem.*;
import openeye.oeomega.*;
import java.util.ArrayList;

public class AddTorsionRule {
    public static void main(String[] args) {
        if (args.length != 1)
            oechem.OEThrow.Usage("AddTorsionRule <outfile>");

        OEMol mol = new OEMol();
        oechem.OESmilesToMol(mol, "O=COC");

        oemolostream ofs = new oemolostream();
        if (!ofs.open(args[0]))
            oechem.OEThrow.Fatal("Unable to open " + args[0] + "for writing");

        OEOmegaOptions omegaOpts = new OEOmegaOptions();
        OEOmega omega = new OEOmega(omegaOpts);
        OETorLib torlib = new OETorLib();

        // Adding the torsion rule "[O:1]=[C:2]-[O:3][CH3:4] 90" as a string
        // This takes precedent over previous rule
        final String rule = "[O:1]=[C:2]-[O:3][CH3:4] 90";
        if(!torlib.AddTorsionRule(rule))
            oechem.OEThrow.Fatal("Failed to add torsion rule: " + rule);

        omegaOpts.SetTorLib(torlib);
        omega.SetOptions(omegaOpts);
        if(omega.call(mol))
            oechem.OEWriteMolecule(ofs, mol);

        // Adding torsion rule "[O:1]=[C:2]-[O:3][CH3:4] 45" as a query
        // molecule. This takes precedent over default rule
        OEQMol qmol = new OEQMol();
        oechem.OEParseSmarts(qmol, "[O:1]=[C:2]-[O:3][CH3:4]");
        ArrayList<Integer> degrees = new ArrayList<Integer>();
        degrees.add(45);

        if (!torlib.AddTorsionRule(qmol, degrees))
            oechem.OEThrow.Fatal("Failed to add torsion rule");

        omegaOpts.SetTorLib(torlib);
        omega.SetOptions(omegaOpts);
        if (omega.call(mol))
            oechem.OEWriteMolecule(ofs, mol);
        ofs.close();
    }
}
