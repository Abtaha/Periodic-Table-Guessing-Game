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
/*****************************************************************************
 * Utility to load a matched pair index
 *  and dump a listing of transformations related to probe molecule(s)
 * ---------------------------------------------------------------------------
 * MatchedPairTransformProbe mmp_index probe_mols
 *
 * mmp_index: filename of MMP index file
 * probe_mols: filename of input molecules to use as probes for related transforms
 *****************************************************************************/
package openeye.examples.oemedchem;

import openeye.oechem.*;
import openeye.oemedchem.*;
import java.util.*;

public class MatchedPairTransformProbe {
    // simple internal class to rank transform output
    public static class MMPXform implements Comparable<MMPXform> {
        OEMatchedPairTransform xform;
        float avg;
        float std;
        int num;

        public MMPXform(OEMatchedPairTransform xfm,
                        float average, float stddev, int count) {
            xform = xfm;
            avg = average;
            std = stddev;
            num = count;
        }

        public int compareTo(MMPXform another) {
            // sort descending by absolute value of the average
            double lAvg = Math.abs(this.avg);
            double rAvg = Math.abs(another.avg);

            int retval = 0;
            if (lAvg > rAvg)
                retval = -1;
            else if (lAvg < rAvg)
                retval = 1;
            return retval;
        }
    }

    // compute some simple statistics for ranking
    private static float Average(List<Float> data) {
        Float avg = null;
        for (Float value : data) {
            if (avg == null)
                avg = value;
            else
                avg += value;
        }
        if (avg != null)
            avg = avg / (float)data.size();

        return avg;
    }
    private static float StdDev(List<Float> data) {
        if (data.size() <= 1)
            return 0.0f;

        float mean = Average(data);

        // calculate the sum of squares
        double sum = 0;
        for (Float value : data) {
            final double v = value - mean;
            sum += v * v;
        }
        return (float)Math.sqrt( sum / data.size() );
    }

