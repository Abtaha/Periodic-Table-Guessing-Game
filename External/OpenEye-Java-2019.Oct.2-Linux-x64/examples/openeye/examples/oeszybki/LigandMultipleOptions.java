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
package openeye.examples.oeszybki;

import openeye.oechem.*;
import openeye.oeszybki.*;

public class LigandMultipleOptions
{
    public static void main(String argv[])
    {
        OEInterface itf = new OEInterface();
        if (!SetupInterface("LigandMultipleOptions", itf, argv))
            return;

        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-in")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-in") + " for reading");

        oemolostream ofs = new oemolostream();
        if (!ofs.open(itf.GetString("-out")))
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-out") + " for writing");

        oeostream logfile = oechem.oeout;
        if (itf.HasString("-log")) {
            if (!logfile.open(itf.GetString("-log")))
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-log") + " for writing");
        }

        // Szybki options
        OESzybkiOptions opts = new OESzybkiOptions();

        // select run type
        if (itf.GetBool("-t"))
            opts.SetRunType(OERunType.TorsionsOpt);

        if (itf.GetBool("-n"))
            opts.SetRunType(OERunType.SinglePoint);

        // apply solvent model
        if (itf.GetBool("-s"))
            opts.GetSolventOptions().SetSolventModel(OESolventModel.Sheffield);

        // remove attractive VdW forces
        if (itf.GetBool("-a"))
            opts.GetGeneralOptions().SetRemoveAttractiveVdWForces(true);

        // Szybki object;
        OESzybki sz = new OESzybki(opts);

        // fix atoms
        if (itf.HasString("-f"))
            if (!sz.FixAtoms(itf.GetString("-f")))
                oechem.OEThrow.Warning("Failed to fix atoms for " + itf.GetString("-f"));

        // process molecules
        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            logfile.write("\nMolecule " + mol.GetTitle() + "\n");
            boolean no_res = true;
            for (OESzybkiResults results : sz.call(mol)) {
                results.Print(logfile);
                no_res = false;
            }

            if (no_res) {
                oechem.OEThrow.Warning("No results processing molecule: " + mol.GetTitle());
                continue;
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
    }

    private static boolean SetupInterface(String progName, OEInterface itf, String argv[])
    {
        oechem.OEConfigure(itf, interfaceData);
        if (oechem.OECheckHelp(itf, argv, progName, false)) {
            return false;
        }
        if (!oechem.OEParseCommandLine(itf, argv, progName)) {
            return false;
        }
        if (!oechem.OEIsReadable(oechem.OEGetFileType(oechem.OEGetFileExtension(itf.GetString("-in"))))) {
            oechem.OEThrow.Warning(itf.GetString("-in") + " is not a readable input file");
            return false;
        }
        if (!oechem.OEIsWriteable(oechem.OEGetFileType(oechem.OEGetFileExtension(itf.GetString("-out"))))) {
            oechem.OEThrow.Warning(itf.GetString("-out") + " is not a writable output file");
            return false;
        }
        return true;
    }

    private static String interfaceData = 
"!PARAMETER -in\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Input molecule file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -out\n" +
"!TYPE string\n" +
"!REQUIRED true\n" +
"!BRIEF Output molecule file name.\n" +
"!END\n" +
"\n" +
"!PARAMETER -log\n" +
"!TYPE string\n" +
"!REQUIRED false\n" +
"!BRIEF Log file name. Defaults to standard out.\n" +
"!END\n" +
"\n" +
"!PARAMETER -s\n" +
"!TYPE bool\n" +
"!DEFAULT false\n" +
"!REQUIRED false\n" +
"!BRIEF Optimization in solution.\n" +
"!END\n" +
"\n" +
"!PARAMETER -t\n" +
"!TYPE bool\n" +
"!DEFAULT false\n" +
"!REQUIRED false\n" +
"!BRIEF Optimization of torsions.\n" +
"!END\n" +
"\n" +
"!PARAMETER -a\n" +
"!TYPE bool\n" +
"!DEFAULT false\n" +
"!REQUIRED false\n" +
"!BRIEF No attractive VdW forces.\n" +
"!END\n" +
"\n" +
"!PARAMETER -n\n" +
"!TYPE bool\n" +
"!DEFAULT false\n" +
"!REQUIRED false\n" +
"!BRIEF Single point calculation.\n" +
"!END\n" +
"\n" +
"!PARAMETER -f\n" +
"!TYPE string\n" +
"!REQUIRED false\n" +
"!BRIEF SMARTS pattern of fixed atoms.\n" +
"!END";
}
