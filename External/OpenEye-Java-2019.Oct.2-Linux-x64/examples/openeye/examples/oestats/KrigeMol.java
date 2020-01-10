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
 * KrigeMol -training training_mols
 *          -in predict_mols
 *          -response_tag FOO
 *****************************************************************************/
package openeye.examples.oestats;

import java.util.HashMap;

import openeye.oechem.*;
import openeye.oestats.*;
import openeye.oedepict.*;

public class KrigeMol {

    private static HashMap<String, OEColor> GetColorDict() {
        HashMap<String, OEColor> colormap = new HashMap<String, OEColor>();

        // add the colors
        colormap.put("red",oechem.getOERed());
        colormap.put("green",oechem.getOEGreen());
        colormap.put("blue",oechem.getOEBlue());
        colormap.put("yellow",oechem.getOEYellow());
        colormap.put("cyan",oechem.getOECyan());
        colormap.put("magenta",oechem.getOEMagenta());
        colormap.put("orange",oechem.getOEOrange());
        colormap.put("pink",oechem.getOEPink());
        colormap.put("grey",oechem.getOEPurple());
        colormap.put("royalblue",oechem.getOERoyalBlue());
        colormap.put("olivebrown",oechem.getOEOliveBrown());
        colormap.put("olivegreen",oechem.getOEOliveGreen());
        colormap.put("oliveGrey",oechem.getOEOliveGrey());
        colormap.put("limegreen",oechem.getOELimeGreen());
        colormap.put("darkblue",oechem.getOEDarkBlue());
        colormap.put("darkgreen",oechem.getOEDarkGreen());
        colormap.put("darkblue",oechem.getOEDarkBlue());
        colormap.put("darkyellow",oechem.getOEDarkYellow());
        colormap.put("darkcyan",oechem.getOEDarkCyan());
        colormap.put("darkmagenta",oechem.getOEDarkMagenta());
        colormap.put("darkorange",oechem.getOEDarkOrange());
        colormap.put("darkgrey",oechem.getOEDarkGrey());
        colormap.put("darkrose",oechem.getOEDarkRose());
        colormap.put("darkbrown",oechem.getOEDarkBrown());
        colormap.put("darksalmon",oechem.getOEDarkSalmon());
        colormap.put("darkpurple",oechem.getOEDarkPurple());
        colormap.put("mediumbrown",oechem.getOEMediumBrown());
        colormap.put("mediumBlue",oechem.getOEMediumBlue());
        colormap.put("mediumyellow",oechem.getOEMediumYellow());
        colormap.put("mediumgreen",oechem.getOEMediumGreen());
        colormap.put("mediumorange",oechem.getOEMediumOrange());
        colormap.put("mediumpurple",oechem.getOEMediumPurple());
        colormap.put("mediumsalmon",oechem.getOEMediumSalmon());
        colormap.put("lightblue",oechem.getOELightBlue());
        colormap.put("lightgrey",oechem.getOELightGrey());
        colormap.put("lightpurple",oechem.getOELightPurple());
        colormap.put("lightsalmon",oechem.getOELightSalmon());
        colormap.put("lightbrown",oechem.getOELightBrown());
        colormap.put("lightorange",oechem.getOELightOrange());
        colormap.put("lightgreen",oechem.getOELightGreen());
        colormap.put("bluetint",oechem.getOEBlueTint());
        colormap.put("brown",oechem.getOEBrown());
        colormap.put("mandarin",oechem.getOEMandarin());
        colormap.put("greenblue",oechem.getOEGreenBlue());
        colormap.put("greentint",oechem.getOEGreenTint());
        colormap.put("hotpink",oechem.getOEHotPink());
        colormap.put("pinktint",oechem.getOEPinkTint());
        colormap.put("redorange",oechem.getOERedOrange());
        colormap.put("seagreen",oechem.getOESeaGreen());
        colormap.put("skyblue",oechem.getOESkyBlue());
        colormap.put("violet",oechem.getOEViolet());
        colormap.put("yellowtint",oechem.getOEYellowTint());
        colormap.put("copper",oechem.getOECopper());
        colormap.put("gold",oechem.getOEGold());
        colormap.put("silver",oechem.getOESilver());
        colormap.put("pewter",oechem.getOEPewter());
        colormap.put("brass",oechem.getOEBrass());

        return colormap;
    }

