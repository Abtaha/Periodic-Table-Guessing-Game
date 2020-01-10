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
 * Print toolkit release date and build information.
 *****************************************************************************/
package openeye.examples.oemedchem;

import openeye.oemedchem.*;

public class OEMedChemInfo  {
    public static void main(String args[]) {
        System.out.printf("OEMedChem version: %s built: %d arch: %s platform: %s%n", 
                          oemedchem.OEMedChemGetRelease(),
                          oemedchem.OEMedChemGetVersion(),
                          oemedchem.OEMedChemGetArch(),
                          oemedchem.OEMedChemGetPlatform());
    }
}
