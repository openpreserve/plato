/*
 * PDFCM
 *
 * PDF Convert and Merge
 * Uses Open Office 2.3 or better to convert supported file types to PDF.
 *
 * Gregory Edwin Graham
 * May 16, 2008
 */
package pdfcm;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Main class for PDFCM - parses parameters and drives program.
 * 
 * @author Gregory Edwin Graham
 */
public class Main {

    /**
     * Returns a String array of trimmed values from an input string and separator 
     * 
     * @param input The String array to split and trim
     * @param separator The separator on which to run the String split
     * @return A String array of trimmed values from an input string and separator
     */
    protected static String[] GetTrimmedArray(String input, String separator) {
        if ( input == null || separator == null ) return null;
        String[] tmp = input.split(separator);
        String[] retval = new String[tmp.length];
        for ( int i = 0; i < tmp.length; i++ ) {
            retval[i] = tmp[i].trim();
        }
        return retval;
    }
    
    /**
     * Prints out a usage string and exits.
     * 
     * @param exVal Exit value to return to the OS.
     */
    public static void Usage(int exVal) {
        System.out.println();
        System.out.println("Usage: java pdfcm.Main [-m mergeFile] [-d] file1 [file2 [file3...]]");
        System.out.println("Usage (jarfile): java -jar pdfcm.jar [-m mergeFile] [-d] file1 [file2 [file3...]]");
        System.out.println();
        System.out.println("    Converts all given input files to PDF.  Output filenames have the same base");
        System.out.println("    filenames as the input files and the extension \"pdf\".  PDF files on the");
        System.out.println("    input are not processed.");
        System.out.println();
        System.out.println("    INPUT OPTIONS");
        System.out.println();
        System.out.println("    -m mergeFile");
        System.out.println("        Causes converted PDF files and existing PDF (unprocessed) files on the");
        System.out.println("        input to be merged into a single PDF file given by mergeFile as a final");
        System.out.println("        step.");
        System.out.println();
        System.out.println("    -d");
        System.out.println("        Causes input files to be removed after successful processing.  When");
        System.out.println("        used in conjunction with the -m option, all intermediate files as well");
        System.out.println("         as any PDF files on the input are removed after successful processing.");
        System.out.println();
        System.out.println("    In case of a name collision between an input filename and the merge filename,");
        System.out.println("    in the case that the -d option is given, the collision will be resolved.  If");
        System.out.println("    the -d option is not given, then an error will be generated to prevent");
        System.out.println("    accidental overwrite of a file.");
        System.exit(exVal);
    }

    /**
     * Prints a custom error message to stdout and exits with the standard 
     * usage message and given exit code.
     * 
     * @param errm Custom error message to pring to stdout
     * @param exVal Exit value to return to the OS.
     */
    public static void Usage(String errm, int exVal) {
        System.out.println();
        System.out.println(errm);
        Usage(exVal);
    }

    /**
     * Assuming the two inputs are filenames, checks the base parts of each
     * filename for equality.
     * 
     * @param f1 The first filename to compare
     * @param f2 The second filename to compare
     * @return true if the base parts of each filename are equal, or false if not or if both inputs are null.  
     */
    protected static boolean CompareFilenameBase(String f1, String f2) {
        if (f1 == null || f2 == null) {
            return false;
        }
        int lastDot1 = f1.lastIndexOf('.');
        if (lastDot1 < 0) {
            return false;
        }
        int lastDot2 = f2.lastIndexOf('.');
        if (lastDot2 < 0) {
            return false;
        }
        if (lastDot1 != lastDot2) {
            return false;
        }
        if (lastDot1 == 0) {
            return true;
        }
        return (f1.substring(0, lastDot1).contentEquals(f2.substring(0, lastDot2)));
    }

