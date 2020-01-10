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
 * Extract ring templates for 2D coordinate generation
 ****************************************************************************/
package openeye.examples.oechem;

import java.net.URL;
import java.util.*;
import openeye.oechem.*;

public class ExtractRingTemplates {

    public static void main(String argv[]) {
        URL fileURL = ExtractRingTemplates.class.getResource("ExtractRingTemplates.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "ExtractRingTemplates", argv);

            String ifname = itf.GetString("-in");
            String ofname = itf.GetString("-out");

            oemolistream ifs = new oemolistream();
            if (!ifs.open(ifname))
                oechem.OEThrow.Fatal("Unable to open " + ifname + " for reading");

            if (!oechem.OEIs2DFormat(ifs.GetFormat()))
                oechem.OEThrow.Fatal("Invalid input format: need 2D coordinates");

            oemolostream ofs = new oemolostream();
            if (!ofs.open(ofname))
                oechem.OEThrow.Fatal("Unable to open " + ofname + " for writing");

            if (!oechem.OEIs2DFormat(ofs.GetFormat()))
                oechem.OEThrow.Fatal("Invalid output format: unable to write 2D coordinates");

            int nrrings = 0;
            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                for (OEMolBase ring : oechem.OEExtractRingTemplates(mol)) {
                    nrrings++; 
                    oechem.OEWriteMolecule(ofs, ring);
                }
            }  
            oechem.OEThrow.Info(nrrings + " number of ring templates extracted");
            ifs.close();
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
