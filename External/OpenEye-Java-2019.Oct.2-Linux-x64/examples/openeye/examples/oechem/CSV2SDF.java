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
 * Merge a CSV file of data/properties, key on compound name in first column
 * and use column titles as keys.  All data is read/written as strings
 ****************************************************************************/
package openeye.examples.oechem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import openeye.oechem.*;

public class CSV2SDF {

    public static void csv2SDF(BufferedReader csv,
            oemolistream ifs,
            oemolostream ofs) {
        try {
            String line = csv.readLine();
            String[] propNames = line.split(",");
            List<String> propNamesList = new LinkedList<String>(Arrays.asList(propNames));
            propNamesList.remove(0);
            Map<String, List<String>> values = new HashMap<String, List<String>>();
            while ( (line = csv.readLine()) != null ) {
                String[] tokens = line.split(",");
                List<String> tokenList = new LinkedList<String>(Arrays.asList(tokens));
                if (tokenList.size() > 1) {
                    String title = tokenList.get(0);
                    if (title == null || title.equals("")) {
                        oechem.OEThrow.Warning("Skipping entry with no title");
                        continue;
                    }
                    tokenList.remove(0);
                    values.put(title, tokenList);
                }
            }

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                if (values.containsKey(mol.GetTitle())) {
                    List<String> valList = values.get(mol.GetTitle());
                    for (int p = 0; p < valList.size() && p < propNamesList.size(); ++p) {
                        String sdVal = valList.get(p);
                        if (sdVal == null || sdVal.equals("")) {
                            continue;
                        }
                        else {
                            oechem.OESetSDData(mol, propNamesList.get(p), sdVal);
                        }
                    }
                }
                oechem.OEWriteMolecule(ofs, mol);
            }
        }
        catch(java.io.IOException e) {
            oechem.OEThrow.Fatal("Failed to read from csv file");
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 3) {
            oechem.OEThrow.Usage("CSV2SDF <csvfile> <infile> <outsdfile>");
        }

        try {
            BufferedReader csv = new BufferedReader(new FileReader(argv[0]));

            oemolistream ifs = new oemolistream();
            if (!ifs.open(argv[1])) {
                oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for reading");
            }

            oemolostream ofs = new oemolostream();
            if (!ofs.open(argv[2])) {
                oechem.OEThrow.Fatal("Unable to open " + argv[2] + " for writing");
            }
            int fmt = ofs.GetFormat();
            if (fmt != OEFormat.SDF && fmt != OEFormat.OEB) {
                oechem.OEThrow.Fatal("Only works for sdf or oeb output files");
            }

            csv2SDF(csv, ifs, ofs);
            ifs.close();
            ofs.close();
        }
        catch(java.io.IOException e) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
    }
}
