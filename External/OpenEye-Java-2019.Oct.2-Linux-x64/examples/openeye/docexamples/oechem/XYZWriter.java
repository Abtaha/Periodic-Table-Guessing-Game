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

public class XYZWriter {
    public static void main(String argv[]) {
        if (argv.length != 1)
            oechem.OEThrow.Usage("XYZWriter <input>");

        oemolistream ifs = new oemolistream();

        if (!ifs.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0]);

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) 
        {
            System.out.println(mol.NumAtoms());
            System.out.println(mol.GetTitle());

            float coords[] = new float[mol.GetMaxAtomIdx() * 3];
            mol.GetCoords(coords);

            for (OEAtomBase atom : mol.GetAtoms())
            {
                int idx = atom.GetIdx();

                System.out.printf("%-3s%11.5f%11.5f%11.5f\n", 
                        oechem.OEGetAtomicSymbol(atom.GetAtomicNum()),
                        coords[idx*3],
                        coords[idx*3+1],
                        coords[idx*3+2]);
            }      
        }
        ifs.close();
    }
}
//@ </SNIPPET>
