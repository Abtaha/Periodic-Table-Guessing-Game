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

public class UsingOEInterfaceHas {
    static String InterfaceData = 
            "!PARAMETER -b\n" +
                    "  !TYPE bool\n" +
                    "  !BRIEF An boolean parameter\n" +
                    "!END\n" +
                    "!PARAMETER -i\n" +
                    "  !TYPE int\n" +
                    "  !BRIEF An integer parameter\n" +
                    "!END\n" +
                    "!PARAMETER -f\n" +
                    "  !TYPE float\n" +
                    "  !BRIEF A float parameter\n" +
                    "!END\n" +
                    "!PARAMETER -str\n" +
                    "  !TYPE string\n" +
                    "  !BRIEF A string parameter\n" +
                    "!END\n";

    public static void main(String argv[]) {
        OEInterface itf = new OEInterface(InterfaceData, "UsingOEInterfaceHas", argv);

        if (itf.HasBool("-b"))
            System.out.println("-b = " + itf.GetBool("-b"));
        if (itf.HasInt("-i"))
            System.out.println("-i = " + itf.GetInt("-i"));
        if (itf.HasFloat("-f"))
            System.out.println("-f = " + itf.GetFloat("-f"));
        if (itf.HasString("-str"))
            System.out.println("-str = " + itf.GetString("-str"));
    }
}
//@ </SNIPPET>