    public static HashMap<String, OEColor> colorMap = GetColorDict();

    private static void OEAddParameterColors(OEInterface itf, String flag) {
        OEParameter param = itf.GetParameter(flag);
        for (String key: KrigeMol.colorMap.keySet())
            param.AddLegalValue(key);
    }

    private static OEColor OETextToColor(String text) {
        return KrigeMol.colorMap.get(text);
    }

    private static String OEGetFilename(OEInterface itf, String flag) {
        String prefix = itf.GetString("-prefix");
        OEParameter param = itf.GetParameter(flag);
        if (param.GetHasValue())
            return itf.GetString(flag);
        return itf.GetString("-prefix") + "_" + itf.GetString(flag);
    }

    private static boolean OEIsUnmeasured(OEInterface itf, double val) {
        if (itf.HasDouble("-unmeasured_greater")) {
            double uVal = itf.GetDouble("-unmeasured_greater");
            if (itf.GetBool("-krige_log")) {
                uVal = Math.log10(uVal);
            }
            if (val > uVal)
                return true;
        }

        if (itf.HasDouble("-unmeasured_less")) {
            double uVal = itf.GetDouble("-unmeasured_less");
            if (itf.GetBool("-krige_log")) {
                uVal = Math.log10(uVal);
            }
            if (val < uVal)
                return true;
        }

        // minor oddity for unwrapped itf.GetDoubleList()
        int iVal = 0;
        while (itf.HasDouble("-unmeasured_values", iVal)) {
            double uVal = itf.GetDouble("-unmeasured_values", iVal);
            if (itf.GetBool("-krige_log")) {
                uVal = Math.log10(uVal);
            }
            if (val == uVal)
                return true;
            ++iVal;
        }

        return false;
    }

