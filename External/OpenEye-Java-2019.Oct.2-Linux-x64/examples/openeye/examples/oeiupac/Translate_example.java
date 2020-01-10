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
 Translates between languages.  Internally LexichemTK uses American
 English so it will convert to/from that as an intermediate
 representation.

 By default the program inputs/outputs the internal LexichemTK
 character set representation.  Optionally one can convert the
 input or output to alternate encodings, eg: HTML or UTF8.

 ****************************************************************************/

package openeye.examples.oeiupac;

import java.net.URL;
import java.io.PrintStream;

import openeye.oechem.*;
import openeye.oeiupac.*;

public class Translate_example {
    static {
        oechem.OEThrow.SetStrict(true);
    }

    private void Translate(OEInterface itf) {
        oeifstream ifs = new oeifstream();
        if (!ifs.open(itf.GetString("-in")))
            oechem.OEThrow.Fatal("Unable to open input file: "+itf.GetString("-in"));

        PrintStream out = System.out;
        if (itf.HasString("-o")) {
            try {
                out = new PrintStream(itf.GetString("-o"));
            }
            catch(java.io.IOException e) {
                oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-o") +
                                      " for writing");
            }
        }

        int from_language =
              oeiupac.OEGetIUPACLanguage(itf.GetString("-from_language"));
        int to_language =
              oeiupac.OEGetIUPACLanguage(itf.GetString("-to_language"));

        int from_charset =
              oeiupac.OEGetIUPACCharSet(itf.GetString("-from_charset"));
        int to_charset =
              oeiupac.OEGetIUPACCharSet(itf.GetString("-to_charset"));

        StringBuffer nbuf = new StringBuffer();
        while (ifs.getline(nbuf)) {
            String name = nbuf.toString();

            // Convert from charset to internal representation
            if (from_charset == OECharSet.UTF8)
                name = oeiupac.OEFromUTF8(name);
            else if (from_charset == OECharSet.HTML)
                name = oeiupac.OEFromHTML(name);

            // Translation functions operate on lowercase names
            name = oeiupac.OELowerCaseName(name);

            if (from_language != OELanguage.AMERICAN)
                name = oeiupac.OEFromLanguage(name, from_language);

            // At this point the name is American English in the
            // LexichemTK default internal character set representation.

            // Convert to output language
            if (to_language != OELanguage.AMERICAN)
                name = oeiupac.OEToLanguage(name, to_language);

            // Convert to output charset
            if (to_charset == OECharSet.ASCII)
                name = oeiupac.OEToASCII(name);
            else if (to_charset == OECharSet.UTF8)
                name = oeiupac.OEToUTF8(name);
            else if (to_charset == OECharSet.HTML)
                name = oeiupac.OEToHTML(name);
            else if (to_charset == OECharSet.SJIS)
                name = oeiupac.OEToSJIS(name);
            else if (to_charset == OECharSet.EUCJP)
                name = oeiupac.OEToEUCJP(name);

            out.println(name);
        }
        ifs.close();
    }

    public static void main(String argv[]) {
        Translate_example app = new Translate_example();
        URL fileURL = app.getClass().getResource("Translate_example.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "Translate_example",
                                              argv);
            app.Translate(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
