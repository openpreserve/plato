package pdfcm;

import java.util.List;
import com.sun.star.beans.PropertyValue;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.util.XCloseable;
import com.sun.star.lang.XComponent;
import com.sun.star.util.CloseVetoException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import ooo.connector.BootstrapSocketConnector;

/**
 * PDF conversion and merge class.
 * 
 * This class depends on the following software being installed and configured:
 * 
 *    -- Open Office version 2.3.0 or better
 *       (Previous 2.X versions may work, but they have not been tested.)
 *       Latest version of Open Office can be obtained at http://www.openoffice.org
 *       (Tested with 2.3.0, 2.4.0)
 * 
 *    -- Ghostscript version 8.6.1 or better
 *       (Previous versions may work if they support the command line options found below.)
 *       Latest version of Ghostscript can be obtained at http://pages.cs.wisc.edu/~ghost/
 *       (Tested with 8.6.1) 
 * 
 *    -- Bootstrap connector jarfile 
 *       The library distributed with this code already contains the bootstrap 
 *       connector.  In case it needs to be updated, or if you wish to leave a 
 *       "Thank-you", it was originally obtained at 
 *       http://user.services.openoffice.org/en/forum/viewtopic.php?f=44&t=2520
 *  
 * @author Gregory Edwin Graham
 */
public class PDFConvert {

    /**
     *  Returns true if the string array contains the given string.
     * 
     * @param arr An input array of strings
     * @param val A particular string to look for
     * @return true if the array contains an instance of the string, or false
     */
    public static boolean StringArrayContains(String[] arr, String val) {
        boolean retval = false;
        if (arr != null && val != null) {
            for (int i = 0; i < arr.length; i++) {
                if (val.contentEquals(arr[i])) {
                    retval = true;
                    break;
                }
            }
        }
        return retval;
    }

    protected String[] writerTypes = null;

    /**
     * Returns the file extensions that get mapped to the OpenOffice writer filter
     * 
     * @return The file extensions that get mapped to the OpenOffice writer filter
     */
    public String[] getWriterTypes() {
        return writerTypes;
    }
    
    /**
     * Sets the file extensions that get mapped to the OpenOffice writer filter
     * 
     * @param types The file extensions that get mapped to the OpenOffice writer filter
     */
    public void SetWriterTypes(String[] types) {
        writerTypes = types;                
    }
    
    protected String[] calcTypes = null;
    
    /**
     * Returns the file extensions that get mapped to the OpenOffice calc filter
     * 
     * @return The file extensions that get mapped to the OpenOffice calc filter
     */
    public String[] getCalcTypes() {
        return calcTypes;
    }
    
    /**
     * Sets the file extensions that get mapped to the OpenOffice calc filter
     * 
     * @param types The file extensions that get mapped to the OpenOffice calc filter
     */
    public void SetCalcTypes(String[] types) {
        calcTypes = types;                
    }
    
    
    protected String[] drawTypes = null;

    /**
     * Returns the file extensions that get mapped to the OpenOffice draw filter
     * 
     * @return The file extensions that get mapped to the OpenOffice draw filter
     */
    public String[] getDrawTypes() {
        return drawTypes;
    }
    
    /**
     * Sets the file extensions that get mapped to the OpenOffice draw filter
     * 
     * @param types The file extensions that get mapped to the OpenOffice draw filter
     */
    public void SetDrawTypes(String[] types) {
        drawTypes = types;                
    }
    
    
    protected String[] nativeTypes = null;

    /**
     * Returns the file extensions that get processed directly by Ghostscript
     * 
     * @return The file extensions that get processed directly by Ghostscript
     */
    public String[] getNativeTypes() {
        return nativeTypes;
    }
    
    /**
     * Sets the file extensions that get processed directly by Ghostscript
     * 
     * @param types The file extensions that get processed directly by Ghostscript
     */
    public void SetNativeTypes(String[] types) {
        nativeTypes = types;                
    }
    
    protected String ooLibPath = "";

    /**
     * Gets the folder containing the Open Office libraries
     * 
     * @return The folder containing the Open Office libraries
     */
    public String getOOLibPath() {
        return ooLibPath;
    }

    /**
     * Sets the folder containing the Open Office libraries
     * 
     * @param val The folder containing the Open Office libraries
     */
    public void setOOLibPath(String val) {
        ooLibPath = val;
    }
    protected String gsExePath = "";

