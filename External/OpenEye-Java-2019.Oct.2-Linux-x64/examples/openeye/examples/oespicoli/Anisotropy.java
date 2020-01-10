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
package openeye.examples.oespicoli;

import openeye.oechem.*;
import openeye.oespicoli.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Anisotropy { 
    private static final int[] ANISOURecordColumns = {28, 35, 42, 49, 56, 63, 70};

    // Format of ANISOU record: http://www.ccp4.ac.uk/html/pdbformat.html#part1ani
    private static int parseFactors(final String line, ArrayList<Float> factors) {
        for(int i=1; i!=ANISOURecordColumns.length; i++) { 
            int prev = ANISOURecordColumns[i-1];
            int next = ANISOURecordColumns[i];
            final float val = Float.parseFloat(line.substring(prev, next).trim()) / 10000.0f;
            factors.add(val);
        }
        return Integer.parseInt(line.substring(6, 11).trim());
    }

    private static OESurface getEllipsoidalSurface(final OEFloatArray center, final ArrayList<Float> factors) {
        OEFloatArray dir1 = new OEFloatArray(3);
        dir1.setItem(0, factors.get(0));
        dir1.setItem(1, factors.get(3));
        dir1.setItem(2, factors.get(4));

        OEFloatArray dir2 = new OEFloatArray(3);
        dir2.setItem(0, factors.get(3));
        dir2.setItem(1, factors.get(1));
        dir2.setItem(2, factors.get(5));

        OEFloatArray dir3 = new OEFloatArray(3);
        dir3.setItem(0, factors.get(4));
        dir3.setItem(1, factors.get(5));
        dir3.setItem(2, factors.get(2));

        OESurface surf = new OESurface();
        oespicoli.OEMakeEllipsoidSurface(surf, center, 10.0f, 10.0f, 10.0f, dir1, dir2, dir3, 4);
        return surf;
    }

    public static void main(String... args) throws IOException {
        if (args.length != 2) { 
            System.err.println("usage: Anistropy <molfile.pdb> <out.srf>");
            System.exit(1);
        }

        String pdbFileName = args[0];
        String surfaceFileName = args[1];

        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream(pdbFileName);
        if (!oechem.OEReadMolecule(ifs, mol)) { 
            System.err.println("unable to read pdb file: " + pdbFileName);
            System.exit(1);
        }
        ifs.close();

        if (!oechem.OEHasResidues(mol)) {
          oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);
        }

        HashMap<Integer, OEAtomBase> serials = new HashMap<Integer, OEAtomBase>();

        for (OEAtomBase atom: mol.GetAtoms()) { 
            OEResidue res = oechem.OEAtomGetResidue(atom);
            Integer serialNumber = new Integer(res.GetSerialNumber());
            serials.put(serialNumber, atom);
        }

        OESurface outSurface = new OESurface();
        OEFloatArray center = new OEFloatArray(3);

        File pdb = new File(pdbFileName);
        FileReader freader = new FileReader(pdb);
        BufferedReader breader = new BufferedReader(freader);
        String line = new String();

        while((line = breader.readLine()) != null) { 
            if (!line.startsWith("ANISOU"))
                continue;
        
            ArrayList<Float> factors = new ArrayList<Float>();
            int serialNo = parseFactors(line, factors);
            if (serials.containsKey(new Integer(serialNo))) {
                mol.GetCoords(serials.get(serialNo), center);
                OESurface tmpSurf = getEllipsoidalSurface(center, factors);
                oespicoli.OEAddSurfaces(outSurface, tmpSurf);
            }
        }

        if (!oespicoli.OEWriteSurface(surfaceFileName, outSurface)) { 
            System.err.println("unable to write to " + surfaceFileName);
            System.exit(1);
        }

        System.exit(0);
    }
}