    private static void MMPAnalyzeProbe(OEInterface itf) {

        // check MMP index
        String mmpimport = itf.GetString("-mmpindex");
        if (!oemedchem.OEIsMatchedPairAnalyzerFileType(mmpimport)) {
            oechem.OEThrow.Fatal("Not a valid matched pair index input file, " + mmpimport);
        }

        // load MMP index
        OEMatchedPairAnalyzer mmp = new OEMatchedPairAnalyzer();
        if (!oemedchem.OEReadMatchedPairAnalyzer(mmpimport, mmp)) {
            oechem.OEThrow.Fatal("Unable to load index " + mmpimport);
        }

        if (mmp.NumMols() == 0) {
            oechem.OEThrow.Fatal("No records in loaded MMP index file: " + mmpimport);
        }

        if (mmp.NumMatchedPairs() == 0) {
            oechem.OEThrow.Fatal("No matched pairs found in MMP index file, " +
                                 "use -fragGe,-fragLe options to extend indexing range");
        }

        // input probe structure(s)
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-input")))
            oechem.OEThrow.Fatal("Unable to open input probe molecule file: " + itf.GetString("-input"));

        // request a specific context for the transform activity, here 0-bonds;
        int chemctxt = OEMatchedPairContext.Bond0;
        String askcontext = itf.GetString("-context");
        char ctxt = askcontext.charAt(0);
        switch (ctxt) {
        case '0':
            chemctxt = OEMatchedPairContext.Bond0;
            break;
        case '1':
            chemctxt = OEMatchedPairContext.Bond1;
            break;
        case '2':
            chemctxt = OEMatchedPairContext.Bond2;
            break;
        case '3':
            chemctxt = OEMatchedPairContext.Bond3;
            break;
        case 'a':
        case 'A':
            chemctxt = OEMatchedPairContext.AllBonds;
            break;
        default:
            oechem.OEThrow.Fatal("Invalid context specified: " +
                                 askcontext + ", only 0|1|2|3|A allowed");
            break;
        }

        boolean bPrintTransforms = itf.GetBool("-printlist");
        // if a data field was specified, retreive the SD data field name
        String field = null;
        if (itf.HasString("-datafield"))
            field = itf.GetString("-datafield");

        OEMatchedPairTransformExtractOptions extractOptions = new OEMatchedPairTransformExtractOptions();
        // specify amount of chemical context at the site of the substituent change
        extractOptions.SetContext(chemctxt);
        // controls how transforms are extracted (direction and allowed properties)
        extractOptions.SetOptions(OEMatchedPairTransformExtractMode.Sorted |
                                  OEMatchedPairTransformExtractMode.NoSMARTS);

        int record = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs,mol)) {
            ++record;

            System.out.printf("*** Probe molecule, %s record=%d%n", oechem.OEMolToSmiles(mol), record);

            // walk the transforms from the indexed matched pairs that are related to this probe molecule
            List<MMPXform> xforms = new ArrayList<MMPXform>();

            int xfmidx = 0;
            for (OEMatchedPairTransform mmpxform : oemedchem.OEMatchedPairGetTransforms(mmp,
                                                                                        mol,
                                                                                        extractOptions)) {
                ++xfmidx;
                if (bPrintTransforms)
                    System.out.printf("%2d %s%n",
                                      xfmidx,
                                      mmpxform.GetTransform());

                // compute delta property
                int mmpidx = 0;
                List<Float> prop = new ArrayList<Float>();
                for (OEMatchedPair mmppair : mmpxform.GetMatchedPairs()) {
                    ++mmpidx;
                    String mmpinfo = String.format("\t%d: (%d,%d)",
                                                   mmpidx,mmppair.FromIndex(),mmppair.ToIndex());

                    for (String tag : mmppair.GetDataTags()) {
                        mmpinfo = mmpinfo + String.format(" %s=(%s,%s)",
                                                          tag,
                                                          mmppair.GetFromSDData(tag),
                                                          mmppair.GetToSDData(tag));
                        if (tag.equals(field)) {
                            Float fromValue = Float.valueOf(mmppair.GetFromSDData(tag));
                            Float toValue = Float.valueOf(mmppair.GetToSDData(tag));
                            prop.add(toValue - fromValue);
                        }
                    }
                    if (bPrintTransforms)
                        System.out.printf("%s%n",mmpinfo);
                }
                // skip if property not found
                if (!prop.isEmpty()) {
                    xforms.add(new MMPXform(mmpxform, Average(prop), StdDev(prop), prop.size()));
                }
                else if (field == null) {
                    xforms.add(new MMPXform(mmpxform, 0.0f, 0.0f, mmpidx));
                }
            }
            if (xforms.isEmpty()) {
                if (field != null)
                    System.out.printf("*** No related transforms found with %s%n", field);
                else
                    System.out.printf("*** No related transforms%n");
            }
            else {
                if (field != null) {
                    System.out.printf("*** Related transforms sorted by delta (%s)%n", field);
                    Collections.sort(xforms);
                }
                else
                    System.out.printf("*** Related transforms%n");

                int idx = 0;
                for (MMPXform xfm : xforms) {
                    ++idx;
                    if (field == null) {
                        System.out.printf("%2d %s%n",idx,xfm.xform.GetTransform());
                    }
                    else {
                        if ((extractOptions.GetOptions() & OEMatchedPairTransformExtractMode.NoSMARTS) != 0) {
                            // not 'invertable' if SMARTS qualifiers were applied
                            if (xfm.avg < 0.f) {
                                xfm.avg = -1.f * xfm.avg;
                                xfm.xform.Invert();
                            }
                        }
                        System.out.printf("%2d %s=(avg=%.2f,stdev=%.2f,num=%d) %s%n",idx,
                                          field,
                                          (float)xfm.avg,
                                          (float)xfm.std,
                                          xfm.num,
                                          xfm.xform.GetTransform());
                    }
                }
            }
            System.out.printf("%n");
        }
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);

        if (oechem.OEParseCommandLine (itf, args, "MatchedPairTransformProbe")) {
            MMPAnalyzeProbe(itf);
        }
    }

    private static String interfaceData =
"# matchedpairtransformprobe interface file\n" +
"!CATEGORY MatchedPairTransformProbe\n" +
"    !CATEGORY I/O\n" +
"        !PARAMETER -mmpindex 1\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of serialized matched pair index to load\n" +
"          !KEYLESS 1\n" +
"        !END\n" +
"        !PARAMETER -input 2\n" +
"          !ALIAS -in\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of probe molecule(s) to use to retrieve related transforms\n" +
"          !KEYLESS 2\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY options\n" +
"        !PARAMETER -context 1\n" +
"           !ALIAS -c\n" +
"           !TYPE string\n" +
"           !DEFAULT 0\n" +
"           !BRIEF chemistry context to use for the transformation [0|1|2|3|A]\n" +
"        !END\n" +
"        !PARAMETER -printlist 2\n" +
"           !ALIAS -p\n" +
"           !ALIAS -pri\n" +
"           !TYPE bool\n" +
"           !DEFAULT 1\n" +
"           !BRIEF print all transforms and matched pairs\n" +
"        !END\n" +
"        !PARAMETER -datafield 3\n" +
"           !ALIAS -d\n" +
"           !TYPE string\n" +
"           !BRIEF sort transforms based on delta change in this property\n" +
"        !END\n" +
"    !END\n" +
"!END\n";
}
