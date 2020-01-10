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
import openeye.oegrid.*;
import openeye.oeshape.*;

public class CreateQuery {

    public static void main(String[] args) {
        if (args.length!=2) {
            System.out.println("CreateQuery <molfile> <queryfile>");
            System.exit(0);
        }

        oemolistream molfs = new oemolistream(args[0]);
        OEGraphMol mol = new OEGraphMol();
        oechem.OEReadMolecule(molfs, mol);
        molfs.close();

        String ext = oechem.OEGetFileExtension(args[1]);
        if (!ext.equals("sq"))
            oechem.OEThrow.Fatal("Only can write shape query to .sq output file format");

        // Use OEOverlapPrep to remove hydrogens and add
        // color atoms to the molecule
        OEOverlapPrep prep = new OEOverlapPrep();
        prep.Prep(mol);

        // Get the color atoms, create gaussians and add them
        // to the shape query
        OEShapeQuery query = new OEShapeQuery();
        for (OEAtomBase atom : oeshape.OEGetColorAtoms(mol)){
            OEFloatArray coords = new OEFloatArray(3);
            mol.GetCoords(atom, coords);
            OEGaussian gauss = new OEGaussian(1.0f, 1.0f, coords, oeshape.OEGetColorType(atom));
            query.AddColorGaussian(gauss);
        }

        // Remove color atoms from the molecule and add to the query
        oeshape.OERemoveColorAtoms(mol);
        query.SetMolecule(mol);

        oeshape.OEWriteShapeQuery(args[1], query);
        System.out.println("shape query created");
    }
}

