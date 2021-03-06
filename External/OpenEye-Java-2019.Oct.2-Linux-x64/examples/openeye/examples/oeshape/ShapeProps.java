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
package openeye.examples.oeshape;

import openeye.oechem.*;
import openeye.oeshape.*;

public class ShapeProps {

    public static void main(String[] args) {
        if (args.length!=1) {
            System.out.println("ShapeProps <infile>");
            System.exit(0);
        }

        oemolistream ifs = new oemolistream(args[0]);

        OEGraphMol mol = new OEGraphMol();

        while (oechem.OEReadMolecule(ifs,mol)) {
            System.out.println("              Title: "+mol.GetTitle());
            System.out.println("             Volume: "+oeshape.OECalcVolume(mol));
            System.out.println("Volume: (without H): "+
                    oeshape.OECalcVolume(mol, false));

            float [] smult = new float[14];
            oeshape.OECalcShapeMultipoles(mol, smult);

            System.out.println("  Steric multipoles:");
            System.out.println("           monopole: "+smult[0]);
            System.out.println("        quadrupoles: "+ 
                    smult[1]+" "+smult[2]+" "+smult[3]);
            System.out.println("          octopoles:");
            System.out.println("                xxx: "+smult[4]+
                    " yyy: "+smult[5]+" zzz: "+smult[6]);
            System.out.println("                xxy: "+smult[7]+
                    " xxz: "+smult[8]+" yyx: "+smult[9]);
            System.out.println("                yyz: "+smult[10]+
                    " zzx: "+smult[11]+" zzy: "+smult[12]);
            System.out.println("                xyz: "+smult[13]);

            System.out.println("");
        }
        ifs.close();
    }
}


