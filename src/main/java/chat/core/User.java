package chat.core;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Timestamp;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

public class User {

    //Password stuff
//    private static final int PASSWORD_ITERATIONS = 65536;
//    private static final int PASSWORD_LENGTH = 256; // 32 bytes
//    private static final SecretKeyFactory PASSWORD_FACTORY;
//    static {
//        SecretKeyFactory factory = null;
//        try {
//            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        PASSWORD_FACTORY = factory;
//    }
//    private final byte[] salt;
//    private final byte[] secret;



    private final int id;
    private final String name;
    private final LocalDateTime date;

    public User(int id, String name, LocalDateTime date) {
        this.id = id;
        this.name = name;
        this.date = date;
//        this.salt = salt;
//        this.secret = secret;
    }

    public User(String name, LocalDateTime date) {
        this.id = -1;
        this.name = name;
        this.date = date;
//        this.salt = salt;
//        this.secret = secret;
    }

    public User withId (int id) {
        return new User(id, this.name, this.date);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Username: " + name;
    }

    /**
     * Password stuff
     */
//
//    public static byte[] generateSalt() {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//        return salt;
//    }
//
//    public static byte[] calculateSecret(byte[] salt, String password) {
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt,
//                PASSWORD_ITERATIONS,
//                PASSWORD_LENGTH);
//        try {
//            return PASSWORD_FACTORY.generateSecret(spec).getEncoded();
//        } catch (InvalidKeySpecException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public boolean isPasswordCorrect(String password) {
//        return Arrays.equals(this.secret, calculateSecret(salt, password));
//    }
//
//    // Thanks: https://stackoverflow.com/a/13006907
//    public static String byteArrayToHex(byte[] a) {
//        StringBuilder sb = new StringBuilder(a.length * 2);
//        for(byte b: a)
//            sb.append(String.format("%02x", b));
//        return sb.toString();
//    }


}
