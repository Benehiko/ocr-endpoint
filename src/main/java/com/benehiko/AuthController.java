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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Optional;
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
    boolean registerMobile(@RequestBody DeviceAuth deviceAuth, Device device) throws UnsupportedEncodingException {
        if (deviceAuth != null) {
            byte[] salt = getSalt();
            byte[] hash = genHash(deviceAuth.getPassword().toCharArray(), salt);
            OptionalInt userID = device.getDeviceUser();
            if (userID.isPresent()) {
                Optional<User> user = userManager.stream().filter(User.USER_ID.equal(userID.getAsInt())).findFirst();
                if (user.isPresent()) {
                    String h = new String(hash, "ISO-8859-1");
                    UserAuth2 userAuth2 = new UserAuth2Impl().setAuthUserId(user.get().getUserId()).setHash(h).setSalt(new String(salt, "ISO-8859-1"));
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
    boolean registerWebsite(@RequestBody WebsiteAuth websiteAuth) throws UnsupportedEncodingException {
        if (websiteAuth != null) {
            String username = websiteAuth.getUsername();
            Optional<User> user = userManager.stream().filter(User.USER_NAME.equalIgnoreCase(username)).findFirst();
            if (user.isPresent()) {
                byte[] salt = getSalt();
                byte[] hash = genHash(websiteAuth.getPassword().toCharArray(), salt);
                String h = new String(hash, "ISO-8859-1");
                UserAuth userAuth = new UserAuthImpl().setHash(h).setUsername(user.get().getUserName()).setSalt(new String(salt, "ISO-8859-1"));
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
    boolean authMobile(@RequestBody DeviceAuth deviceAuth) throws UnsupportedEncodingException {
        if (deviceAuth != null) {
            String mac = deviceAuth.getMac();
            String password = deviceAuth.getPassword();

            Optional<Device> tmpDevice = deviceManager.stream().filter(Device.MAC.equal(mac)).findFirst();
            if (tmpDevice.isPresent()) {
                OptionalInt optionalInt = tmpDevice.get().getDeviceUser();
                if (optionalInt.isPresent()) {
                    User tmpUser = userManager.stream().filter(User.USER_ID.equal(optionalInt.getAsInt())).findFirst().orElse(null);
                    if (tmpUser != null) {
                        UserAuth2 userAuth2 = userAuth2Manager.stream().filter(UserAuth2.AUTH_USER_ID.equal(tmpUser.getUserId())).findFirst().orElse(null);
                        if (userAuth2 != null) {
                            byte[] hash = userAuth2.getHash().getBytes("ISO-8859-1");
                            byte[] salt = userAuth2.getHash().getBytes("ISO-8859-1");
                            char[] pass = (deviceAuth.getPassword() + new String(salt, "ISO-8859-1")).toCharArray();
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
        Optional<UserAuth> auth = userAuthManager.stream().filter(UserAuth.USERNAME.equal(websiteAuth.getUsername())).findFirst();
        if (auth.isPresent()) {
            byte[] hash = auth.get().getHash().getBytes();
            byte[] salt = auth.get().getHash().getBytes();
            char[] pass = (websiteAuth.getPassword() + Arrays.toString(salt)).toCharArray();
            return checkPassword(pass, hash, salt);
        }
        return false;
    }

    private byte[] genHash(char[] password, byte[] salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, 1, 256);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e);
        }
        return new byte[0];
    }

    private byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        return random.generateSeed(20);
    }

    private boolean checkPassword(char[] password, byte[] hash, byte[] salt) {
        byte[] h = genHash(password, salt);
        return Arrays.equals(h, hash);
    }
}
