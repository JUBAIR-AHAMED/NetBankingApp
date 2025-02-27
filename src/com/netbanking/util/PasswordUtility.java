package com.netbanking.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtility {
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray()); // 12 is the cost factor
    }

    public static boolean verifyPassword(String inputPassword, String storedHash) {
        return BCrypt.verifyer().verify(inputPassword.toCharArray(), storedHash).verified;
    }
}
