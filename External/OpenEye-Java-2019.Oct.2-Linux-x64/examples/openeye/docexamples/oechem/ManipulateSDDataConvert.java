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
//@ <SNIPPET>
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class ManipulateSDDataConvert {

    public static void main(String argv[]) {

        String molstring =  new String(
                "ethene                                                               \n" +
                        "  -OEChem-04060917472D                                               \n" +
                        "                                                                     \n" +
                        "  2  1  0     0  0  0  0  0  0999 V2000                              \n" +
                        "    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                        "    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                        "  1  2  1  0  0  0  0                                                \n" +
                        "M  END                                                               \n" +
                        "> <weight>                                                           \n" +
                        "30.069040000000                                                      \n" +
                        "                                                                     \n" +
                "$$$$                                                                 \n");

        oemolistream ims =  new oemolistream();
        ims.SetFormat(OEFormat.SDF);
        ims.openstring(molstring);

        OEGraphMol mol = new OEGraphMol();
        oechem.OEReadMolecule(ims,mol);
        ims.close();
        if (oechem.OEHasSDData(mol,"weight")) {
            String str =  oechem.OEGetSDData(mol,"weight");
            float weight = Float.valueOf(str);
            System.out.println("weight= " + weight);
        }

        oechem.OESetSDData(mol, "number of atoms", Integer.toString(mol.NumAtoms()));
    }
}
//@ </SNIPPET>
