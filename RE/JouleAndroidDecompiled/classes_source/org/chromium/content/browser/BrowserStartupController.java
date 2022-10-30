package org.chromium.content.browser;

import android.content.Context;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.ResourceExtractor;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.library_loader.LibraryLoader;
import org.chromium.base.library_loader.ProcessInitException;
import org.chromium.content.app.ContentMain;

@JNINamespace("content")
public class BrowserStartupController {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final boolean ALREADY_STARTED = true;
    private static final boolean NOT_ALREADY_STARTED = false;
    @VisibleForTesting
    static final int STARTUP_FAILURE = 1;
    @VisibleForTesting
    static final int STARTUP_SUCCESS = -1;
    private static final String TAG = "cr.BrowserStartup";
    private static boolean sBrowserMayStartAsynchronously;
    private static BrowserStartupController sInstance;
    private final List<StartupCallback> mAsyncStartupCallbacks;
    private final Context mContext;
    private boolean mHasStartedInitializingBrowserProcess;
    private int mLibraryProcessType;
    private boolean mPostResourceExtractionTasksCompleted;
    private boolean mStartupDone;
    private boolean mStartupSuccess;

    /* renamed from: org.chromium.content.browser.BrowserStartupController.1 */
    class C03251 implements Runnable {
        C03251() {
        }

        public void run() {
            ThreadUtils.assertOnUiThread();
            if (BrowserStartupController.this.contentStart() > 0) {
                BrowserStartupController.this.enqueueCallbackExecution(BrowserStartupController.STARTUP_FAILURE, BrowserStartupController.NOT_ALREADY_STARTED);
            }
        }
    }

    /* renamed from: org.chromium.content.browser.BrowserStartupController.2 */
    class C03262 implements Runnable {
        final /* synthetic */ boolean val$alreadyStarted;
        final /* synthetic */ int val$startupFailure;

        C03262(int i, boolean z) {
            this.val$startupFailure = i;
            this.val$alreadyStarted = z;
        }

        public void run() {
            BrowserStartupController.this.executeEnqueuedCallbacks(this.val$startupFailure, this.val$alreadyStarted);
        }
    }

    /* renamed from: org.chromium.content.browser.BrowserStartupController.3 */
    class C03273 implements Runnable {
        final /* synthetic */ StartupCallback val$callback;

        C03273(StartupCallback startupCallback) {
            this.val$callback = startupCallback;
        }

        public void run() {
            if (BrowserStartupController.this.mStartupSuccess) {
                this.val$callback.onSuccess(BrowserStartupController.ALREADY_STARTED);
            } else {
                this.val$callback.onFailure();
            }
        }
    }

    /* renamed from: org.chromium.content.browser.BrowserStartupController.4 */
    class C03284 implements Runnable {
        final /* synthetic */ Runnable val$completionCallback;
        final /* synthetic */ boolean val$singleProcess;

        C03284(boolean z, Runnable runnable) {
            this.val$singleProcess = z;
            this.val$completionCallback = runnable;
        }

        public void run() {
            if (!BrowserStartupController.this.mPostResourceExtractionTasksCompleted) {
                DeviceUtils.addDeviceSpecificUserAgentSwitch(BrowserStartupController.this.mContext);
                ContentMain.initApplicationContext(BrowserStartupController.this.mContext);
                BrowserStartupController.nativeSetCommandLineFlags(this.val$singleProcess, BrowserStartupController.nativeIsPluginEnabled() ? BrowserStartupController.this.getPlugins() : null);
                BrowserStartupController.this.mPostResourceExtractionTasksCompleted = BrowserStartupController.ALREADY_STARTED;
            }
            if (this.val$completionCallback != null) {
                this.val$completionCallback.run();
            }
        }
    }

    public interface StartupCallback {
        void onFailure();

        void onSuccess(boolean z);
    }

    private static native boolean nativeIsOfficialBuild();

    private static native boolean nativeIsPluginEnabled();

    private static native void nativeSetCommandLineFlags(boolean z, String str);

    static {
        boolean z;
        if (BrowserStartupController.class.desiredAssertionStatus()) {
            z = NOT_ALREADY_STARTED;
        } else {
            z = ALREADY_STARTED;
        }
        $assertionsDisabled = z;
        sBrowserMayStartAsynchronously = NOT_ALREADY_STARTED;
    }

    private static void setAsynchronousStartup(boolean enable) {
        sBrowserMayStartAsynchronously = enable;
    }

    @CalledByNative
    @VisibleForTesting
    static boolean browserMayStartAsynchonously() {
        return sBrowserMayStartAsynchronously;
    }

    @CalledByNative
    @VisibleForTesting
    static void browserStartupComplete(int result) {
        if (sInstance != null) {
            sInstance.executeEnqueuedCallbacks(result, NOT_ALREADY_STARTED);
        }
    }

    BrowserStartupController(Context context, int libraryProcessType) {
        this.mContext = context.getApplicationContext();
        this.mAsyncStartupCallbacks = new ArrayList();
        this.mLibraryProcessType = libraryProcessType;
    }

