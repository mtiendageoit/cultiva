package com.cultiva.webapp.profile;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cultiva.webapp.config.AppConfig;
import com.cultiva.webapp.google.GoogleCloudClient;
import com.cultiva.webapp.security.UserPrincipal;
import com.cultiva.webapp.user.*;
import com.cultiva.webapp.utils.FileUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfileService {
  private static int AVATAR_MAX_FILE_SIZE_MB = 1;
  private static List<String> AVATAR_EXTS = Arrays.asList("PNG", "GIF", "JPG", "JPEG");

  private final AppConfig config;
  private final UserRepository userRepository;
  private final GoogleCloudClient googleCloudClient;

  public Profile profile(UserPrincipal principal) {
    User user = userRepository.findById(principal.getId()).get();

    return Profile.fromUser(user);
  }

  public Profile updateProfileGeneral(Profile input, UserPrincipal principal) {
    User user = userRepository.findById(principal.getId()).get();

    user.setFullname(input.getFullname());
    user.setPhone(input.getPhone());

    user = userRepository.save(user);

    return Profile.fromUser(user);
  }

  public String updateAvatar(MultipartFile file, UserPrincipal principal) {
    FileUtils.validateFile(file, AVATAR_EXTS, AVATAR_MAX_FILE_SIZE_MB);
    User user = userRepository.findById(principal.getId()).get();
    String filename = FileUtils.randomFileName(file);
    googleCloudClient.updateUserAvatar(file, filename);
    String avatar = config.getGoogleCloudStorageImagesUrl() + "/avatars/" + filename;
    user.setAvatar(avatar);
    userRepository.save(user);
    principal.setAvatar(avatar);
    return avatar;
  }

}
