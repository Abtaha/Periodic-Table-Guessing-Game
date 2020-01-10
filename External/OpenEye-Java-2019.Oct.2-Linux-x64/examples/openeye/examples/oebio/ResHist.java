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
package openeye.examples.oebio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import openeye.oechem.*;
import openeye.oebio.*;

public class ResHist {

    private void displayUsage() {
        System.err.println("usage: ResHist <inmol>");
        System.exit(1);
    } 
    public void showResHist(String infile) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(infile)) {
            System.err.println("Unable to open infile: " + infile);
            return;
        }

        int molCt=0;
        OEGraphMol mol = new OEGraphMol();
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        while (oechem.OEReadMolecule(ifs, mol)) {
            ++molCt;
            map.clear();
            System.out.println("======================================");
            System.out.println("Molecule: "+molCt+" ( "+mol.GetTitle()+" )");
            if (! oechem.OEHasResidues(mol))
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

            OEHierView hv = new OEHierView(mol);
            OEHierResidueIter resiter = hv.GetResidues();
            int resCt=0;
            while (resiter.hasNext()) {
                OEHierResidue res = resiter.next();
                ++resCt;
                String name = res.GetOEResidue().GetName();
                if (map.containsKey(name)) {
                    Integer i = map.get(name);
                    map.put(name, i.intValue()+1);
                }
                else {
                    map.put(name, 1);
                }
            }

            for(String name: new TreeSet<String>(map.keySet())) {
                int count = ((Integer)map.get(name)).intValue();
                double percent = (100.0*count/resCt);
                System.out.printf("%3s %3d %4.1f %%\n", name, map.get(name), percent);
            }

            System.out.println();
        }
        ifs.close();
    }
    public static void main(String argv[]) {
        ResHist app = new ResHist();
        if (argv.length != 1)
            app.displayUsage();
        app.showResHist(argv[0]);
    }
}