    public static BrowserStartupController get(Context context, int libraryProcessType) {
        if ($assertionsDisabled || ThreadUtils.runningOnUiThread()) {
            ThreadUtils.assertOnUiThread();
            if (sInstance == null) {
                if ($assertionsDisabled || STARTUP_FAILURE == libraryProcessType || 3 == libraryProcessType) {
                    sInstance = new BrowserStartupController(context, libraryProcessType);
                } else {
                    throw new AssertionError();
                }
            }
            if ($assertionsDisabled || sInstance.mLibraryProcessType == libraryProcessType) {
                return sInstance;
            }
            throw new AssertionError("Wrong process type");
        }
        throw new AssertionError("Tried to start the browser on the wrong thread.");
    }

    @VisibleForTesting
    static BrowserStartupController overrideInstanceForTest(BrowserStartupController controller) {
        if (sInstance == null) {
            sInstance = controller;
        }
        return sInstance;
    }

    public void startBrowserProcessesAsync(StartupCallback callback) throws ProcessInitException {
        if (!$assertionsDisabled && !ThreadUtils.runningOnUiThread()) {
            throw new AssertionError("Tried to start the browser on the wrong thread.");
        } else if (this.mStartupDone) {
            postStartupCompleted(callback);
        } else {
            this.mAsyncStartupCallbacks.add(callback);
            if (!this.mHasStartedInitializingBrowserProcess) {
                this.mHasStartedInitializingBrowserProcess = ALREADY_STARTED;
                setAsynchronousStartup(ALREADY_STARTED);
                prepareToStartBrowserProcess(NOT_ALREADY_STARTED, new C03251());
            }
        }
    }

    public void startBrowserProcessesSync(boolean singleProcess) throws ProcessInitException {
        if (!this.mStartupDone) {
            if (!(this.mHasStartedInitializingBrowserProcess && this.mPostResourceExtractionTasksCompleted)) {
                prepareToStartBrowserProcess(singleProcess, null);
            }
            setAsynchronousStartup(NOT_ALREADY_STARTED);
            if (contentStart() > 0) {
                enqueueCallbackExecution(STARTUP_FAILURE, NOT_ALREADY_STARTED);
            }
        }
        if (!$assertionsDisabled && !this.mStartupDone) {
            throw new AssertionError();
        } else if (!this.mStartupSuccess) {
            throw new ProcessInitException(4);
        }
    }

    @VisibleForTesting
    int contentStart() {
        return ContentMain.start();
    }

    public void addStartupCompletedObserver(StartupCallback callback) {
        ThreadUtils.assertOnUiThread();
        if (this.mStartupDone) {
            postStartupCompleted(callback);
        } else {
            this.mAsyncStartupCallbacks.add(callback);
        }
    }

    private void executeEnqueuedCallbacks(int startupResult, boolean alreadyStarted) {
        boolean z = ALREADY_STARTED;
        if ($assertionsDisabled || ThreadUtils.runningOnUiThread()) {
            this.mStartupDone = ALREADY_STARTED;
            if (startupResult > 0) {
                z = NOT_ALREADY_STARTED;
            }
            this.mStartupSuccess = z;
            for (StartupCallback asyncStartupCallback : this.mAsyncStartupCallbacks) {
                if (this.mStartupSuccess) {
                    asyncStartupCallback.onSuccess(alreadyStarted);
                } else {
                    asyncStartupCallback.onFailure();
                }
            }
            this.mAsyncStartupCallbacks.clear();
            return;
        }
        throw new AssertionError("Callback from browser startup from wrong thread.");
    }

    private void enqueueCallbackExecution(int startupFailure, boolean alreadyStarted) {
        new Handler().post(new C03262(startupFailure, alreadyStarted));
    }

    private void postStartupCompleted(StartupCallback callback) {
        new Handler().post(new C03273(callback));
    }

    @VisibleForTesting
    void prepareToStartBrowserProcess(boolean singleProcess, Runnable completionCallback) throws ProcessInitException {
        Object[] objArr = new Object[STARTUP_FAILURE];
        objArr[0] = Boolean.valueOf(singleProcess);
        Log.m33i(TAG, "Initializing chromium process, singleProcess=%b", objArr);
        ResourceExtractor resourceExtractor = ResourceExtractor.get(this.mContext);
        resourceExtractor.startExtractingResources();
        LibraryLoader.get(this.mLibraryProcessType).ensureInitialized(this.mContext);
        Runnable postResourceExtraction = new C03284(singleProcess, completionCallback);
        if (completionCallback == null) {
            resourceExtractor.waitForCompletion();
            postResourceExtraction.run();
            return;
        }
        resourceExtractor.addCompletionCallback(postResourceExtraction);
    }

    public void initChromiumBrowserProcessForTests() {
        ResourceExtractor resourceExtractor = ResourceExtractor.get(this.mContext);
        resourceExtractor.startExtractingResources();
        resourceExtractor.waitForCompletion();
        ContentMain.initApplicationContext(this.mContext.getApplicationContext());
        nativeSetCommandLineFlags(NOT_ALREADY_STARTED, null);
    }

    private String getPlugins() {
        return PepperPluginManager.getPlugins(this.mContext);
    }
}
