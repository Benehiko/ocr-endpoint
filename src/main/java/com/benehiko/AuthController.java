package com.benehiko;

import com.Pojo.DeviceAuth;
import com.Pojo.WebsiteAuth;
import com.company.acs.AcsApplication;
import com.company.acs.acs.acs.device.Device;
import com.company.acs.acs.acs.device.DeviceManager;
import com.company.acs.acs.acs.user.User;
import com.company.acs.acs.acs.user.UserManager;
import com.company.acs.acs.acs.userauth.UserAuth;
import com.company.acs.acs.acs.userauth.UserAuthImpl;
import com.company.acs.acs.acs.userauth.UserAuthManager;
import com.company.acs.acs.acs.userauth2.UserAuth2;
import com.company.acs.acs.acs.userauth2.UserAuth2Impl;
import com.company.acs.acs.acs.userauth2.UserAuth2Manager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.OptionalInt;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAuthManager userAuthManager;
    private final UserAuth2Manager userAuth2Manager;
    private final DeviceManager deviceManager;
    private final UserManager userManager;

    public AuthController(AcsApplication app) {
        userAuthManager = app.getOrThrow(UserAuthManager.class);
        userAuth2Manager = app.getOrThrow(UserAuth2Manager.class);
        deviceManager = app.getOrThrow(DeviceManager.class);
        userManager = app.getOrThrow(UserManager.class);
    }

    @PostMapping(path = "register/mobile", consumes = "application/json", produces = "application/json")
    @ResponseBody
    boolean registerMobile(@RequestBody DeviceAuth deviceAuth, Device device) {
        if (deviceAuth != null) {
            String salt = getSalt();
            byte[] hash = genHash(deviceAuth.getPassword(), salt);
            OptionalInt userID = device.getDeviceUser();
            if (userID.isPresent()) {
                User user = userManager.stream().filter(User.USER_ID.equal(userID.getAsInt())).findFirst().orElse(null);
                if (user != null) {
                    String h = Arrays.toString(hash);
                    UserAuth2 userAuth2 = new UserAuth2Impl().setAuthUserId(user.getUserId()).setHash(h).setSalt(salt);
                    try {
                        userAuth2Manager.persist(userAuth2);
                        return true;
                    } catch (SpeedmentException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return false;
    }

    @PostMapping(path = "register/website", consumes = "application/json", produces = "application/json")
    @ResponseBody
    boolean registerWebsite(@RequestBody WebsiteAuth websiteAuth) {
        if (websiteAuth != null) {
            User user = userManager.stream().filter(User.USER_NAME.equal(websiteAuth.getUsername())).findFirst().orElse(null);
            if (user != null) {
                String salt = getSalt();
                byte[] hash = genHash(websiteAuth.getPassword(), salt);
                String h = Arrays.toString(hash);
                UserAuth userAuth = new UserAuthImpl().setHash(h).setUsername(user.getUserName()).setSalt(salt);
                try {
                    userAuthManager.persist(userAuth);
                    return true;
                } catch (SpeedmentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    @PostMapping(path = "login/mobile", consumes = "application/json", produces = "application/json")
    @ResponseBody
    boolean authMobile(@RequestBody DeviceAuth deviceAuth) {
        if (deviceAuth != null) {
            String mac = deviceAuth.getMac();
            String password = deviceAuth.getPassword();

            Device tmpDevice = deviceManager.stream().filter(Device.MAC.equal(mac)).findFirst().orElse(null);
            if (tmpDevice != null) {
                OptionalInt optionalInt = tmpDevice.getDeviceUser();
                if (optionalInt.isPresent()) {
                    User tmpUser = userManager.stream().filter(User.USER_ID.equal(optionalInt.getAsInt())).findFirst().orElse(null);
                    if (tmpUser != null) {
                        UserAuth2 userAuth2 = userAuth2Manager.stream().filter(UserAuth2.AUTH_USER_ID.equal(tmpUser.getUserId())).findFirst().orElse(null);
                        if (userAuth2 != null) {
                            String hash = userAuth2.getHash();
                            String salt = userAuth2.getSalt();
                            String pass = password.concat(salt);
                            return checkPassword(pass, hash, salt);
                        }
                    }

                }
            }
        }
        return false;
    }

    @PostMapping(path = "login/website", consumes = "application/json", produces = "application/json")
    @ResponseBody
    boolean authWebsite(@RequestBody WebsiteAuth websiteAuth) {
        UserAuth auth = userAuthManager.stream().filter(UserAuth.USERNAME.equal(websiteAuth.getUsername())).findFirst().orElse(null);
        if (auth != null) {
            String hash = auth.getHash();
            String salt = auth.getHash();
            String pass = websiteAuth.getPassword().concat(salt);
            return checkPassword(pass, hash, salt);
        }
        return false;
    }

    private byte[] genHash(String password, String salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 1, 256);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e);
        }
        return new byte[0];
    }

    private String getSalt() {
        SecureRandom random = new SecureRandom();
        byte seed[] = random.generateSeed(20);
        return Arrays.toString(seed);
    }

    private boolean checkPassword(String password, String hash, String salt) {
        byte[] h = genHash(password, salt);
        return Arrays.equals(h, hash.getBytes(StandardCharsets.UTF_8));
    }
}
