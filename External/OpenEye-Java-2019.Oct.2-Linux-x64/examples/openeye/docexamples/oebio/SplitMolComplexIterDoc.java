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
package openeye.docexamples.oebio;

import java.io.IOException;
import java.net.URL;

import openeye.oebio.*;
import openeye.oechem.*;

public class SplitMolComplexIterDoc {

    private static void SplitMolComplexIterOneSite(oemolostream oms,
                                                   OEMolBase mol) {
        // limiting lig, prot and wat to just near the first binding site
        // (a very common pattern)

        // for input molecule mol ...
        for (OEMolBase frag : oebio.OEGetMolComplexComponents(mol)) {
            // ...
            oechem.OEWriteMolecule(oms, frag);
        }

        // other api points illustrated below

        OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
        opt.SetSplitCovalent();

        int site = 1;
        boolean surfaceWaters = true;
        opt.ResetFilters(site, surfaceWaters);
        opt.SetMaxSurfaceWaterDist(8.0);
    }

    private static void SplitMolComplexIterLigName(oemolostream oms,
                                                   OEMolBase mol,
                                                   String ligName) {
        // the ligand has been identified by name

        // for input molecule mol and string ligName ...
        OESplitMolComplexOptions opt = new OESplitMolComplexOptions(ligName);

        for (OEMolBase frag : oebio.OEGetMolComplexComponents(mol, opt)) {
            // ...
            oechem.OEWriteMolecule(oms, frag);
        }
    }

    private static void SplitMolComplexIterAllSites(oemolostream oms,
                                                    OEMolBase mol) {
        OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
        int allSites = 0;
        opt.ResetFilters(allSites);

        for (OEMolBase frag : oebio.OEGetMolComplexComponents(mol, opt)) {
            //...
            oechem.OEWriteMolecule(oms, frag);
        }
    }

    private static void SplitMolComplexIterAllSitesLigName(oemolostream oms,
                                                           OEMolBase mol,
                                                           String ligName) {
        OESplitMolComplexOptions opt = new OESplitMolComplexOptions(ligName);
        int allSites = 0;
        opt.ResetFilters(allSites);

        for (OEMolBase frag : oebio.OEGetMolComplexComponents(mol, opt)) {
            //...
            oechem.OEWriteMolecule(oms, frag);
        }
    }

    private static void SplitMolComplexIterFilter(oemolostream oms,
                                                  OEMolBase mol) {
        OEResidueCategoryData db = new OEResidueCategoryData();
        db.AddToDB(OEResidueDatabaseCategory.Cofactor, "MTQ");

        OEMolComplexCategorizer cat = new OEMolComplexCategorizer();
        cat.SetResidueCategoryData(db);

        OESplitMolComplexOptions opt = new OESplitMolComplexOptions();
        opt.SetCategorizer(cat);

        // for input molecule mol and options opt ...
        for (OEMolBase l :
                      oebio.OEGetMolComplexComponents(mol,
                                                      opt,
                                                      opt.GetLigandFilter())) {
            // ...
            oechem.OEWriteMolecule(oms, l);
        }
        OEUnaryRoleSetPred ofilter = opt.GetOtherFilter();
        OERoles rs = new OERoles();
        ofilter.constCall(rs);
   }

    public static void main(String[] argv) {
        URL fileURL = SplitMolComplexIterDoc.class.getResource("SplitMolComplexIterDoc.txt");
        OEInterface itf = null;
        try {
            itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
        } catch (IOException e) {
            oechem.OEThrow.Fatal("Unable to open interface file");
        }

        if (!oechem.OEParseCommandLine(itf, argv, "SplitMolComplexIterDoc"))
            oechem.OEThrow.Fatal("Unable to interpret command line!");

        String iname = itf.GetString("-i");
        String oname = itf.GetString("-o");

        oemolistream ims = new oemolistream();
        if (!ims.open(iname))
            oechem.OEThrow.Fatal("Cannot open input file!");

        oemolostream oms = new oemolostream();
        if (!oms.open(oname))
            oechem.OEThrow.Fatal("Cannot open output file!");

        OEGraphMol inmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ims, inmol))
            oechem.OEThrow.Fatal("Unable to read molecule from " + iname);
        ims.close();

        boolean allSites     = itf.GetBool("-a");
        boolean filterLigand = itf.GetBool("-f");

        // break out each scheme to better illustrate them
        if (filterLigand)
            SplitMolComplexIterFilter(oms, inmol);
        else if (itf.HasString("-l")) {
            String ligName = itf.GetString("-l");
            if (allSites)
                SplitMolComplexIterAllSitesLigName(oms, inmol, ligName);
            else
                SplitMolComplexIterLigName(oms, inmol, ligName);
        }
        else {
            if (allSites)
                SplitMolComplexIterAllSites(oms, inmol);
            else
                SplitMolComplexIterOneSite(oms, inmol);
        }

        oms.close();
    }
}
//@ </SNIPPET>
