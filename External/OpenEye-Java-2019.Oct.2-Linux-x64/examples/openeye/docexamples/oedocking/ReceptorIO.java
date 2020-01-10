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
package openeye.docexamples.oedocking;

import openeye.oechem.*;
import openeye.oedocking.*;

public class ReceptorIO {
    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("ReceptorIO <input receptor> <output receptor>");
        }

        OEGraphMol receptor = new OEGraphMol();
        String inputReceptorFilename = argv[0];
        oedocking.OEReadReceptorFile(receptor, inputReceptorFilename);

        String outputReceptorFilename = argv[1];
        oedocking.OEWriteReceptorFile(receptor,outputReceptorFilename);

        {
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(".oeb", receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, ".oeb", bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-3: .oeb      status=" + status + " nbytes=" + bytes.length);
        }

        {
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(".oeb.gz", receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, ".oeb.gz", bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-4: .oeb.gz   status=" + status + " nbytes=" + bytes.length);
        }

        {
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(".json", receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, ".json", bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-5: .json     status=" + status + " nbytes=" + bytes.length);
        }

        {
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(".json.gz", receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, ".json.gz", bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-6: .json.gz  status=" + status + " nbytes=" + bytes.length);
        }

        {
            boolean gzip = false;
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(OEFormat.OEB, oechem.OEGetDefaultOFlavor(OEFormat.OEB), gzip, receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, OEFormat.OEB, oechem.OEGetDefaultIFlavor(OEFormat.OEB), gzip, bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-7: .oeb      status=" + status + " nbytes=" + bytes.length);
        }

        {
            boolean gzip = true;
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(OEFormat.OEB, oechem.OEGetDefaultOFlavor(OEFormat.OEB), gzip, receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, OEFormat.OEB, oechem.OEGetDefaultIFlavor(OEFormat.OEB), gzip, bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-8: .oeb.gz   status=" + status + " nbytes=" + bytes.length);
        }

        {
            boolean gzip = false;
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(OEFormat.JSON, oechem.OEGetDefaultOFlavor(OEFormat.JSON), gzip, receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, OEFormat.JSON, oechem.OEGetDefaultIFlavor(OEFormat.JSON), gzip, bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-9: .json     status=" + status + " nbytes=" + bytes.length);
        }

        {
            boolean gzip = true;
            byte[] bytes = oedocking.OEWriteReceptorToByteArray(OEFormat.JSON, oechem.OEGetDefaultOFlavor(OEFormat.JSON), gzip, receptor);
            OEMolBase receptorIO = new OEGraphMol();
            boolean status = oedocking.OEReadReceptorFromByteArray(receptorIO, OEFormat.JSON, oechem.OEGetDefaultIFlavor(OEFormat.JSON), gzip, bytes);
            System.out.println("SNIPPET-RECEPTOR-IO-10: .json.gz status=" + status + " nbytes=" + bytes.length);
        }

    }
}
