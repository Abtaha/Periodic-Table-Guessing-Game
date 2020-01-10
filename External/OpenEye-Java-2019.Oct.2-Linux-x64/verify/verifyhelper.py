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
import os
import shlex
import subprocess
import sys
import re


# this sort of functionality is built in with newer pythons
def format(data, params):
    for (key,val) in params.items():
        if val is None:
            continue
        if type(val) != str:
            continue
        data = data.replace("$" + key, val)
    return data

def writefile(name, cmdline, contents=""):
    fd = None
    try:
        fd = open(name, "wb")
        fd.write(cmdline.encode() + b"\n")
        fd.write(contents)
        fd.close()
    except: 
        raise 

def catfile(name):
    try:
        fd = open(name, "r")
        sys.stdout.write(fd.read())
        fd.close()
    except:
        raise

# make sure that OE_DIR or OE_LICENSE are set
def checkLicenseVars():
    if "OE_LICENSE" in os.environ:
        if os.path.exists(os.environ["OE_LICENSE"]):
            return os.environ["OE_LICENSE"]

    licenseFile = "oe_license.txt"
    if "OE_DIR" in os.environ:
        licpath = os.path.join(os.environ["OE_DIR"], licenseFile)
        if os.path.exists(licpath):
            return licpath

    if os.path.exists("./" + licenseFile):
        os.environ["OE_DIR"] = os.path.abspath(".") 
        return "./" + licenseFile

    if os.path.exists("../" + licenseFile):
        os.environ["OE_DIR"] = os.path.abspath("../") 
        return "../" + licenseFile

    raise Exception("Could not find " + licenseFile + " in OE_LICENSE, OE_DIR, or in the current directory")
    
def getLicensedProducts(feature):
    licenseRegex = re.compile("^#PRODUCT: (\\w+)\\n#.*\\n#FEATURES: (?:[\S ]+;)*(%s)" % feature, re.M) 
    fname = checkLicenseVars();
    fd = open(fname, "rU")
    buffer = fd.read()
    fd.close()

    matchList = licenseRegex.findall(buffer)
    
    if not len(matchList):
        raise Exception("Could not find licenses in [" + fname + "]")

    products = [ m[0] for m in matchList ]

    if 'oechem' not in products:
        return list() # can not run any examples

    if 'oedepict' not in products and 'grapheme' in products :
        products.remove('grapheme') # depict license is required for grapheme

    return products



def runOEExample(cmdline, prettyname, transcript):
    sys.stdout.write("Running %s... " % prettyname)
    sys.stdout.flush()

    # execute the process
    cmd = shlex.split(cmdline)
    proc = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE)
    stdout = proc.communicate()[0]
  
    warn = False
    fatal = False
    if stdout.find(b"Fatal:") != -1:
        fatal = True
    elif stdout.find(b"Warning:") != -1:
        warn = True

    # save the out put to a file
    writefile(transcript, cmdline, stdout)

    # print the results
    if proc.returncode:
        sys.stdout.write("Errors occurred.  Please send %s to support@eyesopen.com\n" % transcript)
        sys.stdout.write(stdout.decode('utf-8'))
        return 1
    else:
        if fatal:
            sys.stdout.write("Errors occurred.  Please send %s to support@eyesopen.com\n" % transcript)
            return 1
        if warn:
            sys.stdout.write("Warnings occurred.  Please send %s to support@eyesopen.com\n" % transcript)
            return 1
        else:
            sys.stdout.write("OK\n")
    return 0
