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

public class UserColor {

    public static void main(String[] args) {
        if (args.length!=2) {
            System.out.println("UserColor <reffile> <overlayfile>");
            System.exit(0);
        }

        oemolistream reffs = new oemolistream(args[0]);
        oemolistream fitfs = new oemolistream(args[1]);

        OEGraphMol refmol = new OEGraphMol();
        oechem.OEReadMolecule(reffs, refmol);
        reffs.close();

        // Modify ImplicitMillsDean color force field by
        // adding user defined color interactions
        OEColorForceField cff = new OEColorForceField();
        cff.Init(OEColorFFType.ImplicitMillsDean);
        cff.ClearInteractions();
        int donorType = cff.GetType("donor");
        int accepType = cff.GetType("acceptor");
        cff.AddInteraction(donorType, donorType, "gaussian", -1.0f, 1.0f);
        cff.AddInteraction(accepType, accepType, "gaussian", -1.0f, 1.0f);

        // Prepare reference molecule for calculation
        // With default options this will add required color atoms
        // Set the modified color force field for addignment
        OEOverlapPrep prep = new OEOverlapPrep();
        prep.SetColorForceField(cff);
        prep.Prep(refmol);

        // Get appropriate function to calculate exact color
        // Set appropriate options to use the user defined color
        OEColorOptions options = new OEColorOptions();
        options.SetColorForceField(cff);
        OEExactColorFunc colorFunc = new OEExactColorFunc(options);
        colorFunc.SetupRef(refmol);

        OEOverlapResults res = new OEOverlapResults();
        OEGraphMol fitmol = new OEGraphMol();
        while (oechem.OEReadMolecule(fitfs, fitmol)) {
            prep.Prep(fitmol);
            colorFunc.Overlap(fitmol, res);
            System.out.print("Fit. Title: "+fitmol.GetTitle()+" ");
            System.out.println("Color TAnimoto: "+res.GetColorTanimoto());
        } 
    }
}


