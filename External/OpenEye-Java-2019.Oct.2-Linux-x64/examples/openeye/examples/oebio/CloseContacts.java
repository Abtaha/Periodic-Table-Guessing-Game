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

import java.io.*;
import java.util.ArrayList;
import openeye.oechem.*;

public class CloseContacts {

    // delete atoms from the protein w/same coords as the ligand
    // as well as any waters
    static void DropLigandFromProtein(OEMolBase prot, OEMolBase lig) {

        double approximatelyTheSame = 0.05;
        OENearestNbrs nn = new OENearestNbrs(prot, approximatelyTheSame);

        // mark ligand atoms for deletion
        OEBitVector bv = new OEBitVector(prot.GetMaxAtomIdx());
        for (OENbrs nbrs : nn.GetNbrs(lig)) {
            OEResidue r1 = oechem.OEAtomGetResidue(nbrs.GetBgn());
            OEResidue r2 = oechem.OEAtomGetResidue(nbrs.GetEnd());
            if (r1.GetModelNumber() == r2.GetModelNumber()) {
                bv.SetBitOn(nbrs.GetBgn().GetIdx());
            }
        }
        // mark waters for deletion too
        for (OEAtomBase atom : prot.GetAtoms()) {
            OEResidue res = oechem.OEAtomGetResidue(atom);
            if (oechem.OEGetResidueIndex(res) == OEResidueIndex.HOH) {
                bv.SetBitOn(atom.GetIdx());
            }
        }
        OEAtomIdxSelected pred = new OEAtomIdxSelected(bv);
        for (OEAtomBase atom : prot.GetAtoms(pred)) {
            prot.DeleteAtom(atom);
        }
    }
    // atoms in the protein within maxgap Angstroms of the ligand
    static ArrayList<OENbrs>
    LigandProteinCloseContacts(OEMolBase prot, OEMolBase lig,
            double maxgap) {

        oechem.OESuppressHydrogens(prot);
        oechem.OESuppressHydrogens(lig);

        DropLigandFromProtein(prot, lig);

        OENearestNbrs nn = new OENearestNbrs(prot, maxgap);

        ArrayList<OENbrs> contacts = new ArrayList<OENbrs>();
        for (OENbrs nbrs : nn.GetNbrs(lig)) {
            contacts.add(nbrs);
        }
        return contacts;
    }
    // print atoms in the protein within maxgap Angstroms of the ligand
    static void PrintCloseContacts(OEMolBase prot, OEMolBase lig,
            double maxgap, String protName) {

        ArrayList<OENbrs> contacts = LigandProteinCloseContacts(prot, lig, maxgap);

        if (contacts.size() > 0) {
            System.out.println(protName+": "+prot.GetTitle()
                    +": "+contacts.size()+" contacts within "+Math.round(100.0*maxgap)/100.0+"A");
            for (OENbrs nbrs : contacts) {
                OEAtomBase pat = nbrs.GetBgn();
                OEAtomBase lat = nbrs.GetEnd();
                OEResidue rp = oechem.OEAtomGetResidue(pat);
                OEResidue rl = oechem.OEAtomGetResidue(lat);
                System.out.println(protName+":\t"+ 
                        Math.round(100.0*Math.sqrt(nbrs.GetDist2()))/100.0+"A\t:"+
                        rp.GetSerialNumber()+"\t"+pat.GetName()+rp.GetAlternateLocation()+" "+
                        rp.GetName()+" "+rp.GetChainID()+" "+rp.GetResidueNumber()+rp.GetInsertCode()+
                        ":\t"+
                        rl.GetSerialNumber()+"\t"+lat.GetName()+rl.GetAlternateLocation()+" "+
                        rl.GetName()+" "+rl.GetChainID()+" "+rl.GetResidueNumber()+rl.GetInsertCode());
            }
            System.out.println();
        }
    }
    public static void main(String argv[]) {
        if (argv.length != 3) {
            oechem.OEThrow.Usage("CloseContacts <prot-infile> <lig-infile> <max-distance>");
        }

        oemolistream ims = new oemolistream();
        if(! ims.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open protein " + argv[0] + " for reading");
        }
        OEGraphMol prot = new OEGraphMol();
        oechem.OEReadMolecule(ims, prot);
        if (! oechem.OEHasResidues(prot))
            oechem.OEPerceiveResidues(prot, OEPreserveResInfo.All);

        if(! ims.open(argv[1])) {
            oechem.OEThrow.Fatal("Unable to open lig " + argv[1] + " for reading");
        }
        OEGraphMol lig = new OEGraphMol();
        oechem.OEReadMolecule(ims, lig);
        if (! oechem.OEHasResidues(lig))
            oechem.OEPerceiveResidues(lig, OEPreserveResInfo.All);

        ims.close();

        double maxgap = Double.parseDouble(argv[2]);

        PrintCloseContacts(prot, lig, maxgap, argv[0]);
    }
}
