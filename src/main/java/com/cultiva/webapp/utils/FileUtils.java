package com.cultiva.webapp.utils;

import java.io.*;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

import org.springframework.web.multipart.MultipartFile;

import com.cultiva.webapp.exception.BaseException;

public class FileUtils {
  
  public static String randomFileName(MultipartFile file) {
    String ext = FilenameUtils.getExtension(file.getOriginalFilename());
    return RandomCodeGenerator.generateUUIDCode() + "." + ext;
  }

  public static File fromMultiPartFile(MultipartFile file) throws IOException {
    File convFile = new File(file.getOriginalFilename());
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(file.getBytes());
    fos.close();
    return convFile;
  }

  public static void validateFile(MultipartFile file, List<String> validExts, int maxFileSizeMB) {
    validateFileExtension(file, validExts);
    validateFileSizeMB(file, maxFileSizeMB);
  }

  public static void validateFileExtension(MultipartFile file, List<String> validExts) {
    String ext = FilenameUtils.getExtension(file.getOriginalFilename());
    boolean validExt = validExts.contains(ext.toUpperCase());
    if (!validExt) {
      throw new BaseException("file-not-allowed", "File type not allowed");
    }
  }

  public static void validateFileSizeMB(MultipartFile file, int maxFileSizeMB) {
    long maxBytesSize = maxFileSizeMB * 1024 * 1024;

    long fileSize = file.getSize();
    boolean valid = fileSize <= maxBytesSize;

    if (!valid) {
      throw new BaseException("file-size-exceeded", "File size is exceeded, max allowed " + maxFileSizeMB + " MB");
    }
  }

}
