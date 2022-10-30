package org.chromium.base.library_loader;

import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

@SuppressFBWarnings
public class NativeLibraries {
    public static final String[] LIBRARIES;
    public static boolean sEnableLinkerTests;
    public static boolean sUseLibraryInZipFile;
    public static boolean sUseLinker;
    static String sVersionNumber;

    static {
        sUseLinker = false;
        sUseLibraryInZipFile = false;
        sEnableLinkerTests = false;
        LIBRARIES = new String[]{"xwalkdummy"};
        sVersionNumber = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
    }
}
