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
 * Using the OERMSD() function.
 * Performing RMSD calculation between a 3D reference molecule and
 * multi-conformation molecules
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;
import openeye.oebio.*;
import java.net.URL;
import java.io.IOException;

public class RMSD {
        public static void main(String[] args) {
            URL fileURL = RMSD.class.getResource("RMSD.txt");
            OEInterface itf  = null;
            try {
                itf = new OEInterface(fileURL, "RMSD", args);
            } catch (IOException e) {
                System.err.println("Unable to open interface file");
            }

            if (!itf.GetBool("-verbose")) {
                oechem.OEThrow.SetLevel(OEErrorLevel.Warning);
            }

            String rfname = itf.GetString("-ref");
            String ifname = itf.GetString("-in");

            boolean automorph = itf.GetBool("-automorph");
            boolean heavy = itf.GetBool("-heavyonly");
            boolean overlay = itf.GetBool("-overlay");

            oemolistream refifs = new oemolistream();
            if (!refifs.open(rfname))
                oechem.OEThrow.Fatal("Unable to open " + rfname + " for reading");

            OEGraphMol rmol = new OEGraphMol();
            if (!oechem.OEReadMolecule(refifs, rmol))
                oechem.OEThrow.Fatal("Unable to read reference molecule");
            refifs.close();

            oemolistream ifs = new oemolistream();
            if (!ifs.open(ifname))
                oechem.OEThrow.Fatal("Unable to open " + ifname + " for reading");

            oemolostream ofs = null;
            if (itf.HasString("-out")) {
                String ofname = itf.GetString("-out");
                ofs = new oemolostream();
                if (!ofs.open(ofname))
                    oechem.OEThrow.Fatal("Unable to open "+ ofname +" for writing");
                if (!overlay)
                    oechem.OEThrow.Warning("Output is the same as input when overlay is false");
            }

            for (OEMCMolBase mol : ifs.GetMCMolBases()) {
                oechem.OEThrow.Info(mol.GetTitle());

                int maxIdx = mol.GetMaxConfIdx();
                OEDoubleArray rmsds = new OEDoubleArray(maxIdx);
                OEDoubleArray rmtx = new OEDoubleArray(9*maxIdx);
                OEDoubleArray tmtx = new OEDoubleArray(3*maxIdx);

                // performing RMSD for all conformers
                oechem.OERMSD(rmol, mol, rmsds, automorph, heavy, overlay, rmtx, tmtx);

                for( OEConfBase conf : mol.GetConfs()) {
                    int cidx = conf.GetIdx();
                    oechem.OEThrow.Info("Conformer " + cidx + ": rmsd = " + rmsds.getItem(cidx));

                    if (itf.GetBool("-overlay")) {
                        oechem.OERotate(conf, RMSD.slice(rmtx, cidx*9, cidx*9+9));
                        oechem.OETranslate(conf, RMSD.slice(tmtx, cidx*3, cidx*3+3));
                    }
                }

                if (ofs != null) {
                    oechem.OEWriteMolecule(ofs, mol);
                }
            } // end for
            if (ofs != null) {
                ofs.close();
            }
        }

    private static OEDoubleArray slice(OEDoubleArray src, int begin, int end) {
        OEDoubleArray ret = new OEDoubleArray(end - begin);
        int cnt = 0;
        for(int i=begin; i!=end; i++,cnt++) {
            ret.setItem(cnt, src.getItem(i));
        }
        return ret;
    }
}
