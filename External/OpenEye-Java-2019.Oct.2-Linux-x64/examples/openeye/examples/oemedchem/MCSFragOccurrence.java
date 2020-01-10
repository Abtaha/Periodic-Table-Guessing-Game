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
/*****************************************************************************
 * report common fragments from an input set of molecules
 *****************************************************************************/
package openeye.examples.oemedchem;

import java.util.*;
import openeye.oechem.*;
import openeye.oemedchem.*;

public class MCSFragOccurrence {

    private static class FragOcc implements Comparable<FragOcc> {
        private int occ;
        private String smi;

        public FragOcc() {
            this.occ = 0;
            this.smi = "";
        }
        public FragOcc(int occ, String smi) {
            this.occ = occ;
            this.smi = smi;
        }
        public FragOcc(FragOcc frag) {
            this.occ = frag.occ;
            this.smi = frag.smi;
        }

        public int getOcc() { return occ; }
        public String getSMI() { return smi; }

        public int compareTo(FragOcc otherfrag) {
            return this.occ - otherfrag.occ;
        }
    }

    private static void MCSFragOcc(OEInterface itf) {

        boolean verbose = itf.GetBool("-verbose");

        String indb = itf.GetString("-indb");
        if (!oemedchem.OEIsMCSFragDatabaseFileType(indb)) {
            oechem.OEThrow.Fatal("Option -indb must specify a .mcsfrag filename: "
                                 + indb);
        }
        if (verbose) {
            oechem.OEThrow.Info("Loading index from " + indb);
        }

        OEMCSFragDatabase mcsdb = new OEMCSFragDatabase();
        if (!oemedchem.OEReadMCSFragDatabase(indb, mcsdb)) {
            oechem.OEThrow.Fatal("Error deserializing MCS fragment database: "
                                 + indb);
        }

        if (mcsdb.NumMols() == 0) {
            oechem.OEThrow.Fatal("Loaded empty index");
        }

        if (mcsdb.NumFragments() == 0) {
            oechem.OEThrow.Fatal("No fragments loaded from index, " +
                                 "use -fragGe,-fragLe options to " +
                                 "extend indexable range");
        }

        oechem.OEThrow.Info("Loaded index of " + mcsdb.NumMols() + " molecules, " + mcsdb.NumFragments() + " fragments");

        oemolistream ifsquery = new oemolistream();
        if (!ifsquery.open(itf.GetString("-query"))) {
            oechem.OEThrow.Fatal("Unable to open query file: " + itf.GetString("-query"));
        }

        // process the query options
        int qmaxrec = 0;
        if (itf.HasInt("-qlim"))
            qmaxrec = itf.GetInt("-qlim");

        // process the queries
        Set<String> uniqueCores = new HashSet<String>();

        int record = 0;
        OEGraphMol qmol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifsquery, qmol)) {
            for (String core : mcsdb.MoleculeToCores(qmol)) {
                // collect unique cores with >0 counts in the index
                if (mcsdb.CoreToMoleculeCount(core) == 0) {
                    continue;
                }
                uniqueCores.add(core);
            }
            ++record;

            if (qmaxrec > 0 && record >= qmaxrec) {
                break;
            }
        }

        ArrayList<FragOcc> coreOcc = new ArrayList<FragOcc>();
        for (String core : uniqueCores) {
            FragOcc frag = new FragOcc(mcsdb.CoreToMoleculeCount(core), core);
            coreOcc.add(frag);
        }

        oechem.OEThrow.Info("Common fragments with occurrence >1:");

        // sort greater over lesser
        Collections.sort(coreOcc, Collections.reverseOrder());

        int num = 0;
        int topnum = itf.GetInt("-top");
        for (FragOcc frag : coreOcc) {

            if (frag.getOcc() <= 1) {
                break;
            }
            ++num ;

            String listids = new String();
            for (Integer coreid : mcsdb.CoreToMolecules(frag.getSMI())) {
                if (listids.isEmpty())
                    listids = "[" + coreid;
                else
                    listids = listids + ", " + coreid;
            }
            listids = listids + "]";
            oechem.OEThrow.Info(frag.getOcc() + " " + frag.getSMI() + " " + listids);

            if (topnum > 0 && num >= topnum) {
                break;
            }
        }

        if (itf.GetBool("-uncommon")) {
            oechem.OEThrow.Info("Uncommon fragments:");

            // sort lesser over greater
            Collections.sort(coreOcc);

            for (FragOcc frag : coreOcc) {

                if (frag.getOcc() > 1) {
                    break;
                }

                String listids = new String();
                for (Integer coreid : mcsdb.CoreToMolecules(frag.getSMI())) {
                    if (listids.isEmpty())
                        listids = "[" + coreid;
                    else
                        listids = listids + ", " + coreid;
                }
                listids = listids + "]";
                oechem.OEThrow.Info(frag.getOcc() + " " + frag.getSMI() + " " + listids);
            }
        }
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);

        if (oechem.OEParseCommandLine (itf, args, "MCSFragOccurrence")) {
            MCSFragOcc(itf);
        }
    }

    private static String interfaceData =
"#MCSFragOccurrence interface file\n" +
"!CATEGORY MCSFragOccurrence\n" +
"    !CATEGORY I/O\n" +
"        !PARAMETER -indb 1\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of index file to load\n" +
"          !KEYLESS 1\n" +
"        !END\n" +
"        !PARAMETER -query 2\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF query structure file to report cores against\n" +
"          !KEYLESS 2\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY options\n" +
"        !PARAMETER -verbose 1\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate verbose output\n" +
"        !END\n" +
"        !PARAMETER -top 2\n" +
"           !TYPE int\n" +
"           !DEFAULT 10\n" +
"           !LEGAL_RANGE 0 inf\n" +
"           !BRIEF print the -top number of common fragment occurrences (0:all, 10:default)\n" +
"        !END\n" +
"        !PARAMETER -uncommon 3\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF print the uncommon fragments also\n" +
"        !END\n" +
"        !PARAMETER -qlim 4\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !BRIEF limit query processing to -qlim records from the -query structures\n" +
"        !END\n" +
"    !END\n" +
"!END\n";
}
