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

import openeye.oechem.*;
import openeye.oequacpac.*;

public class WibergBondOrders {

    public static void main(String[] args) {
        if (args.length!=1) {
            System.out.println("WibergBondOrders <infile>");
            System.exit(0);
        }

        oemolistream ifs = new oemolistream(args[0]);
        OEAM1 am1 = new OEAM1();
        OEAM1Results results = new OEAM1Results();
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            for (OEConfBase conf : mol.GetConfs()) {
                System.out.println(" molecule = "+mol.GetTitle());
                System.out.println(" conformer = "+conf.GetIdx());
                if (am1.CalcAM1(results, conf)) {
                    int nbonds = 0;
                    for (OEBondBase bond : mol.GetBonds(new OEIsRotor())) {
                        nbonds += 1;
                        System.out.println(results.GetBondOrder(bond.GetBgnIdx(), bond.GetEndIdx()));
                    }
                    System.out.println(" Rotatable bonds: "+nbonds);
                }
            }
        }
    }
}

