/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package at.tuwien.minimee.migration.testprogram;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageConverter {
    public static void main(String a[]) {
        
        ImageConverter imgConv=new ImageConverter();
        try {
            int inputStart = 0;
            // System.out.println(a[0]+"-"+a[1]+"-"+a[2]+"-");
            String actionToDo = "toGrayScaleJPG";
            Integer loops = 1;
            String output="";
            if (a[inputStart].compareTo("-a") == 0) { // read action
                actionToDo = a[inputStart + 1];
                inputStart += 2;
            } else if (a[inputStart].compareTo("-l") == 0) { // read loops
                loops = Integer.parseInt(a[inputStart + 1]);
                inputStart += 2;
            } else if (a[inputStart].compareTo("-h") == 0) { // read loops
                System.out
                        .println("Usage: -a <ACTION> -l <times_to_loop> -o <outputfile> input_file output_file");
                return;
            }

            System.err.println("Looking for inputStart"+a[inputStart+0]);
            File inputImage = new File(a[inputStart + 0]);
            BufferedImage image = ImageIO.read(inputImage);

            System.err.println("Writing to " + a[inputStart + 1]);
            for (int i = 0; i < loops; i++) {
                if (actionToDo.compareTo("toGrayScaleJPG") == 0) {
                    System.out.println("entered Greyscale");
                    image = imgConv.convertToGrayScale(image);
                    System.out.println("Image " + image);
                    File tmpFile = new File(a[inputStart + 1]);

                    if (tmpFile.exists())
                        tmpFile.delete();

                    tmpFile.createNewFile();

                    ImageIO.write(image, "jpg", new File(a[inputStart + 1]));
                    System.out.println("Image WRITTEN IN  " + a[inputStart + 1]);
                } else if (actionToDo.compareTo("pdf") == 0) {
                   // imgConv.convertToPDF(a[inputStart + 0], a[inputStart + 1]);          
                } else {
                    ImageIO.write(image, actionToDo, new File(a[inputStart + 1]));
                }
                System.err.println("Image has been succesfully converted into " + a[inputStart + 1]);
               
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
    
//    private  void convertToPDF(String input, String output) throws DocumentException, MalformedURLException, IOException {
//        Document document=new Document();
//        PdfWriter.getInstance(document,new FileOutputStream(output));
//        document.open();
//        com.lowagie.text.Image image = com.lowagie.text.Image.getInstance (input);
//        //2480x3508
//        int docWidthPixel=700;
//        int docHeightPixel=500;
//        
//        float scaledHeight=image.scaledHeight();
//        float scaledWidth=image.scaledWidth();
//        
//        if(scaledWidth>scaledHeight)
//            image.setRotationDegrees(90); 
//        
//        if(image.scaledWidth()>docWidthPixel)
//            scaledWidth=docWidthPixel;
//        
//        if(image.scaledHeight()>docHeightPixel)
//            scaledHeight=docHeightPixel;
//        
//        image.scaleToFit(scaledHeight, scaledWidth);
//        document.add(image);
//        document.close();
//       
//    }

    private BufferedImage convertToGrayScale(Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int type = java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage grayImage = new java.awt.image.BufferedImage(w, h, type);
        Graphics g = grayImage.getGraphics();
        g.drawImage(image, 4, 5, null);
        g.dispose();
        return grayImage;
    }

    private BufferedImage convertToBGRScale(Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int type = java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage bgrImage = new java.awt.image.BufferedImage(w, h, type);
        Graphics g = bgrImage.getGraphics();
        g.drawImage(image, 4, 5, null);
        g.dispose();
        return bgrImage;
    }

    private static BufferedImage convertToARGBScale(Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int type = java.awt.image.BufferedImage.TYPE_INT_ARGB;
        BufferedImage bgrImage = new java.awt.image.BufferedImage(w, h, type);
        Graphics g = bgrImage.getGraphics();
        g.drawImage(image, 4, 5, null);
        g.dispose();
        return bgrImage;
    }

    private static BufferedImage convertToRGBScale(Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int type = java.awt.image.BufferedImage.TYPE_INT_RGB;
        BufferedImage bgrImage = new java.awt.image.BufferedImage(w, h, type);
        Graphics g = bgrImage.getGraphics();
        g.drawImage(image, 4, 5, null);
        g.dispose();
        return bgrImage;
    }

    private static BufferedImage convertToTranslucentImage(Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int type = java.awt.image.BufferedImage.TRANSLUCENT;
        BufferedImage bgrImage = new java.awt.image.BufferedImage(w, h, type);
        Graphics g = bgrImage.getGraphics();
        g.drawImage(image, 4, 5, null);
        g.dispose();
        return bgrImage;
    }

    private static BufferedImage convertToSCALE_AREA_AVERAGING(Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int type = java.awt.image.BufferedImage.SCALE_AREA_AVERAGING;
        BufferedImage bgrImage = new java.awt.image.BufferedImage(w, h, type);
        Graphics g = bgrImage.getGraphics();
        g.drawImage(image, 4, 5, null);
        g.dispose();
        return bgrImage;
    }

}
