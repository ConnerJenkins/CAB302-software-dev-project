package main.java.com.team.game.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtils {
    private PasswordUtils() {}

    /** Hash password using bcrypt with rand salt. */
    public static String hashPassword(char[] password) {
        String plain = new String(password);
        try {
            return BCrypt.hashpw(plain, BCrypt.gensalt(12)); // 12 = cost factor
        } finally {
            // clear memory
            java.util.Arrays.fill(password, '\0');
        }
    }

    /** Verify candidate pass against stored bcrypt hash. */
    public static boolean verifyPassword(char[] password, String storedHash) {
        String plain = new String(password);
        try {
            return BCrypt.checkpw(plain, storedHash);
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }
}