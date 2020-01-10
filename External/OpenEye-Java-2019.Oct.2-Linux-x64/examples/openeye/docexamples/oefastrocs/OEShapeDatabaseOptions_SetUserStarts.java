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

package openeye.docexamples.oefastrocs;

import openeye.oechem.*;
import openeye.oefastrocs.*;

public class OEShapeDatabaseOptions_SetUserStarts {
    public static void main(String argv[]) {
        OEShapeDatabaseOptions opts = new OEShapeDatabaseOptions();
        opts.SetInitialOrientation(OEFastROCSOrientation.UserInertialStarts);
        
        float [] c = {(float)1.45, (float)6.78, (float)-3.21};
        OEFloatArray startsCoords = new OEFloatArray(c);
        
        opts.SetUserStarts(startsCoords, 1);

        OEFloatArray coords = new OEFloatArray(opts.GetNumStarts() * 3);
        opts.GetUserStarts(coords); 

    }
}

