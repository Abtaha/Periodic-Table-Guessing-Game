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
package openeye.docexamples.oedepict;

import openeye.oechem.*;
import openeye.oedepict.*;

public class DepictMDLQuery {
    public static void main(String argv[]){
        if (argv.length != 2) {
            oechem.OEThrow.Usage("DepictMDLQuery <mdlquery> <imagefile>");
        }
        oemolistream ifs = new oemolistream(argv[0]);
        OEGraphMol qmol = new OEGraphMol();
        oechem.OEReadMDLQueryFile(ifs, qmol);
        ifs.close();
        oedepict.OEPrepareDepiction(qmol);
        oedepict.OERenderMolecule(argv[1], qmol);
    }
}
