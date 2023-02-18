package com.example.tempauth;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TOTPgenerator
{
    private static byte[] HMAC_SHA1(byte[] key, byte[] text)  // text is counter in our case
    {
        /*
            Mac class provides the functionality of a "Message Authentication Code" (MAC) algorithm.
            A MAC provides a way to check the integrity of information transmitted over or stored in an
            unreliable medium, based on a secret key. Typically, message authentication codes are used
            between two parties that share a secret key in order to validate information transmitted
            between these parties. A MAC mechanism that is based on cryptographic hash functions is
            referred to as HMAC. HMAC can be used with any cryptographic hash function, e.g., SHA1 or
            SHA256 or SHA384, in combination with a secret shared key. HMAC is specified in RFC 2104.
            Every implementation of the Java platform is required to support the following standard Mac algorithms:
                * HmacSHA1
                * HmacSHA256
            These algorithms are described in the Mac section of the Java Security Standard Algorithm Names Specification.
         */
        try {
            Mac hmac_sha1 = Mac.getInstance("HmacSHA1"); // what algo to implement
            // returns a Mac object that implements the specified MAC algorithm

            SecretKeySpec macKey = new SecretKeySpec(key, "RAW");
            // This class specifies a secret key in a provider-independent fashion.
            // It can be used to construct a SecretKey from a byte array, without having to go through a (provider-based) SecretKeyFactory.
            // This class is only useful for raw secret keys that can be
            // represented as a byte array and have no key parameters associated with them,
            // e.g., DES or Triple DES keys.

            hmac_sha1.init(macKey); // what key is to be used in the algo
            // Initializes this Mac object with the given key

            byte[] barr = hmac_sha1.doFinal(text); // final HMAC output (combination of key and text (unix time)
            // Processes the given array of bytes and finishes the MAC operation. Output is of 20 Bytes

            return barr;
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println(e);
        }
        return null;
    }

    public static String generateTOTP(byte[] key)
    {
        // GENERATION PHASE
        // getting UNIX time as moving factor
        long unixTime = System.currentTimeMillis()/1000; // 32 bits (4 bytes) counter
        long movingFactor = unixTime / 30; // to have interval of 30 seconds

        // putting movingFactor byte-by-byte into text byte[] array
        byte[] text = new byte[8]; // required 8 bytes array
        for(int i=text.length-1; i>=0; i--)
        {
            text[i] = (byte) (movingFactor & 0xff); // 0xff = 255 = 11111111
            movingFactor = movingFactor >> 8;
        }

        // compute HMAC hash
        // arg1 - key => secret key provided (in byte array format)
        // arg2 - text => unix time calculated (in byte array format)
        byte[] hmacHash = HMAC_SHA1(key,text);

        // TRUNCATION PHASE
        // calculating a 2 digit (0-15) offset from last byte of the hmacHash
        int offset = hmacHash[hmacHash.length-1] & 0xf; // 0xf = 15 = 00001111

        // calculating truncated hash
        int truncatedHash = (hmacHash[offset] & 0x7f)<<24 | (hmacHash[offset+1] & 0xff)<<16 | (hmacHash[offset+2] & 0xff)<<8 | (hmacHash[offset+3] & 0xff);
        //     32 bits int          (8 bits) + 24 bits        8 bits + (8 bits) + 16 bits       16 bits + (8 bits) + 8 bits         24 bits + (8 bits)

        // calculating OTP
        int TOTP = truncatedHash % (1000000);

        // to get TOTP in proper 6-digit string format
        String TOTPstr = TOTP+"";
        for(int i=TOTPstr.length(); i<6; i++) TOTPstr = "0" + TOTPstr; // to get 0 in front if TOTP starts with zero and having six digits total

        return TOTPstr;
    }
}

/*
    8-byte counter value, the moving factor.  This counter
    MUST be synchronized between the HOTP generator (client)
    and the HOTP validator (server).
    shared secret between client and server; each HOTP
    generator has a different and unique secret Key.

    The HOTP algorithm is based on an increasing counter value and a
    static symmetric key known only to the token and the validation
    service.  In order to create the HOTP value, we will use the HMAC-
    SHA-1 algorithm, as the output of the HMAC-SHA-1 calculation is
    160 bits, we must truncate this value to something that can be
    easily entered by a user.


    We can HOTP value in 3 distinct steps:

    Step 1: Generate an HMAC-SHA-1 value Let HS = HMAC-SHA-1(K,C)  // HS is a 20-byte string

    Step 2: Generate a 4-byte string (Dynamic Truncation)
            Let Sbits = DT(HS)  // returns a 31-bit string

    Step 3: Compute an HOTP value
            Let Snum  = StToNum(Sbits)  // Convert S to a number in 0...2^{31}-1
            Return D = Snum mod 10^Digit  // D is a number in the range 0...10^{Digit}-1

    The Truncate function performs Step 2 and Step 3, i.e., the dynamic
    truncation and then the reduction modulo 10^Digit.  The purpose of
    the dynamic offset truncation technique is to extract a 4-byte
    dynamic binary code from a 160-bit (20-byte) HMAC-SHA-1 result.

*/
