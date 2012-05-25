/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.tuwien.minimee.migration.testprogram.acme;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * General Encoder for all the Acme provided Encoder: gif, jpeg, ppm
 * @author riccardo
 *
 */
public class AcmeImageEncoder {
    public static void main(String a[]) {
        try {
            int inputStart = 0;
            String actionToDo = "toGrayScaleJPG";
            Integer loops = 1;
            if (a[inputStart].compareTo("-a") == 0) { // read action
                actionToDo = a[inputStart + 1];
                inputStart += 2;
            } else if (a[inputStart].compareTo("-h") == 0) { // read loops
                System.out.println("Usage: -a <ACTION> input_file output_file");
                return;
            }
            System.err.println("Looking for inputStart" + a[inputStart + 0]);
            File inputImage = new File(a[inputStart + 0]);
            BufferedImage image = ImageIO.read(inputImage);

            System.err.println("Writing to " + a[inputStart + 1]);
            for (int i = 0; i < loops; i++) {
                if (actionToDo.compareTo("jpg") == 0) {
                    JpegEncoder jpgEnc = new JpegEncoder(image,
                            new FileOutputStream(a[inputStart + 1]));
                    jpgEnc.encode();
                } else if (actionToDo.compareTo("gif") == 0) {
                    GifEncoder gEnc = new GifEncoder(image,
                            new FileOutputStream(a[inputStart + 1]));
                    gEnc.encode();
                } else if (actionToDo.compareTo("ppm") == 0) {
                    PpmEncoder ppmEnc = new PpmEncoder(image,
                            new FileOutputStream(a[inputStart + 1]));
                    ppmEnc.encode();
                } else {
                    System.err.println("action not recognized");
                }
                System.err.println("Image has been succesfully converted into "
                        + a[inputStart + 1]);
                //                                      
                // ImageIO.write(image, "jpg", new File(a[inputStart+1] +
                // ".jpg"));
                // ImageIO.write(image, "png", new File(a[inputStart+1] +
                // ".png"));
                //                                      
                // BufferedImage tmpImage=convertToGrayScale(image);
                // ImageIO.write(tmpImage, "png", new File(a[1] + "_GREY.png"));
                //                                      
                // tmpImage=convertToARGBScale(image);
                // ImageIO.write(tmpImage, "png", new File(a[1] + "_ARGB.png"));
                //                                      
                // tmpImage=convertToBGRScale(image);
                // ImageIO.write(tmpImage, "png", new File(a[1] + "_BGR.png"));
                //                                      
                // // tmpImage=convertToSCALE_AREA_AVERAGING(image);
                // // ImageIO.write(tmpImage, "png", new File(a[1] +
                // "_averaging.png"));
                // //
                // tmpImage=convertToTranslucentImage(image);
                // ImageIO.write(tmpImage, "png", new File(a[1] +
                // "_translucent.png"));
                //                                      
                // System.out
                // .println("Your image has been converted successfully "
                // + (i+1) + " times");
            }
        } catch (FileNotFoundException e) {
            System.out
                    .println("Usage: -a <ACTION> -l <times_to_loop> -o <outputfile> input_file output_file");
            System.out.println("Error:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out
                    .println("Usage: -a <ACTION> -l <times_to_loop> -o <outputfile> input_file output_file");
            System.out.println("Error:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out
                    .println("Usage: -a <ACTION> -l <times_to_loop> -o <outputfile> input_file output_file");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
