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

import java.io.*;
import openeye.oechem.*;
import openeye.oebio.*;

// recursive function to print hex strings for all possible alt loc states
// alf     -- OEAltLocationFactory for molecule of interest
// locs    -- array containing one location iterator per group
// i       -- index of current group
// numGrps -- total number of alternate location groups
public class AltlocfactStates {

    static void EnumerateStates(OEAltLocationFactory alf,
            OEAltLocationIter locs[],
            int i,
            int numGrps) {
        if (i < numGrps) {
            locs[i].ToFirst();
            for (OEAltLocation loc : locs[i]) {
                alf.SetAlt(loc);
                EnumerateStates(alf, locs, i+1, numGrps);
            }
        }
        else {
            for (int j=0; j < numGrps; ++j) {
                System.out.print(locs[j].Target().GetLocationID());
                if ((j+1) < numGrps) { System.out.print(":");  }
                else                  { System.out.print("\t"); }
            }
            OEAltLocationState state = new OEAltLocationState();
            alf.GetState(state);
            String hex = state.ToHexString();
            System.out.println(hex);
        }
    }

    // list alternate location state information
    static void PrintStates(OEMolBase mol) {
        if (! oechem.OEHasResidues(mol))
            oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

        OEAltLocationFactory alf = new OEAltLocationFactory(mol);

        System.out.println(mol.GetTitle() + "\t"
                + alf.GetGroupCount() + " groups\t");
        long     tot = 1;
        float totexp = 0.0f;
        for (OEAltGroup grp : alf.GetGroups()) {
            tot    *= grp.GetLocationCount();
            totexp += Math.log10(grp.GetLocationCount());
        }
        if (totexp > 7.0) {
            System.out.println("over 10^" + totexp + " states");
            System.out.println("too many states to enumerate");
        }
        else {
            System.out.println(tot + " states");

            OEAltLocationIter locs[] = new OEAltLocationIter[alf.GetGroupCount()];
            int i = 0;
            for (OEAltGroup grp : alf.GetGroups()) {
                locs[i++] = grp.GetLocations();
            }
            EnumerateStates(alf, locs, 0, locs.length);
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("AltlocfactStates <mol-infile>");
        }
        oemolistream ims = new oemolistream();
        if(! ims.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        // need this flavor to read alt loc atoms
        ims.SetFlavor(OEFormat.PDB, OEIFlavor.PDB.ALTLOC);

        OEGraphMol mol = new OEGraphMol();
        while(oechem.OEReadMolecule(ims, mol)) {
            PrintStates(mol);
        }
        ims.close();
    }
}
