package org.xwalk.core.internal;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import junit.framework.Assert;
import org.chromium.base.CommandLine;
import org.chromium.base.JNINamespace;
import org.chromium.base.PathUtils;
import org.chromium.base.ResourceExtractor;
import org.chromium.base.ResourceExtractor.ResourceEntry;
import org.chromium.base.ResourceExtractor.ResourceInterceptor;
import org.chromium.base.ThreadUtils;
import org.chromium.base.library_loader.LibraryLoader;
import org.chromium.base.library_loader.ProcessInitException;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.content.browser.BrowserStartupController;
import org.chromium.content.browser.DeviceUtils;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

@JNINamespace("xwalk")
class XWalkViewDelegate {
    private static final String COMMAND_LINE_FILE = "xwalk-command-line";
    private static final String[] MANDATORY_LIBRARIES;
    private static final String[] MANDATORY_PAKS;
    private static final String PRIVATE_DATA_DIRECTORY_SUFFIX = "xwalkcore";
    private static final String TAG = "XWalkViewDelegate";
    private static final String XWALK_PAK_NAME = "xwalk.pak";
    private static final String XWALK_RESOURCES_LIST_RES_NAME = "xwalk_resources_list";
    private static boolean sInitialized;
    private static boolean sLibraryLoaded;
    private static boolean sLoadedByHoudini;
    private static boolean sRunningOnIA;

    /* renamed from: org.xwalk.core.internal.XWalkViewDelegate.1 */
    static class C04841 implements Runnable {
        final /* synthetic */ Context val$context;

        C04841(Context context) {
            this.val$context = context;
        }

        public void run() {
            try {
                LibraryLoader.get(1).ensureInitialized(this.val$context);
                DeviceUtils.addDeviceSpecificUserAgentSwitch(this.val$context);
                CommandLine.getInstance().appendSwitchWithValue(XWalkSwitches.PROFILE_NAME, XWalkPreferencesInternal.getStringValue(XWalkSwitches.PROFILE_NAME));
                if (XWalkPreferencesInternal.getValue(XWalkPreferencesInternal.ANIMATABLE_XWALK_VIEW) && !CommandLine.getInstance().hasSwitch(XWalkSwitches.DISABLE_GPU_RASTERIZATION)) {
                    CommandLine.getInstance().appendSwitch(XWalkSwitches.DISABLE_GPU_RASTERIZATION);
                }
                try {
                    BrowserStartupController.get(this.val$context, 1).startBrowserProcessesSync(true);
                } catch (ProcessInitException e) {
                    throw new RuntimeException("Cannot initialize Crosswalk Core", e);
                }
            } catch (ProcessInitException e2) {
                throw new RuntimeException("Cannot initialize Crosswalk Core", e2);
            }
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkViewDelegate.2 */
    static class C06532 implements ResourceInterceptor {
        final /* synthetic */ Context val$context;
        final /* synthetic */ HashSet val$interceptableResources;
        final /* synthetic */ boolean val$isSharedMode;
        final /* synthetic */ boolean val$isTestApk;

        C06532(HashSet hashSet, boolean z, boolean z2, Context context) {
            this.val$interceptableResources = hashSet;
            this.val$isSharedMode = z;
            this.val$isTestApk = z2;
            this.val$context = context;
        }

        public boolean shouldInterceptLoadRequest(String resource) {
            return this.val$interceptableResources.contains(resource);
        }

        public InputStream openRawResource(String resource) {
            if (this.val$isSharedMode || this.val$isTestApk) {
                try {
                    return this.val$context.getAssets().open(resource);
                } catch (IOException e) {
                    Assert.fail(resource + " can't be found in assets.");
                    return null;
                }
            }
            String resourceName = resource.split("\\.")[0];
            try {
                return this.val$context.getResources().openRawResource(XWalkViewDelegate.getResourceId(this.val$context, resourceName, "raw"));
            } catch (NotFoundException e2) {
                Assert.fail("R.raw." + resourceName + " can't be found.");
                return null;
            }
        }
    }

    private static native boolean nativeIsLibraryBuiltForIA();

    XWalkViewDelegate() {
    }

    static {
        boolean z = false;
        sInitialized = false;
        sLibraryLoaded = false;
        sRunningOnIA = true;
        sLoadedByHoudini = false;
        MANDATORY_PAKS = new String[]{XWALK_PAK_NAME, "icudtl.dat"};
        MANDATORY_LIBRARIES = new String[]{PRIVATE_DATA_DIRECTORY_SUFFIX};
        if (Build.CPU_ABI.equalsIgnoreCase("x86") || Build.CPU_ABI.equalsIgnoreCase("x86_64")) {
            z = true;
        }
        sRunningOnIA = z;
        if (!sRunningOnIA) {
            try {
                InputStreamReader ir = new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream());
                BufferedReader input = new BufferedReader(ir);
                sRunningOnIA = input.readLine().contains("x86");
                input.close();
                ir.close();
            } catch (IOException e) {
                Log.w(TAG, Log.getStackTraceString(e));
            }
        }
    }

