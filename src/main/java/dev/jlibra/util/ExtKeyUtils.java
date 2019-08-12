package dev.jlibra.util;

import org.bouncycastle.util.encoders.Hex;

import java.security.PrivateKey;
import java.security.PublicKey;

public class ExtKeyUtils {

    public static PublicKey publicKeyFromBytes(byte[] publicKey) {
        return dev.jlibra.KeyUtils.publicKeyFromHexString(new String(Hex.encode(publicKey)));
    }

    public static PrivateKey privateKeyFromBytes(byte[] privateKey) {
        return dev.jlibra.KeyUtils.privateKeyFromHexString(new String(Hex.encode(privateKey)));
    }
}
