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

public class CustomFASTA { 
    public static void main(String[] args)
    {
        oemolistream ifs = new oemolistream();

        int flavor = OEIFlavor.Generic.Default |
                     OEIFlavor.FASTA.CustomResidues |
                     OEIFlavor.FASTA.EmbeddedSMILES;

      ifs.SetFlavor(OEFormat.FASTA, flavor);
      ifs.SetFormat(OEFormat.FASTA);

      oechem.OEAddCustomFASTAResidue("dI", "CC[C@@H](C)[C@@H](C(=O)O)N");

      final String customFasta = ">Custom FASTA\nFVVVSTDPWVNGLY[dI]D[NC(=O)CNC(=O)[C@@H](N[R16])CSCC(=O)[R1]]";


      ifs.openstring(customFasta);

      OEGraphMol mol = new OEGraphMol();
      oechem.OEReadMolecule(ifs, mol); 
      ifs.close();
    }
}
//@ </SNIPPET>
