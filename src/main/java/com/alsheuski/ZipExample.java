package com.alsheuski;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;

public class ZipExample {
  public static void main(String[] args) {
    String zipFilePath = "output.zip";

    String password = "yourpassword";

    String fileToZipPath = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/pom.xml";

    try {
      ZipFile zipFile = new ZipFile(zipFilePath, password.toCharArray());

      ZipParameters zipParameters = new ZipParameters();
      zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); // Set compression method
      zipParameters.setEncryptFiles(true);
      zipParameters.setEncryptionMethod(EncryptionMethod.AES); // Set encryption method

      zipFile.addFile(new File(fileToZipPath), zipParameters);

      System.out.println("Zip file created successfully with password protection.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
