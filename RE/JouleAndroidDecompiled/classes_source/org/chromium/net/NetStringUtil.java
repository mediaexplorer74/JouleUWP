package org.chromium.net;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("net::android")
public class NetStringUtil {
    @CalledByNative
    private static String convertToUnicode(ByteBuffer text, String charsetName) {
        try {
            return Charset.forName(charsetName).newDecoder().decode(text).toString();
        } catch (Exception e) {
            return null;
        }
    }

    @CalledByNative
    private static String convertToUnicodeAndNormalize(ByteBuffer text, String charsetName) {
        String unicodeString = convertToUnicode(text, charsetName);
        if (unicodeString == null) {
            return null;
        }
        return Normalizer.normalize(unicodeString, Form.NFC);
    }

    @CalledByNative
    private static String convertToUnicodeWithSubstitutions(ByteBuffer text, String charsetName) {
        try {
            CharsetDecoder decoder = Charset.forName(charsetName).newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            decoder.replaceWith("\ufffd");
            return decoder.decode(text).toString();
        } catch (Exception e) {
            return null;
        }
    }
}
