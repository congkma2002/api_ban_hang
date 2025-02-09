package com.actvn.Shopee_BE.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleAuthService {
    String verifyAndAuthenticate(String credential) throws GeneralSecurityException, IOException;
}
