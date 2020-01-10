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
package openeye.docexamples.oegrid;

import openeye.oegrid.*;

public class ASCIIWriter {
    public static void main(String argv[]) {
        OEScalarGrid grid = new OEScalarGrid(2,2,2,0.0f,0.0f,0.0f,0.5f);
        grid.SetTitle("Simple Grid");

        System.out.printf("Title: %s\n", grid.GetTitle());

        System.out.printf("Mid: %12.6f %12.6f %12.6f\n", 
                grid.GetXMid(), grid.GetYMid(), grid.GetZMid());

        System.out.printf("Dim: %6d %6d %6d\n", 
                grid.GetXDim(), grid.GetYDim(), grid.GetZDim());

        System.out.printf("Spacing: %12.6f\n", grid.GetSpacing());

        System.out.println("Values:");
        for (int iz = 0; iz < grid.GetZDim(); iz++)
            for (int iy = 0; iy < grid.GetYDim(); iy++)
                for (int ix = 0; ix < grid.GetXDim(); ix++) {
                    System.out.printf("%-12.6e\n", grid.GetValue(ix,iy,iz));
                }
    }
}
//@ </SNIPPET>
