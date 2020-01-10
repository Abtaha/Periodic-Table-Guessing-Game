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

import java.util.concurrent.*;
import openeye.oechem.*;

public class PipelineMolFind {
    static OESubSearch ss;
    static oemolithread ifs;
    static oemolothread ofs;

    static BlockingQueue<OEGraphMol> iqueue;
    static BlockingQueue<OEGraphMol> oqueue;

    public static class ParseThread extends Thread {
        public void run() {
            try {
                OEGraphMol mol = new OEGraphMol();
                while (oechem.OEReadMolecule(ifs, mol)) {
                    iqueue.put(mol);
                    mol = new OEGraphMol();
                }
                mol.Clear();
                // signal SearchThread to die with an empty molecule
                iqueue.put(mol);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    public static class SearchThread extends Thread {
        public void run() {
            try {
                OEGraphMol mol = iqueue.take();
                while (mol.IsValid()) {
                    oechem.OEPrepareSearch(mol, ss);
                    if (ss.SingleMatch(mol))
                        oqueue.put(mol);

                    mol = iqueue.take();
                }
                mol = new OEGraphMol();
                // signal main thread to die with an empty molecule
                oqueue.put(mol);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String argv[]) {
        ss   = new OESubSearch(argv[0]);

        ifs = new oemolithread(argv[1]);
        ofs = new oemolothread(argv[2]);

        iqueue = new ArrayBlockingQueue<OEGraphMol>(1024);
        oqueue = new ArrayBlockingQueue<OEGraphMol>(1024);

        Thread pthrd = new ParseThread();
        pthrd.start();

        Thread sthrd = new SearchThread();
        sthrd.start();

        try {
            OEGraphMol mol = oqueue.take();
            while (mol.IsValid()) {
                oechem.OEWriteMolecule(ofs, mol);
                mol = oqueue.take();
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        ofs.close();
        ifs.close();
    }
}
//@ </SNIPPET>
