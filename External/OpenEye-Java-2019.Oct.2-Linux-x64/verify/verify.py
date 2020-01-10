# CONFIDENTIAL. (C) 2017 OpenEye Scientific Software Inc.
# All rights reserved.
# ALL SOFTWARE BELOW IS PROPRIETARY AND CONFIDENTIAL TO OPENEYE
# SCIENTIFIC SOFTWARE INC., AND IS SUBJECT TO THE FULL PROTECTION OF
# COPYRIGHT AND TRADESECRET LAW.
# Copying or modifying this software is strictly prohibited and illegal.
# Using this software without a valid license from OpenEye is strictly
# prohibited and illegal.  De-obfuscating, de-minimizing, or any other
# attempt to reverse engineer or discover the workings of this software,
# is strictly prohibited. Even if software is exposed or visible, it is
# still proprietary and confidential and the above prohibitions apply.
# This software is NOT "Sample Code". For purposes of customization or
# interoperation with other software you must use Sample Code
# specifically labeled as such.
# Please contact OpenEye at eyesopen.com/legal if you have any questions
# about this warning.

from __future__ import print_function
import glob
import os
import sys
import verifyhelper
import tempfile
import shutil
import traceback
import subprocess
import os.path


def usage():
    print("verify.py [ -subset ] [ -suppresscuda ]")
    print("  -subset: create subset jars by product and run examples using them")
    print("  -suppresscuda: explicitly *exclude* oecuda dependency")
    print("by default, examples are run against the uber jar and whether to include"
          " cuda dependencies is determined from the platform information")


def IsGPUAvailable():
    return sys.platform.startswith("linux") and os.path.exists('/dev/nvidia0')


def runJavaExample(cmdline, classPath, params, tmpDir):
    # add some formatting support to the command
    program = cmdline.split(' ', 1)[0]
    outfile = os.path.join(params["resultdir"], program + ".out.txt")

    JAVA_EXE = params["JAVAEXE"]

    if sys.platform == "win32" or sys.platform == "cygwin":
        classPath = classPath.replace(":", ";")

    cmdline = "%s $JAVAFLAGS -Doejava.libs.tmpdir=\"%s\" $OEJAVAFLAGS -cp \"%s\"  openeye.%s" % (JAVA_EXE, tmpDir, classPath, cmdline)
    cmdline = verifyhelper.format(cmdline, params)
    res = verifyhelper.runOEExample(cmdline, program, outfile)
    return res

def mkdir(path):
    try:
        os.makedirs(path)
    except OSError as e:
        if e.errno != 17: # ignore the exception if the path already exists
            raise

class SpecialFlags:
    def __init__(self, cmd, flags):
        self.cmd = cmd
        self.flags = flags or {}

    def __str__(self):
        return self.cmd

    def get_java_flags(self):
        return self.flags.get(sys.platform, "")