    /**
     * Gets the folder containing the Ghostscript executable
     * 
     * @return The folder containing the Ghostscript executable
     */
    public String getGSExePath() {
        return gsExePath;
    }

    /**
     * Sets the folder containing the Ghostscript executable
     * 
     * @param val The folder containing the Ghostscript executable
     */
    public void setGSExePath(String val) {
        gsExePath = val;
    }
    protected String gsExeName = "";

    /**
     * Gets the name of the Ghostscript executable
     * 
     * @return The name of the Ghostscript executable
     */
    public String getGSExeName() {
        return gsExeName;
    }

    /**
     * Sets the name of the Ghostscript executable
     * 
     * @param val The name of the Ghostscript executable
     */
    public void setGSExeName(String val) {
        gsExeName = val;
    }
    protected String shellCommandStyle = null;

    /**
     * Gets the value of the shellCommandStyle.  Can be either "doubleQuoted"
     * for Windows-like or "escapeSpaces" for Unix-like.
     * 
     * @return The value of shellCommandStyle
     */
    public String getShellCommandStyle() {
        return shellCommandStyle;
    }

    /**
     * Sets the value of the shellCommandStyle.  Can be either "doubleQuoted"
     * for Windows-like or "escapeSpaces" for Unix-like.
     * 
     * @param scs The value of shellCommandStyle
     */
    public void setShellCommandStyle(String scs) {
        shellCommandStyle = scs;
    }
    protected boolean deleteOnFinish = false;

    /**
     * Gets the value of the deleteOnFinish option
     * 
     * @return The value of the deleteOnFinish option
     */
    public boolean getDeleteOnFinish() {
        return deleteOnFinish;
    }

    /**
     * Sets the value of the deleteOnFinish option
     * 
     * @param val The value of the deleteOnFinish option
     */
    public void setDeleteOnFinish(boolean val) {
        deleteOnFinish = val;
    }
    protected boolean doMerge = false;

    /**
     * Gets the value of the doMerge option
     * 
     * @return The value of the doMerge option
     */
    public boolean getDoMerge() {
        return doMerge;
    }

    /**
     * Sets the value of the doMerge option
     * 
     * @param val The value of the doMerge option
     */
    public void setDoMerge(boolean val) {
        doMerge = val;
    }
    private String outputFilename;

    /**
     * Gets the value of the output filename for the merge option
     * 
     * @return The value of the output filename for the merge option
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     * Sets the value of the output filename for the merge option
     * 
     * @param outputFile The value of the output filename for the merge option
     */
    public void setOutputFilename(String outputFile) {
        this.outputFilename = outputFile;
    }
    private String statusText = null;

    /**
     * Gets status text set during the course of a conversion operation
     * 
     * @return Status text set during the course of a conversion operation 
     */
    public String getStatusText() {
        return statusText;
    }
    private boolean isError = false;

    /**
     * Gets error status set during the course of a conversion operation
     * 
     * @return Error status set during the course of a conversion operation 
     */
    public boolean getIsError() {
        return isError;
    }

    /**
     * Clears error status and status text
     */
    public void ClearError() {
        statusText = null;
        isError = false;
    }

    /**
     * PDF Conversion and Merge method
     * 
     * @param inputFiles An array of input filenames to process
     * @return true if conversion succeeded
     */
    public boolean DoConvert(String[] inputFiles) {

        // Reset the errors and status
        ClearError();

        // Check configuration
        if (ooLibPath == null) {
            statusText = "Location of Open Office libraries not configured.";
            isError = true;
            return false;
        }
        if (gsExePath == null || gsExeName == null) {
            statusText = "Location of Ghostscript not configured.";
            isError = true;
            return false;
        }

        // Input array should not be null
        if (inputFiles == null) {
            statusText = "No input files.";
            isError = true;
            return false;
        }

        // Zero length array is not an error per se
        if (inputFiles.length == 0) {
            statusText = "No input files.";
            return false;
        }

        // Lack of output filename is an error
        if (outputFilename == null && doMerge) {
            statusText = "No output file specified.";
            isError = true;
            return false;
        }

        // Check for valid shell command style if -m is involved
        if (doMerge) {
            if (shellCommandStyle == null) {
                statusText = "Shell command style not configured.";
                isError = true;
                return false;
            } else if (!shellCommandStyle.contentEquals("doubleQuoted") &&
                    !shellCommandStyle.contentEquals("escapeSpaces")) {
                statusText = "Unknown shell command style: " + shellCommandStyle;
                isError = true;
                return false;
            }
        }

        // Declare Open Office components
        XComponentContext xContext = null;
        XMultiComponentFactory xMCF = null;
        XComponentLoader xComponentLoader = null;
        XStorable xStorable = null;
        XCloseable xCloseable = null;
        Object desktop = null;
        Object document = null;

        // File extension
        String ext;

        // Keep track of files for deletion
        List<String> filesToDelete = new ArrayList<String>();

        // Keep track of converted files
        List<String> convertedFiles = new ArrayList<String>();

        // Try to get reference to an Open Office process
        try {
            // Should use OO installation lib/programs directory on your system
            String ooLibFolder = ooLibPath;

            // Load the Open Office context
            xContext = BootstrapSocketConnector.bootstrap(ooLibFolder);

            // Load the Open Office object factory
            xMCF = xContext.getServiceManager();

            // Get a desktop instance
            desktop = xMCF.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", xContext);

            // Get a reference to the the desktop interface that can load files
            xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktop);

        } catch (Exception ex) {

            // Open Office error
            statusText = "Could not get usable OpenOffice: " + ex.toString();
            isError = true;
            return false;
        }

