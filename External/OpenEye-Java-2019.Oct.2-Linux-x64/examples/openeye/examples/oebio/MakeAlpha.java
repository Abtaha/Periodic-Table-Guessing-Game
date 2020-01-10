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
package openeye.examples.oebio;

import openeye.oechem.*;
import openeye.oebio.*;

public class MakeAlpha {

    private void displayUsage() {
        System.err.println("usage: MakeAlpha <inmol> <outmol>");
        System.exit(1);
    }

    public void makeAlpha(String infile, String outfile) {
        oemolistream ifs = new oemolistream();
        if (!ifs.open(infile)) {
            System.err.println("Unable to open infile: " + infile);
            return;
        }
        oemolostream ofs = new oemolostream();
        if (!ofs.open(outfile)) {
            System.err.println("Unable to open outfile: " + outfile);
            return;
        }

        double phi = oechem.getPi() / -3.0;
        double psi = oechem.getPi() / -3.0;
        double chi = oechem.getPi();

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            if (! oechem.OEHasResidues(mol))
                oechem.OEPerceiveResidues(mol, OEPreserveResInfo.All);

            // remove cross-links
            for (OEBondBaseIter biter = mol.GetBonds(); biter.hasNext();) {
                OEBondBase bond = biter.next();
                if (bond.GetBgn().GetAtomicNum() == OEElemNo.S
                        && bond.GetEnd().GetAtomicNum() == OEElemNo.S) {
                    mol.DeleteBond(bond);
                }
            }
            oechem.OEFindRingAtomsAndBonds(mol);

            OEHierView hv = new OEHierView(mol);
            for (OEHierResidue res : hv.GetResidues()) {
                if (!oebio.OEIsStandardProteinResidue(res))
                    continue;

                // set psi and phi angles
                if (!oebio.OESetTorsion(res, OEProtTorType.Phi, phi))
                    System.err.println("Unable to set phi for "
                            + res.GetOEResidue().GetName() + res.GetOEResidue().GetResidueNumber());
                if (!oebio.OESetTorsion(res, OEProtTorType.Psi, psi))
                    System.err.println("Unable to set psi for "
                            + res.GetOEResidue().GetName() + res.GetOEResidue().GetResidueNumber());

                // set chis
                for (OEUnsignedIter chiiter = oebio.OEGetChis(res); chiiter.hasNext();) {
                    int chiInt = chiiter.next();
                    if (!oebio.OESetTorsion(res, chiInt, chi))
                        System.err.println("Unable to set "
                                + oebio.OEGetProteinTorsionName(chiInt) + " for "
                                + res.GetOEResidue().GetName() + res.GetResidueNumber());
                }
            }
            oechem.OEWriteMolecule(ofs, mol);
        }
        ifs.close();
        ofs.close();
    }

    public static void main(String argv[]) {
        MakeAlpha app = new MakeAlpha();
        if (argv.length != 2)
            app.displayUsage();
        app.makeAlpha(argv[0], argv[1]);
    }
}