    /**
     * Main method
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        // Sanity check the command line input
        if (args.length == 0) {
            Usage(0);
        }

        // Get paths of Open Office, Ghostscript and file extension map from the config file.    
        String ooLibPath = null;
        String gsExePath = null;
        String gsExeName = null; 
        String shellCommandStyle = null;
        String wTypes = null;
        String cTypes = null;
        String dTypes = null;
        String nTypes = null;
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            InputStream is = loader.getResourceAsStream("pdfcm/config.properties");
            Properties props = new Properties();
            props.load(is);
            is.close();
            ooLibPath = props.getProperty("ooLibPath");
            gsExePath = props.getProperty("gsExePath");
            gsExeName = props.getProperty("gsExeName");
            shellCommandStyle = props.getProperty("shellCommandStyle");
            wTypes = props.getProperty("writerTypes");
            cTypes = props.getProperty("calcTypes");
            dTypes = props.getProperty("drawTypes");
            nTypes = props.getProperty("nativeTypes");
        } catch (Exception ex) {
            System.out.println("Could not get configuration from config.properties: " +
                    ex.getMessage());
        }

        // Instantiate the converter class
        PDFConvert pdfc = new PDFConvert();

        // Configure the converter
        pdfc.setOOLibPath(ooLibPath);
        pdfc.setGSExePath(gsExePath);
        pdfc.setGSExeName(gsExeName);
        pdfc.setShellCommandStyle(shellCommandStyle);
        
        // Configue file extensions
        pdfc.SetWriterTypes(GetTrimmedArray(wTypes,","));
        pdfc.SetCalcTypes(GetTrimmedArray(cTypes,","));
        pdfc.SetDrawTypes(GetTrimmedArray(dTypes,","));
        pdfc.SetNativeTypes(GetTrimmedArray(nTypes,","));        
        
        // Set options from the command line
        pdfc.setDeleteOnFinish(false);
        pdfc.setDoMerge(false);

        // This will hold the real output filename in case a merge filename 
        // collides with a processed input filename
        String oFileName = null;

        // Process arguments
        int iArg = 0;
        List<String> rawFileNames = new ArrayList<String>();
        while (iArg < args.length) {
            // Is it a merge?
            if (args[iArg].contentEquals("-m")) {
                // Reject a trailing "-m"
                if (iArg + 1 > args.length) {
                    Usage(-1);
                }
                // Reject multiple "-m"
                if (pdfc.getDoMerge()) {
                    Usage("Use the \"-m\" option only once.", -1);
                }
                // Set merge parameters
                pdfc.setDoMerge(true);
                pdfc.setOutputFilename(args[iArg + 1]);
                oFileName = args[iArg + 1];
                iArg += 2;
            } else // Is it a delete-on-success?
            if (args[iArg].contentEquals("-d")) {

                pdfc.setDeleteOnFinish(true);
                iArg++;
            } else // Treat as a filename
            {
                rawFileNames.add(args[iArg]);
                iArg++;
            }
        }

        // Reject if no input files were given
        if (rawFileNames.size() == 0) {
            Usage("Please enter at least one input file.", -1);
        }

        // Check existence of input files
        boolean isMissingFile = false;
        String[] inputFiles = new String[rawFileNames.size()];
        for (int i = 0; i < rawFileNames.size(); i++) {
            File file = new File(rawFileNames.get(i));
            if (file.exists()) {
                inputFiles[i] = file.getAbsolutePath();
            } else {
                System.out.println("Input file not found: " + file.getAbsolutePath());
                isMissingFile = true;
            }
        }

        // If any files are missing, quit.
        if (isMissingFile) {
            Usage(-1);
        }

        // Check for collisions between filenames of a merged file and an 
        // intermediate file and temproarily change the output filename. 
        boolean isCollision = false;
        if (pdfc.getDoMerge() && pdfc.getOutputFilename() != null) {
            String testName = pdfc.getOutputFilename();
            for (int i = 0; i < rawFileNames.size(); i++) {
                if (CompareFilenameBase(rawFileNames.get(i), testName)) {
                    isCollision = true;
                    break;
                }
            }
        }

        if (isCollision) {
            if (!pdfc.getDeleteOnFinish()) {
                // If -d is not given, then processing would overwrite a 
                // file the user might expect to be there afterwards.
                Usage("Merge filename conflicts with an intermediate filename: " + oFileName +                         
                        "\nPlease choose another filename or use the -d option.", -1);
            } else {
                // Name collision is OK if -d was also given since the user expects the 
                // intermediate file to be removed anyway. So create a temporary unique 
                // filename for the output
                StringBuffer uniquifier = new StringBuffer();
                for (int i = 0; i < rawFileNames.size(); i++) {
                    uniquifier.append((rawFileNames.get(i).charAt(i) == 'a' ? 'b' : 'a'));
                }
                pdfc.setOutputFilename(uniquifier.toString() + oFileName);
            }
        }

        // Phew!  We made it...
        boolean done = pdfc.DoConvert(inputFiles);

        // Check and report any errors
        if (!done) {
            System.out.println("Conversion failed: " + pdfc.getStatusText());
            System.exit(-1);
        } else {
            // Rename the merge file if it was uniquified to avoid name collision
            if (pdfc.getDoMerge()) {
                File orig = new File(pdfc.getOutputFilename());
                if (orig.exists()) {
                    File dest = new File(oFileName);
                    orig.renameTo(dest);
                    System.out.println("Conversion succeeded.");
                    System.exit(0);
                } else {
                    // Check and report any errors
                    System.out.println("Conversion and merge apparently succeeded, but output file was not found: " +
                            pdfc.getOutputFilename());
                    System.exit(-1);
                }
            } else {
                System.out.println("Conversion succeeded.");
                System.exit(0);
            }
        }
    }
}


