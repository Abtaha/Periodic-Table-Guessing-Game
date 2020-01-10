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
/****************************************************************************
 * This program concatenates molecules into one file.
 * It can be useful for generating ROCS queries or reattach ligands to an
 * protein structure
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class CatMols {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    public static void main(String argv[]) {
        URL fileURL = CatMols.class.getResource("CatMols.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "CatMols", argv);

            OEGraphMol omol = new OEGraphMol();
            for (String filename : itf.GetStringList("-i")) {
                oemolistream ifs = new oemolistream();
                if (ifs.open(filename)) {
                    OEGraphMol imol = new OEGraphMol();
                    while (oechem.OEReadMolecule(ifs, imol)) {
                        oechem.OEAddMols(omol, imol);
                    }
                }
                else {
                    oechem.OEThrow.Fatal("Unable to open " + filename + " for reading");
                }
                ifs.close();
            }
            oemolostream ofs = new oemolostream();
            if (!ofs.open(itf.GetString("-o"))) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") + " for writing");
            }
            oechem.OEWriteMolecule(ofs, omol);
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
