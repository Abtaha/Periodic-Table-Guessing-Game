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
 * Print toolkit release date platform and build information. Also print out
 * all formats supported by OEChem and whether they are readable or writeable
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class OEChemInfo {

    public static void PrintFormats() {
        System.out.println("\tcode| ext           | description                       |read? |write?");
        System.out.println("\t----+---------------+-----------------------------------+------+------");
        for (int code = 1; code < OEFormat.MAXFORMAT; ++code) {
            String extension = oechem.OEGetFormatExtension(code);
            String description = oechem.OEGetFormatString(code);
            String readable = oechem.OEIsReadable(code) ? "yes" : "no";
            String writeable = oechem.OEIsWriteable(code) ? "yes" : "no";
            System.out.printf("\t %2d | %-13s | %-33s | %-4s | %-4s\n",
                    code, extension, description, readable, writeable);
        }
        System.out.println("\t----+---------------+-----------------------------------+------+------");
    }

    public static void main(String argv[]) {
        System.out.println("Installed OEChem version: " +
                oechem.OEChemGetRelease() +
                " platform: " +
                oechem.OEChemGetPlatform() +
                " built: " +
                oechem.OEChemGetVersion() +
                " release name: " +
                oechem.OEToolkitsGetRelease());
        PrintFormats();
    }
}
