package org.chromium.base;

import java.util.Locale;

public class LocaleUtils {
    private LocaleUtils() {
    }

    @CalledByNative
    public static String getDefaultLocale() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if ("iw".equals(language)) {
            language = "he";
        } else if ("in".equals(language)) {
            language = "id";
        } else if ("tl".equals(language)) {
            language = "fil";
        }
        return country.isEmpty() ? language : language + "-" + country;
    }

    @CalledByNative
    private static String getDefaultCountryCode() {
        CommandLine commandLine = CommandLine.getInstance();
        return commandLine.hasSwitch(BaseSwitches.DEFAULT_COUNTRY_CODE_AT_INSTALL) ? commandLine.getSwitchValue(BaseSwitches.DEFAULT_COUNTRY_CODE_AT_INSTALL) : Locale.getDefault().getCountry();
    }
}