    private static void PerformKriging(OEInterface itf) {
        // Figure out output file name
        String outFile = OEGetFilename(itf, "-molecule_file");
        String reportFile = OEGetFilename(itf,"-report_file");
        String failTrainFile = OEGetFilename(itf,"-training_fail_file");
        String failKrigeFile = OEGetFilename(itf,"-prediction_fail_file");
        String settingsFile = OEGetFilename(itf,"-settings_file");

        // Write the settings file and splash to screen
        oechem.OEWriteSettings(itf, oechem.oeout, false);
        oeofstream ofs = new oeofstream(settingsFile);
        oechem.OEWriteSettings(itf, ofs, true);
        ofs.close();

        // Setup report options
        OEKrigeReportOptions rOpts = new OEKrigeReportOptions();
        if (itf.HasString("-report_title")) {
            rOpts.SetTitle(itf.GetString("-report_title"));
        }
        String responseName = null;
        if (itf.HasString("-response_name")) {
            rOpts.SetResponseName(itf.GetString("-response_name"));
            responseName = itf.GetString("-response_name");
        } else {
            rOpts.SetResponseName(itf.GetString("-response_tag"));
            responseName = itf.GetString("-response_tag");
        }
        if (itf.HasString("-atom_contribution_negative_color")) {
            OEColor color = OETextToColor(itf.GetString("-atom_contribution_negative_color"));
            rOpts.SetContributionNegativeColor(color);
        }
        if (itf.HasString("-atom_contribution_positive_color")) {
            OEColor color = OETextToColor(itf.GetString("-atom_contribution_positive_color"));
            rOpts.SetContributionPositiveColor(color);
        }
        for (String param : itf.GetStringList("-report_tagged_data")) {
            rOpts.PushSDProperty(param);
        }
        // minor oddity for unwrapped itf.GetDoubleList()
        int iVal = 0;
        while (itf.HasDouble("-classification_boundaries", iVal)) {
            double boundary = itf.GetDouble("-classification_boundaries", iVal);
            if (itf.GetBool("-krige_log")) {
                boundary = Math.log10(boundary);
            }
            rOpts.PushClassificationBoundry(boundary);
            ++iVal;
        }
        for (String param : itf.GetStringList("-classification_colors")) {
            OEColor color = OETextToColor(param);
            rOpts.PushClassificationColor(color);
        }

        if (itf.GetBool("-hide_krige_details"))
            rOpts.SetShowKrigeDetails(false);
        else
            rOpts.SetShowKrigeDetails(true);

        rOpts.SetUnlog(itf.GetBool("-krige_log"));
        rOpts.SetShowConfidence(itf.GetBool("-show_response_confidence"));
        rOpts.SetResponseSignificantFigures(itf.GetUnsignedInt("-report_significant_figures"));
        if (itf.HasString("-atom_contribution_positive_label"))
            rOpts.SetContributionPositiveLabel(itf.GetString("-atom_contribution_positive_label"));
        if (itf.HasString("-atom_contribution_negative_label"))
            rOpts.SetContributionNegativeLabel(itf.GetString("-atom_contribution_negative_label"));
        if (itf.HasDouble("-atom_contribution_baseline_response")) {
            double baseline = itf.GetDouble("-atom_contribution_baseline_response");
            if (itf.GetBool("-krige_log")) {
                baseline = Math.log10(baseline);
            }
            rOpts.SetContributionBaseline(baseline);
        }

        // Figure out the trend properties for universal kriging if we have them
        OEMolPropertyList universalProps = new OEMolPropertyList() ;
        for (String propStr : itf.GetStringList("-universal_prop")) {
            if (propStr == "mw") {
                universalProps.Add(new OEMolecularWeightPropertyFxn());
                rOpts.PushProperty(new OEMolecularWeightPropertyFxn(),"Molecular Weight");
            }
            else
                oechem.OEThrow.Error("Unknown property " + propStr + " passed to -universal_prop");
        }

        // Setup the object for getting the response from the training molecules
        OEMolPropertyFxn responseFxn = new OEMolTaggedPropertyFxn(itf.GetString("-response_tag"));
        if (itf.GetBool("-krige_log"))
            responseFxn = new OEMolLog10PropertyFxn(responseFxn);

        // Construct the krige
        OELocalKrigeOptions lopts = null;
        OEGlobalKrigeOptions gopts = null;
        if (itf.HasUnsignedInt("-local_krige")) {
            lopts = new OELocalKrigeOptions();
            lopts.SetNumLocal(itf.GetUnsignedInt("-local_krige"));
        } else {
            gopts = new OEGlobalKrigeOptions();
        }

        // Set if the distance function is stereo aware
        OEDefaultKrigeDistanceOptions distOpts = new OEDefaultKrigeDistanceOptions();
        distOpts.SetStereoAware(!itf.GetBool("-ignore_stereo"));

        OEMolKrige krige = null;
        if (lopts != null)
            krige = new OEMolKrige(distOpts, universalProps, lopts);
        else
            krige = new OEMolKrige(distOpts, universalProps, gopts);

        // Add the training molecules to the Krige
        System.out.println("Reading in training molecules");
        OEStopwatch sw = new OEStopwatch();
        oemolostream failStr = null;
        int numTrain = 0;
        int numTrainFail = 0;
        int numTrainUnmeasured = 0;
        int numTrainMeasured = 0;
        for (String trainFile : itf.GetStringList("-train")) {
            oemolistream imstr = new oemolistream(trainFile);
            OEMol mol = new OEMol();
            while (oechem.OEReadMolecule(imstr,mol)) {
                boolean success = false;
                boolean measured = true;
                if (responseFxn.Has(mol)) {
                    double response = responseFxn.Get(mol);
                    if (OEIsUnmeasured(itf,response)) {
                        ++numTrainUnmeasured;
                        measured = false;
                        success = krige.AddUnmeasuredTraining(mol, response);
                    }
                    else {
                        ++numTrainMeasured;
                        success = krige.AddTraining(mol, response);
                    }
                    ++numTrain;
                }
                if (!success) {
                    ++numTrainFail;
                    if (failStr == null) {
                        failStr = new oemolostream(failTrainFile);
                    }
                    oechem.OEWriteMolecule(failStr, mol);
                }
                if (sw.Elapsed() > 10.0) {
                    sw.Start();
                    if (numTrainFail != 0) {
                        System.out.format("So far added %d training molecules, and of those %d unmeasured are unmeasured. %d input molecules are missing response data or universal krige properties.\n",
                                          numTrain, numTrainUnmeasured, numTrainFail);
                    }
                    else {
                        System.out.format("So far added %d training molecules, and of those %d unmeasured are unmeasured.\n",
                                          numTrain, numTrainUnmeasured);
                    }
                }
            }
            imstr.close();
        }
        if (failStr != null) {
            failStr.close();
            System.out.println("Training failed molecules written to " + failTrainFile);
        }
        if (numTrainFail != 0) {
            System.out.format("Added %d training molecules, and of those %d unmeasured are unmeasured.  %d input molecules are missing response data or universal krige properties.\n",
                              numTrain, numTrainUnmeasured, numTrainFail);
        }
        else {
            System.out.format("Added %d training molecules, and of those %d unmeasured are unmeasured.\n",
                              numTrain, numTrainUnmeasured);
        }

        //--------------------------------------------------------------------------------=
        // Train the Krige on the molecules we added
        System.out.println("\nTraining krige model");
        if (krige.Train())
            System.out.println("Training successful");
        else {
            System.out.println("Training failed, aborting");
            return;
        }

        // Krige for MW of the test molecules, and compare to actual MW
        String krigeTag = String.format("Krige(%s)", responseName);
        String upperTag = String.format("Krige(%s) 95%% confidence upper", responseName);
        String lowerTag = String.format("Krige(%s) 95%% confidence lower", responseName);

        System.out.println("Predicting " + responseName + " of input molecules");
        int numKrige = 0;
        int numKrigeFail = 0;
        oemolostream outstr = new oemolostream(outFile);
        OEMultiPageImageFile multi = new OEMultiPageImageFile(OEPageOrientation.Landscape, OEPageSize.US_Letter);
        failStr = null;
        sw.Start();
        for (String molFile : itf.GetStringList("-in")) {
            oemolistream imstr = new oemolistream(molFile);
            OEMol mol = new OEMol();
            while (oechem.OEReadMolecule(imstr,mol)) {
                OEMolKrigeResult result = krige.GetResult(mol);
                boolean success = true;
                if (result.Valid()) {
                    double response = result.GetResponse();
                    double conf95 = 1.96*Math.sqrt(result.GetVariance());
                    double upper95 = response + conf95;
                    double lower95 = response - conf95 ;
                    if (itf.GetBool("-krige_log")) {
                        response = Math.pow(10.0, response);
                        upper95 = Math.pow(10.0, upper95);
                        lower95 = Math.pow(10.0, lower95);
                    }
                    oechem.OESetSDData(mol, krigeTag, Double.toString(response));
                    oechem.OESetSDData(mol, upperTag, Double.toString(upper95));
                    oechem.OESetSDData(mol, lowerTag, Double.toString(lower95));
                    if (!itf.GetBool("-no_report")) {
                        OEImage image = multi.NewPage();
                        oestats.OEKrigeRenderReport(image, result, mol, rOpts);
                    }
                    oechem.OEWriteMolecule(outstr, mol);
                }
                else
                    success = false;

                ++numKrige;
                if (!success) {
                    ++numKrigeFail;
                    failStr = new oemolostream(failKrigeFile);
                    oechem.OEWriteMolecule(failStr, mol);
                }
                if (sw.Elapsed() > 10.0) {
                    System.out.format("Kriging predicted %d molecules (%d failures) so far\n", numKrige, numKrigeFail);
                    sw.Start();
                }
            }
            imstr.close();
            outstr.close();
            if (failStr != null) {
                failStr.close();
                System.out.format("Kriging predicted %d molecules (%d failures)\n", numKrige, numKrigeFail);
            }
        }
        if (!itf.GetBool("-no_report")) {
            System.out.println("Writing report to " + reportFile);
            oedepict.OEWriteMultiPageImage(reportFile, multi);
        }
        System.out.println("Finished");
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);
        OEAddParameterColors(itf,"-atom_contribution_positive_color");
        OEAddParameterColors(itf,"-atom_contribution_negative_color");
        OEAddParameterColors(itf,"-classification_colors");