        // Keep track of status
        StringBuffer buf = new StringBuffer();

        // Loop through the input files
        for (int i = 0; i < inputFiles.length; i++) {

            // Check file
            if (inputFiles[i] == null) {
                buf.append("File " + i + " was null.");
                isError = true;
                continue;  // Skip to the next file
            }

            // Get the file extension
            ext = null;
            int lastDot = inputFiles[i].lastIndexOf('.');
            if (lastDot > 0) {
                ext = inputFiles[i].substring(lastDot).toLowerCase();
            }
            if (ext == null) {
                buf.append("File " + i + " was unrecognized by extension.");
                isError = true;
                continue;  // Skip to the next file
            }

            try {
                // Set the document opener to not display an OO window
                PropertyValue[] loaderValues = new PropertyValue[1];
                loaderValues[0] = new PropertyValue();
                loaderValues[0].Name = "Hidden";
                loaderValues[0].Value = new Boolean(true);

                // Convert file path to URL name format and escape spaces
                String docURL = "file:///" + inputFiles[i].replace(File.separatorChar, '/').replace(" ", "%20");
                lastDot = docURL.lastIndexOf('.');

                // If it is already PDF, add it to the list of files to "converted" files
                if (StringArrayContains(nativeTypes, ext)) {
                    convertedFiles.add(docURL);
                } else {
                    // Open the document in Open Office
                    document = xComponentLoader.loadComponentFromURL(
                            docURL, "_blank", 0, loaderValues);

                    // Get a reference to the document interface that can store files
                    xStorable = (XStorable) UnoRuntime.queryInterface(
                            XStorable.class, document);

                    // Set the arguments to save to pdf.
                    PropertyValue[] saveArgs = new PropertyValue[2];
                    saveArgs[0] = new PropertyValue();
                    saveArgs[0].Name = "Overwrite";
                    saveArgs[0].Value = new Boolean(true);

                    // Choose appropriate output filter
                    saveArgs[1] = new PropertyValue();
                    saveArgs[1].Name = "FilterName";
                    if (StringArrayContains(writerTypes, ext)) {
                        saveArgs[1].Value = "writer_pdf_Export";
                    } else if (StringArrayContains(calcTypes, ext)) {
                        saveArgs[1].Value = "calc_pdf_Export";
                    } else if (StringArrayContains(drawTypes, ext)) {
                        saveArgs[1].Value = "draw_pdf_Export";
                    } else {
                        buf.append("File " + i + " has unknown extension: " + ext);
                        isError = true;
                        continue;  // Skip to the next file
                    }

                    // The converted file will have the same name with a pdf extension
                    String sSaveUrl = docURL.substring(0, lastDot) + ".pdf";

                    // Save the file
                    xStorable.storeToURL(sSaveUrl, saveArgs);

                    // On success, add the converted filename to a list
                    convertedFiles.add(sSaveUrl);
                    if (deleteOnFinish) {
                        filesToDelete.add(inputFiles[i]);
                    }
                }
                buf.append("Processed file " + i + ". ");

            } catch (com.sun.star.io.IOException ooioException) {
                buf.append("Caught exception while processing file " + i + ": " +
                        ooioException.toString() + ". ");
                isError = true;
            } catch (com.sun.star.lang.IllegalArgumentException ooiaException) {
                buf.append("Caught exception while processing file " + i + ": " +
                        ooiaException.toString() + ". ");
                isError = true;
            } catch (Exception otherException) {
                buf.append("Caught exception while processing file " + i + ": " +
                        otherException.toString() + ". ");
                isError = true;
            } finally {
                // Make sure the file is closed before going to the next one
                if (document != null) {
                    // Get a reference to the document interface that can close a file
                    xCloseable = (XCloseable) UnoRuntime.queryInterface(
                            XCloseable.class, document);

                    // Try to close it or explicitly dispose it
                    // See http://doc.services.openoffice.org/wiki/Documentation/DevGuide/OfficeDev/Closing_Documents
                    if (xCloseable != null) {
                        try {
                            xCloseable.close(false);
                        } catch (com.sun.star.util.CloseVetoException ex) {
                            XComponent xComp = (XComponent) UnoRuntime.queryInterface(
                                    XComponent.class, document);
                            xComp.dispose();
                        }
                    } else {
                        XComponent xComp = (XComponent) UnoRuntime.queryInterface(
                                XComponent.class, document);
                        xComp.dispose();
                    }
                }
                document = null;   // Javanauts, please pardon my CSharpery
            }
        }