    private static String[] readCommandLine(Context context) {
        Throwable th;
        String[] tokenizeQuotedAruments;
        InputStreamReader reader = null;
        try {
            InputStream input = context.getAssets().open(COMMAND_LINE_FILE, 3);
            char[] buffer = new char[WebInputEventModifier.NumLockOn];
            StringBuilder builder = new StringBuilder();
            InputStreamReader reader2 = new InputStreamReader(input, "UTF-8");
            while (true) {
                try {
                    int length = reader2.read(buffer, 0, WebInputEventModifier.NumLockOn);
                    if (length == -1) {
                        break;
                    }
                    builder.append(buffer, 0, length);
                } catch (IOException e) {
                    reader = reader2;
                } catch (Throwable th2) {
                    th = th2;
                    reader = reader2;
                }
            }
            tokenizeQuotedAruments = CommandLine.tokenizeQuotedAruments(builder.toString().toCharArray());
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Unable to close file reader.", e2);
                }
            }
            reader = reader2;
        } catch (IOException e3) {
            tokenizeQuotedAruments = null;
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e22) {
                    Log.e(TAG, "Unable to close file reader.", e22);
                }
            }
            return tokenizeQuotedAruments;
        } catch (Throwable th3) {
            th = th3;
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e222) {
                    Log.e(TAG, "Unable to close file reader.", e222);
                }
            }
            throw th;
        }
        return tokenizeQuotedAruments;
    }

    public static void init(Context libContext, Context appContext) {
        if (!loadXWalkLibrary(libContext, null)) {
            Assert.fail();
        }
        if (libContext == null) {
            try {
                init(appContext);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        init(new MixedContext(libContext, appContext));
    }

    public static boolean loadXWalkLibrary(Context context) {
        return loadXWalkLibrary(context, null);
    }

    public static boolean loadXWalkLibrary(Context context, String libDir) throws UnsatisfiedLinkError {
        if (sLibraryLoaded) {
            return true;
        }
        if (libDir == null || sLoadedByHoudini) {
            for (String library : MANDATORY_LIBRARIES) {
                System.loadLibrary(library);
            }
        } else {
            for (String library2 : MANDATORY_LIBRARIES) {
                System.load(libDir + File.separator + "lib" + library2 + ".so");
            }
        }
        try {
            LibraryLoader.get(1).loadNow(context);
        } catch (ProcessInitException e) {
        }
        if (sRunningOnIA != nativeIsLibraryBuiltForIA()) {
            sLoadedByHoudini = true;
            return false;
        }
        sLibraryLoaded = true;
        return true;
    }

    private static void init(Context context) throws IOException {
        if (!sInitialized) {
            PathUtils.setPrivateDataDirectorySuffix(PRIVATE_DATA_DIRECTORY_SUFFIX, context);
            XWalkInternalResources.resetIds(context);
            if (!CommandLine.isInitialized()) {
                CommandLine.init(readCommandLine(context.getApplicationContext()));
            }
            setupResourceInterceptor(context);
            ResourceExtractor.get(context);
            startBrowserProcess(context);
            sInitialized = true;
        }
    }

    private static void startBrowserProcess(Context context) {
        ThreadUtils.runOnUiThreadBlocking(new C04841(context));
    }

    private static void setupResourceInterceptor(Context context) throws IOException {
        boolean isSharedMode;
        boolean isTestApk = true;
        if (context.getPackageName().equals(context.getApplicationContext().getPackageName())) {
            isSharedMode = false;
        } else {
            isSharedMode = true;
        }
        if (isSharedMode || !Arrays.asList(context.getAssets().list(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE)).contains(XWALK_PAK_NAME)) {
            isTestApk = false;
        }
        HashMap<String, ResourceEntry> resourceList = new HashMap();
        if (isSharedMode || isTestApk) {
            for (String resource : MANDATORY_PAKS) {
                resourceList.put(resource, new ResourceEntry(0, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE, resource));
            }
        } else {
            try {
                for (String resource2 : context.getResources().getStringArray(getResourceId(context, XWALK_RESOURCES_LIST_RES_NAME, "array"))) {
                    resourceList.put(resource2, new ResourceEntry(0, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE, resource2));
                }
            } catch (NotFoundException e) {
                Assert.fail("R.array.xwalk_resources_list can't be found.");
            }
        }
        ResourceExtractor.setResourcesToExtract((ResourceEntry[]) resourceList.values().toArray(new ResourceEntry[resourceList.size()]));
        ResourceExtractor.setResourceInterceptor(new C06532(new HashSet(resourceList.keySet()), isSharedMode, isTestApk, context));
    }

    private static int getResourceId(Context context, String resourceName, String resourceType) {
        int resourceId = context.getResources().getIdentifier(resourceName, resourceType, context.getClass().getPackage().getName());
        if (resourceId == 0) {
            return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
        }
        return resourceId;
    }

    public static boolean isRunningOnIA() {
        return sRunningOnIA;
    }
}
