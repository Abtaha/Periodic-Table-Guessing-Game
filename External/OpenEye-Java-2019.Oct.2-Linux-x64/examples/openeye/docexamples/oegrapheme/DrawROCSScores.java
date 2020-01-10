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
package openeye.docexamples.oegrapheme;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;
import java.util.*;

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE GRAPHEME DOCUMENTATION
 **************************************************************/

public class DrawROCSScores {
    public static void main(String argv[]) {
        OEImage image = new OEImage(200, 150);
        ArrayList<Double>scores = new ArrayList<Double>();
        scores.add(0.75);
        scores.add(0.25);
        scores.add(0.50);
        oegrapheme.OEDrawROCSScores(image, scores);
        oedepict.OEWriteImage("DrawROCSScores.png", image);
    }
}
