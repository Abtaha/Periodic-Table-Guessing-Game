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
package openeye.docexamples.oespicoli;

import openeye.oechem.*;
import openeye.oespicoli.*;

public class SurfaceDataAccess {
    public static void main(String argv[]) {
        OESurface surf = new OESurface();

        OEFloatArray coords = new OEFloatArray(surf.GetNumVertices()*3);
        surf.GetVertices(coords);

        OEFloatArray vert = new OEFloatArray(3);
        for(int i=0; i < surf.GetNumVertices(); ++i)
        {
            surf.GetVertex(i, vert);
        }

        OEUIntArray triangles = new OEUIntArray(surf.GetNumTriangles()*3);
        surf.GetTriangles(triangles);

        OEUIntArray tri = new OEUIntArray(3);
        for(int i=0; i < surf.GetNumTriangles(); ++i)
        {
            surf.GetTriangle(i, tri);
        }
    }
}