        if (doMerge) {
            // configure the shell command build
            boolean doQuotes = shellCommandStyle.contentEquals("doubleQuoted");
            boolean doEscapes = shellCommandStyle.contentEquals("escapeSpaces");
            // Do the merge
            StringBuffer cmd = new StringBuffer();
            if (doQuotes) {
                cmd.append("\"");
            }
            if (doEscapes) {
                cmd.append(gsExePath.replace(" ", "\\ ")).append(File.separatorChar).append(gsExeName);
            } else {
                cmd.append(gsExePath).append(File.separatorChar).append(gsExeName);
            }
            if (doQuotes) {
                cmd.append("\"");
            }
            cmd.append(" -q").append(" -dNOPAUSE").append(" -dBATCH").append(" -sDEVICE=pdfwrite").append(" -sOUTPUTFILE=");
            if (doQuotes) {
                cmd.append("\"");
            }
            if (doEscapes) {
                cmd.append(outputFilename.replace(" ", "\\ "));
            } else {
                cmd.append(outputFilename);
            }
            if (doQuotes) {
                cmd.append("\"");
            }
            cmd.append(" ");

            // Loop over converted files
            for (int i = 0; i < convertedFiles.size(); i++) {
                String fileToAdd = convertedFiles.get(i).substring(8).replace("%20", " ").replace('/', File.separatorChar);
                if (doQuotes) {
                    cmd.append("\"");
                }
                if (doEscapes) {
                    cmd.append(fileToAdd.replace(" ", "\\ "));
                } else {
                    cmd.append(fileToAdd);
                }
                if (doQuotes) {
                    cmd.append("\"");
                }
                cmd.append(" ");
                if (deleteOnFinish) {
                    filesToDelete.add((fileToAdd));
                }
            }

            // Merge!
            if (!isError) {
                try {
                    // Execute the command
                    Process mProc = Runtime.getRuntime().exec(cmd.toString());

                    // Voodoo - In order to wait for an external process, you
                    // have to handle its stdout(getInputStream) and stderr (getErrorStream)
                    // I'm just going to close them as I'm only interested in if it succeeded or not
                    InputStream iStr = mProc.getInputStream();
                    iStr.close();
                    InputStream eStr = mProc.getErrorStream();
                    eStr.close();

                    // Now wait
                    int exCode = mProc.waitFor();
                    if (exCode == 0) {
                        buf.append("Merge succeeded: exit code was zero.");
                    } else {
                        isError = true;
                        buf.append("Merge failed: exit code was " + exCode);
                    }

                } catch (java.io.IOException ex) {
                    buf.append("Merge failed: " + ex.toString());
                    isError = true;
                    statusText = buf.toString();
                    return false;
                } catch (java.lang.InterruptedException ex) {
                    buf.append("Merge interrupted: " + ex.toString());
                    isError = true;
                    statusText = buf.toString();
                    return false;
                }
            }
        }

        // Delete the converted files
        if (deleteOnFinish) {
            for (int i = 0; i < filesToDelete.size(); i++) {
                File dFile = new File(filesToDelete.get(i));
                if (dFile.exists()) {
                    dFile.delete();
                }
            }
        }
        statusText = buf.toString();
        return !isError;
    }
}
