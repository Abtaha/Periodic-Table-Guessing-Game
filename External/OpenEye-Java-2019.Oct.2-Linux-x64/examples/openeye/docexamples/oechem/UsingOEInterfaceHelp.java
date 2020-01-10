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
//@ <SNIPPET>
package openeye.docexamples.oechem;

import openeye.oechem.*;

public class UsingOEInterfaceHelp {
    static String InterfaceData = 
            "!BRIEF UsingOEInterfaceHelp [-d <delimiter>] [-o] <output> [-i] <input1> <input2> ...\n" +
                    "!PARAMETER -delim\n" +
                    "  !ALIAS -d\n" +
                    "  !TYPE string\n" +
                    "  !DEFAULT _\n" +
                    "  !BRIEF Title delimiter\n" +
                    "  !DETAIL\n" +
                    "         This is the value given to the OEChem function OEAddMols to\n" +
                    "         separate the titles of the input molecules in the output.\n" +
                    "!END\n" +
                    "!PARAMETER -output\n" +
                    "  !ALIAS -o\n" +
                    "  !TYPE string\n" +
                    "  !REQUIRED true\n" +
                    "  !KEYLESS 1\n" +
                    "  !BRIEF The output file\n" +
                    "  !DETAIL\n" +
                    "         The molecule file to output to, can be any file format\n" +
                    "         OEChem supports.\n" +
                    "!END\n" +
                    "!PARAMETER -inputs\n" +
                    "  !ALIAS -i\n" +
                    "  !TYPE string\n" +
                    "  !REQUIRED true\n" +
                    "  !LIST true\n" +
                    "  !KEYLESS 2\n" +
                    "  !BRIEF The input files\n" +
                    "  !DETAIL\n" +
                    "         A list of molecule files to add together. Note, only the\n" +
                    "         first molecule from every file will be added.\n" +
                    "!END\n";

    public static void main(String argv[]) {
        OEInterface itf = new OEInterface(InterfaceData, "UsingOEInterfaceHelp", argv);

        OEGraphMol outmol = new OEGraphMol();
        for (String param : itf.GetStringList("-inputs")) {
            oemolistream ifs = new oemolistream(param);

            OEGraphMol inmol = new OEGraphMol();
            oechem.OEReadMolecule(ifs, inmol);

            oechem.OEAddMols(outmol, inmol, itf.GetString("-delim"));
            ifs.close();
        }

        oemolostream ofs = new oemolostream(itf.GetString("-output"));
        oechem.OEWriteMolecule(ofs, outmol);
        ofs.close();
    }
}
//@ </SNIPPET>
