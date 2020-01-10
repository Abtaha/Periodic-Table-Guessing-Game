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

// This example reads in a molecule and performs Hermite expansion of
//that molecule to a given order in the NPolyMax parameter and outputs
//the resulting Hermite approximated shape to a grid.

package openeye.examples.oeshape;

import openeye.oechem.*;
import openeye.oeshape.*;
import openeye.oegrid.*;
import java.net.URL;
import java.io.IOException;
import java.util.Vector;

public class HermiteExpansion {
    public static void main(String[] args) {
        URL fileURL = HermiteExpansion.class.getResource("HermiteExpansion.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface(fileURL, "HermiteExpansion", args);
        } catch(IOException e) {
            oechem.OEThrow.Fatal("Unable to open itf file");
        }

        // Get the input parameters from the OEInterface

        int NPolyMax = itf.GetInt("-NPolyMax");
        float gridspacing = itf.GetFloat("-gridspacing");
        String ifname = itf.GetString("-inputfile");
        String ofname = itf.GetString("-outputgrid");

        oemolistream ifs = new oemolistream();

        if (!ifs.open(ifname)) {
            oechem.OEThrow.Fatal("Unable to open " + ifname + " for reading");
        }


        if (!ofname.endsWith(".grd")) {
            oechem.OEThrow.Fatal("Output grid file extension hast to be '.grd' ");
        }   

        OEMol mol = new OEMol();

        if (!oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEThrow.Fatal("Unable to read molecule in " + ifname);
        }

        OEOverlapPrep prep = new OEOverlapPrep();
        prep.SetAssignColor(false);
        prep.Prep(mol);    


        OETrans transfm = new OETrans();
        oeshape.OEOrientByMomentsOfInertia(mol, transfm);

        OEHermiteOptions hermiteoptions = new OEHermiteOptions();
        hermiteoptions.SetNPolyMax(NPolyMax);
        hermiteoptions.SetUseOptimalLambdas(true);

        OEHermite hermite = new OEHermite(hermiteoptions);

        if (!hermite.Setup(mol)) {
            oechem.OEThrow.Fatal("Was not able to Setup the molecule for the OEHermite class.");
        }

        OEHermiteOptions hopts = hermite.GetOptions();

        System.out.println("Best lambdas found X=" 
                            + String.valueOf(hopts.GetLambdaX()) + "  Y="
                            + String.valueOf(hopts.GetLambdaY()) + "  Z="
                            + String.valueOf(hopts.GetLambdaZ()));

        System.out.println("Hermite self-overlap =" + String.valueOf(hermite.GetSelfOverlap()));
        
        Vector<Double> coeffs = new  Vector<Double>();
        hermite.GetCoefficients(coeffs);

        String NPolyMaxstring = String.valueOf(NPolyMax);

        System.out.println("Hermite coefficients f_{l,m,n} in the following order l = 0..."
              + NPolyMaxstring + ", m = 0..." + NPolyMaxstring+"-l, n = " + NPolyMaxstring + "-l-m :");

        String coeff_str=coeffs.toString();

        System.out.println(coeff_str);

        OEScalarGrid grid = new OEScalarGrid();

        hermite.CreateGrid(grid, gridspacing);

        if (!oegrid.OEWriteGrid(ofname, grid)){
            oechem.OEThrow.Fatal("Unable to write grid file");
        }

    }

    
}