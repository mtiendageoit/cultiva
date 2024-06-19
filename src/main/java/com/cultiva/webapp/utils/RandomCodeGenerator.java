package com.cultiva.webapp.utils;

import java.util.*;

public class RandomCodeGenerator {

  public static String generateNumericRandomCode(int length) {
    Random random = new Random();
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      code.append(random.nextInt(10));
    }
    return code.toString();
  }

  public static String generateUUIDCode() {
    return UUID.randomUUID().toString();
  }

}
