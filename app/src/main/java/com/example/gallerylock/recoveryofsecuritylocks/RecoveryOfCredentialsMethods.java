package com.example.gallerylock.recoveryofsecuritylocks;

import java.util.regex.Pattern;

/* loaded from: classes2.dex */
public class RecoveryOfCredentialsMethods {
    public boolean isEmailValid(String str) {
        return Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", 2).matcher(str).matches();
    }
}