        if (!oechem.OEParseCommandLine (itf, args, "KrigeMol"))
            return;
        PerformKriging(itf);
    }

    private static String interfaceData =
"!CATEGORY Input 1\n" +
"  !PARAMETER -param 0\n" +
"    !TYPE param_file\n" +
"    !REQUIRED false\n" +
"    !SIMPLE false\n" +
"    !BRIEF Parameter file\n" +
"  !END\n" +
"  !PARAMETER -in 1\n" +
"    !TYPE string\n" +
"    !LIST true\n" +
"    !REQUIRED true\n" +
"    !BRIEF Input molecules to predict\n" +
"  !END\n" +
"  !PARAMETER -training\n" +
"    !ALIAS -train\n" +
"    !TYPE string\n" +
"    !LIST true\n" +
"    !REQUIRED true\n" +
"    !LEGAL_VALUE *.oeb.gz\n" +
"    !LEGAL_VALUE *.sdf.gz\n" +
"    !LEGAL_VALUE *.oeb\n" +
"    !LEGAL_VALUE *.sdf\n" +
"    !BRIEF Training molecules for the krige.  Response values should be tagged to SD or generic data (see -response_tag).\n" +
"  !END\n" +
"!END\n" +
"!CATEGORY Settings 2\n" +
"  !PARAMETER -response_tag 1\n" +
"    !ALIAS -tag\n" +
"    !ALIAS -response_tag\n" +
"    !TYPE string\n" +
"    !REQUIRED true\n" +
"    !BRIEF SD or generic data tag on the training molecules the Krige will predict.\n" +
"  !END\n" +
"  !PARAMETER -krige_log 2\n" +
"    !ALIAS -log\n" +
"    !TYPE bool\n" +
"    !DEFAULT false\n" +
"    !SIMPLE true\n" +
"    !BRIEF Krige on the log of the property specified by the -response_tag.  Commonly used when predicting activity concentration (e.g. IC50).\n" +
"  !END\n" +
"  !PARAMETER -local_krige 3\n" +
"    !ALIAS -krige_local\n" +
"    !ALIAS -num_local\n" +
"    !TYPE unsigned\n" +
"    !SIMPLE true\n" +
"    !DEFAULT 0\n" +
"    !BRIEF Number of nearest training molecules to use in the krige.  Setting 0 uses all, i.e., global kriging.\n" +
"  !END\n" +
"  !PARAMETER -response_name 4\n" +
"    !ALIAS -name\n" +
"    !TYPE string\n" +
"    !REQUIRED false\n" +
"    !SIMPLE false\n" +
"    !BRIEF Name of the response.  If not specified the setting of -response_tag will be used.\n" +
"  !END\n" +
"  !PARAMETER -ignore_stereo 5\n" +
"    !TYPE bool\n" +
"    !DEFAULT false\n" +
"    !SIMPLE false\n" +
"    !BRIEF If true the stereoness of atoms will be ignored in the 2d distance function the krige uses.\n" +
"  !END\n" +
"  !CATEGORY Specifying Unmeasured Responses 6\n" +
"    !PARAMETER -unmeasured_values 1\n" +
"      !ALIAS -unmeasured\n" +
"      !TYPE double\n" +
"      !REQUIRED false\n" +
"      !LIST true\n" +
"      !SIMPLE false\n" +
"      !BRIEF Response values that will be considered unmeasured.\n" +
"    !END\n" +
"    !PARAMETER -unmeasured_greater 2\n" +
"      !TYPE double\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF Response values greater than this value will be considered unmeasured\n" +
"    !END\n" +
"    !PARAMETER -unmeasured_less 2\n" +
"      !TYPE double\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF Response values less than this value will be considered unmeasured\n" +
"    !END\n" +
"  !END\n" +
"  !CATEGORY Universal kriging 7\n" +
"    !BRIEF Universal kriging flags.  They can be used in combination.\n" +
"    !PARAMETER -universal_tag\n" +
"      !ALIAS -utag\n" +
"      !TYPE string\n" +
"      !LIST true\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF SD or generic data tags to use as trend properties for universal kriging.\n" +
"    !END\n" +
"    !PARAMETER -universal_tag_log\n" +
"      !ALIAS -utag_log\n" +
"      !TYPE string\n" +
"      !LIST true\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF Same as -universal_tag, except that the log of the values will be used.\n" +
"    !END\n" +
"    !PARAMETER -universal_prop\n" +
"      !ALIAS -uprop\n" +
"      !TYPE string\n" +
"      !LIST true\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !LEGAL_VALUE mw\n" +
"      !BRIEF Calculated properties to use as trend properties for universal kriging\n" +
"    !END\n" +
"  !END\n" +
"  !CATEGORY Report Options 8\n" +
"    !PARAMETER -no_report 0\n" +
"      !TYPE bool\n" +
"      !DEFAULT false\n" +
"      !BRIEF Supress creating a report PDF file.\n" +
"    !END\n" +
"    !PARAMETER -report_title 1\n" +
"      !ALIAS -title\n" +
"      !TYPE string\n" +
"      !SIMPLE false\n" +
"      !BRIEF Title that will be printed on the top of each page of the report.\n" +
"    !END\n" +
"    !PARAMETER -report_significant_figures 3\n" +
"      !ALIAS -sigfigs\n" +
"      !TYPE unsigned\n" +
"      !SIMPLE false\n" +
"      !DEFAULT 3\n" +
"      !BRIEF Number of significant figures to report the response in on the report.\n" +
"    !END\n" +
"    !PARAMETER -show_response_confidence 4\n" +
"      !ALIAS -conf95\n" +
"      !TYPE bool\n" +
"      !DEFAULT false\n" +
"      !SIMPLE false\n" +
"      !BRIEF Show the 95% confidence interval of the response on the report.\n" +
"    !END\n" +
"    !PARAMETER -report_tagged_data 2\n" +
"      !ALIAS -extra_tags\n" +
"      !TYPE string\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF Extra SD/genertic data to include on the report.  (Not used by the calculation).\n" +
"    !END\n" +
"    !PARAMETER -atom_contribution_positive_color 4\n" +
"      !ALIAS -positive_color\n" +
"      !ALIAS -pos_color\n" +
"      !TYPE string\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF Color of positive atom contributions on the krige report\n" +
"    !END\n" +
"    !PARAMETER -atom_contribution_negative_color 5\n" +
"      !ALIAS -negative_color\n" +
"      !ALIAS -neg_color\n" +
"      !TYPE string\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF Color of positive atom contributions on the krige report\n" +
"    !END\n" +
"    !PARAMETER -atom_contribution_positive_label 6\n" +
"      !ALIAS -positive_label\n" +
"      !ALIAS -pos_label\n" +
"      !TYPE string\n" +
"      !SIMPLE false\n" +
"      !REQUIRED false\n" +
"      !BRIEF Legend label for atoms that contribute positively to the response\n" +
"    !END\n" +
"    !PARAMETER -atom_contribution_negative_label 7\n" +
"      !ALIAS -negative_label\n" +
"      !ALIAS -neg_label\n" +
"      !TYPE string\n" +
"      !SIMPLE false\n" +
"      !REQUIRED false\n" +
"      !BRIEF Legend label for atoms that contribute negatively to the response\n" +
"    !END\n" +
"    !PARAMETER -atom_contribution_baseline_response 8\n" +
"      !ALIAS -baseline_response\n" +
"      !TYPE double\n" +
"      !SIMPLE false\n" +
"      !REQUIRED false\n" +
"      !BRIEF Set the baseline response that is considered neutral for atom contributions.  If not specified the predicted response of each molecule is used for atom contributions.\n" +
"    !END\n" +
"    !PARAMETER -classification_boundaries 9\n" +
"      !ALIAS -class\n" +
"      !TYPE double\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !LIST true\n" +
"      !BRIEF Report result will be given in terms of classifcation defined by these response boundary values.\n" +
"    !END\n" +
"    !PARAMETER -classification_colors 10\n" +
"      !ALIAS -class_colors\n" +
"      !LIST true\n" +
"      !TYPE string\n" +
"      !REQUIRED false\n" +
"      !SIMPLE false\n" +
"      !BRIEF User specification of the classification colors.  The order applies to the classifications is highest to lowest.\n" +
"    !END\n" +
"    !PARAMETER -hide_krige_details 11\n" +
"      !ALIAS -hide_details\n" +
"      !TYPE bool\n" +
"      !SIMPLE false\n" +
"      !DEFAULT false\n" +
"      !BRIEF Hide the krige details in the report (variogram and model quality).\n" +
"    !END\n" +
"  !END\n" +
"!END\n" +
"!CATEGORY Output 5\n" +
"  !PARAMETER -prefix 1\n" +
"    !TYPE string\n" +
"    !DEFAULT krige\n" +
"    !SIMPLE true\n" +
"    !BRIEF Prefix for output files\n" +
"  !END\n" +
"  !PARAMETER -molecule_file 2\n" +
"    !ALIAS -mol_file\n" +
"    !ALIAS -out\n" +
"    !TYPE string\n" +
"    !SIMPLE false\n" +
"    !DEFAULT molecules.sdf\n" +
"    !LEGAL_VALUE *.oeb.gz\n" +
"    !LEGAL_VALUE *.sdf.gz\n" +
"    !LEGAL_VALUE *.oeb\n" +
"    !LEGAL_VALUE *.sdf\n" +
"    !BRIEF Output molecule for molecules with krige predictions tagged to them\n" +
"  !END\n" +
"  !PARAMETER -report_file 3\n" +
"    !ALIAS -report\n" +
"    !TYPE string\n" +
"    !DEFAULT report.pdf\n" +
"    !LEGAL_VALUE *.pdf\n" +
"    !SIMPLE false\n" +
"    !BRIEF Report file\n" +
"  !END\n" +
"  !PARAMETER -training_fail_file 4\n" +
"    !ALIAS -train_fail_file\n" +
"    !TYPE string\n" +
"    !DEFAULT training_fail.sdf\n" +
"    !SIMPLE false\n" +
"    !BRIEF Output file for training molecules that failed to be added to the Krige\n" +
"  !END\n" +
"  !PARAMETER -prediction_fail_file 5\n" +
"    !ALIAS -predict_fail_file\n" +
"    !TYPE string\n" +
"    !DEFAULT prediction_fail.sdf\n" +
"    !SIMPLE false\n" +
"    !BRIEF Output files for molecules that the Krige failed to predict\n" +
"  !END\n" +
"  !PARAMETER -settings_file 6\n" +
"    !TYPE string\n" +
"    !DEFAULT settings.txt\n" +
"    !SIMPLE false\n" +
"    !BRIEF Output file with parameters settings for this run\n" +
"  !END\n" +
"!END\n";
}
