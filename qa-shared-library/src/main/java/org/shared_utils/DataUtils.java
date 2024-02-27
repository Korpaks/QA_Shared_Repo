package org.shared_utils;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.io.Zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DataUtils {


    public static File file;
    public static FileInputStream fileInputStream;
    public static PDDocument document;


    public static String readPDF_File(String fileName) throws IOException {
        try {
            file = new File(System.getProperty("user.dir") + "\\Downloads/" + fileName);
            fileInputStream = new FileInputStream(file);
            document = PDDocument.load(fileInputStream);
            return new PDFTextStripper().getText(document);

        }catch (IOException e){
            e.printStackTrace();

        }finally {
            fileInputStream.close();
            document.close();
        }
       return null;
    }



    public static void unZipFile (String fileName) throws IOException {

        Zip.unzip(new FileInputStream(System.getProperty("user.dir") + "\\Downloads/" + fileName), new File(System.getProperty("user.dir") + "\\Downloads/"));
    }
}