def getBitness(jar):
    if sys.platform == "win32":
      return ""

    bitness = 32      # figure out the bitness of the jar
    if jar.find("-x64") != -1:
        bitness = 64

    # check if java supports it
    proc = subprocess.Popen('java -d%d -version' % bitness, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    proc.communicate()
    if proc.returncode == 1:
        raise Exception("The OpenEye distribution is %d bit, but the current java doesn't support it." % bitness)

    return "-d%d" % bitness

def main(argv):

    bSubsetJarTest = False
    bUberJarTest   = True
    bEnableCuda    = False
    bSuppressCuda  = False
    for args in argv[1:]:
        if args.find("-help") >= 0:
            usage()
            exit(0)
        elif args.find("-subset") >= 0:
            # mutually exclusive, for now
            bSubsetJarTest = True
            bUberJarTest   = False
        elif args.find("-suppresscuda") >= 0:
            bSuppressCuda  = True
        else:
            print("Unknown arg: {}".format(args))

    # if not explicitly suppressed, try to decide if cuda should be enabled based on platform
    if bSuppressCuda:
        # explicitly suppress cuda
        bEnableCuda = False
    elif not bEnableCuda:
        # by default, include cuda dependencies for linux
        if sys.platform.startswith("linux"):
            bEnableCuda = True

    exampleData = {

      "oechem":["examples.oechem.OEChemInfo",
                "examples.oechem.MolGrep -i $datadir/data.smi -p [C] -o $resultdir/molgrep.smi",
                "docexamples.oechem.Aromaticity",
                "examples.oebio.Backbone $datadir/1d3h.pdb $resultdir/backbone.pdb",
                "examples.oebio.MakeAlpha $datadir/data.smi $resultdir/makealpha.smi",
                "examples.oebio.SplitMolComplex -v -in $datadir/1FCX.pdb.gz -out $resultdir/splitmolcomplex_1FCX.oeb.gz",
                "examples.oebio.SplitMolComplexFrags -v -in $datadir/1FCX.pdb.gz -out $resultdir/splitmolcomplexfrags_1FCX.oeb.gz",
                "examples.oebio.SplitMolComplexLowLevel -v -in $datadir/1FCX.pdb.gz -out $resultdir/splitmolcomplexlowlevel_1FCX.oeb.gz",
                "examples.oegrid.GridBabel -i $datadir/data.agd  -o $resultdir/gridbabel.agd",
                "docexamples.oechem.LoopingOverBonds",
                "docexamples.oegrid.AttachGaussianGrid $datadir/data3d.sdf.gz $resultdir/AttachGaussian.oeb.gz",
                "docexamples.oegrid.ASCIIWriter",
                "docexamples.oegrid.SpatialCoordAccess $datadir/data3d.sdf.gz",],

      "oedepict":["examples.oedepict.Depict -in $datadir/data.smi  -out $resultdir/depict.sdf",
                  "examples.oedepict.Mol2Img -in $datadir/data.smi  -out $resultdir/mol2img.svg",
                  "examples.oedepict.MDLReaction2Img $datadir/reaction.rxn $resultdir/depictmdl.svg",],

      "docking":["examples.oedocking.RescorePoses -receptor $datadir/receptor.oeb.gz -in $datadir/conformers.oeb.gz -out $resultdir/rescoreposes.oeb.gz",
                 "examples.oedocking.PoseMolecules -receptor $datadir/receptor.oeb.gz -in $datadir/1di9.sdf -out $resultdir/poses.oeb.gz",
                 "examples.oedocking.PoseMolsMultiReceptor -rec $datadir/receptor.oeb.gz -in $datadir/to_be_posed.oeb.gz -out $resultdir/poses.oeb.gz",
                 "docexamples.oedocking.DockScoreTagging $datadir/receptor.oeb.gz $datadir/conformers.oeb.gz",
                 "docexamples.oedocking.RecBoundLigandEdit $datadir/receptor.oeb.gz",
                 ],

      "grapheme":["examples.oegrapheme.BFactor2Img -complex $datadir/1FCX.pdb.gz -out $resultdir/bfactor2img.png",
                  "examples.oegrapheme.Complex2Img -complex $datadir/1FCX.pdb.gz -out $resultdir/complex2img.png",],

      "iupac":["examples.oeiupac.Mol2Nam_example -i $datadir/data.smi",],

      "filter":["examples.oemolprop.MolPropTable -in $datadir/data.smi -filtertype Lead",
                "docexamples.oemolprop.PSAAtomContributions $datadir/data3d.sdf.gz",],

      "omega":["examples.oeomega.SimpleOmega $datadir/data3d.sdf.gz $resultdir/simpleomega.oeb.gz",],

      "quacpac":["examples.oequacpac.EnumerateTautomers $datadir/data.smi $resultdir/enumerate_out.smi", ],

      "shape":["examples.oeshape.ShapeProps $datadir/data3d.sdf.gz",
               "examples.oeshape.HermiteExpansion -in $datadir/4std_prot.oeb.gz -out $resultdir/4std_prot_grid.grd",
               """examples.oeshape.HermiteTanimoto -inref $datadir/data3d.sdf.gz -infit $datadir/data3d.sdf.gz
               -out $resultdir/data3d-out.oeb
               """,],

      "spicoli":["examples.oespicoli.PolarSurfaceArea $datadir/data.smi",
                 "docexamples.oespicoli.CalculateTriangleAreas $datadir/data3d.sdf.gz",
                 "docexamples.oespicoli.MakeConnectedSurfaceCliques $datadir/data3d.sdf.gz",
                 "docexamples.oespicoli.SurfaceDataAccess",],

      "szmaptk":[ "examples.oeszmap.SzmapBestOrientations -p $datadir/4std_prot.oeb.gz -l $datadir/4std_lig.oeb.gz -o $resultdir/4std_orientations.oeb.gz",
                  "examples.oeszmap.SzmapEnergies -p $datadir/4std_prot.oeb.gz -l $datadir/4std_lig.oeb.gz" ],

      "zap":[ "examples.oezap.ZapForces $datadir/data3d.sdf.gz",],

      "case":[ "examples.oeszybki.SimpleVacuum $datadir/data3d.sdf.gz $resultdir/szybkiexample1.smi",],

      "graphsim":["examples.oegraphsim.SearchFP -query $datadir/query.smi -molfname $datadir/drugs.smi -out $resultdir/searchfp-result.ism",
                  "examples.oegraphsim.MakeFastFP -in $datadir/drugs.sdf -fpdbfname $resultdir/makefastfp-drugs.fpbin",
                  "examples.oegraphsim.SearchFastFP -query $datadir/query.smi -molfname $datadir/drugs.sdf -fpdbfname $datadir/drugs.fpbin -out $resultdir/searchfastfp-result.ism",
                  "docexamples.oegraphsim.FP2OEB $datadir/drugs.sdf $resultdir/FP2OEB.oeb.gz",
                  "docexamples.oegraphsim.FPAtomTyping",
                  "docexamples.oegraphsim.FPBitString",
                  "docexamples.oegraphsim.FPCompare",],

      "oemedchem":[ "examples.oemedchem.OEMedChemInfo",
                    "examples.oemedchem.BemisMurckoPerception $datadir/data.smi $resultdir/bemismurcko.sdf",
                    "examples.oemedchem.ChEMBLsolubility $datadir/fit.sdf $resultdir/chemblsolubility.smi",
                    "examples.oemedchem.CreateMMPIndex -input $datadir/data.smi -output $resultdir/data.smi.mmpidx -ge 10 -le 100",
                    "examples.oemedchem.MatchedPairTransform -mmpindex $resultdir/data.smi.mmpidx -input $datadir/data.smi -output $resultdir/matchedpairtransform.smi",
                    "examples.oemedchem.MatchedPairTransformList -mmpindex $resultdir/data.smi.mmpidx -printlist 1",
                    "examples.oemedchem.MatchedPairTransformProbe -mmpindex $resultdir/data.smi.mmpidx -input $datadir/data.smi -context 0 -printlist 1",
                    "examples.oemedchem.CreateMCSFragDatabase -input $datadir/data.smi -output $resultdir/data.smi.mcsfrag -ge 10 -le 100",
                    "examples.oemedchem.MCSFragOccurrence -indb $resultdir/data.smi.mcsfrag -query $datadir/data.smi -top 5",
                    "docexamples.oemedchem.FragRingChain",],

      "fastrocs":["examples.oefastrocs.SimpleJavaScript $datadir/drugs.sdf $datadir/drugs.sdf", ],
    }

    exampleToProducts = {
        "oechem"   : "OEChem",
        "oedepict" : "OEDepict",
        "fastrocs" : "OEFastROCS",
        "docking"  : "OEDocking",
        "filter"   : "OEMolProp",
        "graphsim" : "OEGraphSim",
        "grapheme" : "OEGrapheme",
        "iupac"    : "OEIUPAC",
        "oemedchem": "OEMedChem",
        "omega"    : "OEOmega",
        "quacpac"  : "OEQuacpac",
        "shape"    : "OEShape",
        "spicoli"  : "OESpicoli",
        "szmaptk"  : "OESzmap",
        "case"     : "OESzybki",
        "zap"      : "OEZap",
    }

    # tmpDir = os.path.join(tempfile.gettempdir(), "oeverify")
    tmpDir = "../verify/oeverify"
    try:
        licensedProducts = verifyhelper.getLicensedProducts("java")

        os.chdir("../examples")
        uberjar = glob.glob("../lib/oejava-*.jar")[0]
        javautiljar = glob.glob("../lib/openeye-javautils.jar")[0]
        bitflag = getBitness(uberjar)

        if sys.platform == "win32":
            uberjar = uberjar.replace("\\","/")

        parameters = {
              "JAVAEXE": "java",
              "JAVAJAR":  None,
              "datadir": "../verify/data",
              "resultdir": "../verify/results",
              "JAVAFLAGS":  os.getenv("JAVAFLAGS", "") + " " + bitflag,
              "uberjar":  uberjar,
              "OEJAVAFLAGS":  "",   # "-Doejava.libs.debug=1" will dump load information
            }

        if os.getenv('JAVA_BIN'):
            # use explicit environment setting
            javacmd = 'java'
            needquote = False
            if sys.platform == "win32":
                javacmd += '.exe'
                needquote = True
            osdir = os.getenv('JAVA_BIN')
            if " " in osdir or needquote:
                parameters['JAVAEXE'] = "\"{0}\"".format(os.path.join(osdir, javacmd))
            else:
                parameters['JAVAEXE'] = os.path.join(osdir, javacmd)
        elif os.getenv('JAVA_HOME'):
            # use explicit environment setting
            javacmd = 'java'
            needquote = False
            if sys.platform == "win32":
                javacmd += '.exe'
                needquote = True
            osdir = os.getenv('JAVA_HOME')
            if " " in osdir or needquote:
                parameters['JAVAEXE'] = "\"{0}\"".format(os.path.join(osdir, 'bin', javacmd))
            else:
                parameters['JAVAEXE'] = os.path.join(osdir, 'bin', javacmd)

        mkdir(parameters['resultdir'])

        # compile examples
        antfile = os.path.join(parameters['resultdir'], "ant.out.txt")
        antexe = "ant"
        if sys.platform == "win32":
          antexe = "ant.bat"
        if verifyhelper.runOEExample(antexe, "ant", antfile): return 1

        # run examples
        products = [x for x in licensedProducts if x in exampleData.keys()]
        for p in exampleData.keys():
            if p not in products:
                sys.stdout.write("** Not licensed %s - SKIPPING\n" % p)

        products = sorted(products)
        for p in products:
            prod = exampleToProducts[p]
            if 'OEFastROCS' in prod and not IsGPUAvailable():
                print('No GPU available - SKIPPING: {}'.format(prod))
                continue
            if prod and bSubsetJarTest:
                # generate subset jar for testing
                subsetjar = "{0}{1}oejava-{2}.jar".format(parameters['resultdir'],os.sep,prod)
                if sys.platform == "win32":
                    subsetjar = subsetjar.replace("\\","/")
                if bEnableCuda:
                    cmdline = "{0} -jar {1} -products {2} -jars {3} -out {4} -includecuda".format(parameters['JAVAEXE'],javautiljar,prod,uberjar,subsetjar)
                else:
                    cmdline = "{0} -jar {1} -products {2} -jars {3} -out {4}".format(parameters['JAVAEXE'],javautiljar,prod,uberjar,subsetjar)
                if verifyhelper.runOEExample(cmdline,
                                             "subsetjar_{0}".format(prod),
                                             os.path.join(parameters['resultdir'], "subsetjar_{0}.out.txt".format(prod))):
                    print("ERROR: creating subset jar!")
                    return 1

            if bUberJarTest:
                if os.path.exists(tmpDir):
                    shutil.rmtree(tmpDir)

                # test examples vs uberjar
                uberjarClassPath = "$uberjar:openeye.examples.jar"
                for example in exampleData[p]:
                    if isinstance(example, SpecialFlags):
                        special_param = parameters.copy()
                        special_param["JAVAFLAGS"] = special_param["JAVAFLAGS"] + " " + example.get_java_flags()
                        if runJavaExample(example.cmd, uberjarClassPath, special_param, tmpDir) : return 1
                    else:
                        if runJavaExample(example, uberjarClassPath, parameters, tmpDir) : return 1

            if bSubsetJarTest:
                if os.path.exists(tmpDir):
                    shutil.rmtree(tmpDir)

                sys.stdout.write("** Testing subset jar for %s\n" % prod)
                subsetClassPath = "{0}:openeye.examples.jar".format(subsetjar)
                for example in exampleData[p]:
                    if isinstance(example, SpecialFlags):
                        special_param = parameters.copy()
                        special_param["JAVAFLAGS"] = special_param["JAVAFLAGS"] + " " + example.get_java_flags()
                        if runJavaExample(example.cmd, subsetClassPath, special_param, tmpDir) : return 1
                    else:
                        if runJavaExample(example, subsetClassPath, parameters, tmpDir) : return 1

        return 0

    except Exception as e:
        sys.stdout.write("Caught Exception:" + str(e))
        traceback.print_exc()
        return 1
    finally:
        if os.path.exists(tmpDir):
            shutil.rmtree(tmpDir)

if __name__ == "__main__":
    sys.exit(main(sys.argv))
