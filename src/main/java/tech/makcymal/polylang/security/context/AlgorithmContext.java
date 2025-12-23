package tech.makcymal.polylang.security.context;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.makcymal.polylang.security.SecurityProperties;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlgorithmContext {

    private final SecurityProperties props;

    private Algorithm algorithm;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private KeyFactory keyFactory;

    public Algorithm getAlgorithm() {
        if (algorithm == null) {
            algorithm = Algorithm.RSA256(getPublicKey(), getPrivateKey());
        }
        return algorithm;
    }

    private RSAPublicKey getPublicKey() {
        if (publicKey == null && props.getPublicKey() != null) {
            publicKey = Optional.of(props)
                    .map(SecurityProperties::getPublicKey)
                    .map(this::getPublicKeySpec)
                    .map(this::generatePublicKey)
                    .orElse(null);
        }
        return publicKey;
    }

    private EncodedKeySpec getPublicKeySpec(String keyValue) {
        byte[] keyBytes = Base64.getDecoder().decode(keyValue);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keySpec;
    }

    private RSAPublicKey generatePublicKey(EncodedKeySpec keySpec) {
        try {
            KeyFactory factory = getKeyFactory();
            return (RSAPublicKey) factory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Can't generate public key", e);
        }
    }

    private RSAPrivateKey getPrivateKey() {
        if (privateKey == null && props.getPrivateKey() != null) {
            privateKey = Optional.of(props)
                .map(SecurityProperties::getPrivateKey)
                .map(this::getPrivateKeySpec)
                .map(this::generatePrivateKey)
                .orElse(null);
        }
        return privateKey;
    }

    private EncodedKeySpec getPrivateKeySpec(String keyValue) {
        byte[] keyBytes = Base64.getDecoder().decode(keyValue);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keySpec;
    }

    private RSAPrivateKey generatePrivateKey(EncodedKeySpec keySpec) {
        try {
            KeyFactory factory = getKeyFactory();
            return (RSAPrivateKey) factory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Can't generate private key", e);
        }
    }

    private KeyFactory getKeyFactory() {
        if (keyFactory == null) {
            try {
                keyFactory = KeyFactory.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Can't instantiate KeyFactory", e);
            }
        }
        return keyFactory;
    }

}
