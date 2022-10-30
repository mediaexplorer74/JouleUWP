package com.microsoft.cordova;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import com.microsoft.cordova.CodePushReportingManager.Status;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONException;

public class CodePush extends CordovaPlugin {
    private static final String DEPLOYMENT_KEY_PREFERENCE = "codepushdeploymentkey";
    public static final String RESOURCES_BUNDLE = "resources.arsc";
    private static boolean ShouldClearHistoryOnLoad = false;
    private static final String WWW_ASSET_PATH_PREFIX = "file:///android_asset/www/";
    private CodePushPackageManager codePushPackageManager;
    private CodePushReportingManager codePushReportingManager;
    private boolean didStartApp;
    private boolean didUpdate;
    private long lastPausedTimeMs;
    private CordovaWebView mainWebView;
    private boolean pluginDestroyed;

    /* renamed from: com.microsoft.cordova.CodePush.1 */
    class C02111 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ CallbackContext val$callbackContext;

        C02111(CallbackContext callbackContext) {
            this.val$callbackContext = callbackContext;
        }

        protected Void doInBackground(Void... params) {
            try {
                String binaryHash = UpdateHashUtils.getBinaryHash(CodePush.this.cordova.getActivity());
                CodePush.this.codePushPackageManager.saveBinaryHash(binaryHash);
                this.val$callbackContext.success(binaryHash);
            } catch (IOException e) {
                this.val$callbackContext.error("An error occurred when trying to get the hash of the binary contents. " + e.getMessage());
            } catch (NoSuchAlgorithmException e2) {
                this.val$callbackContext.error("An error occurred when trying to get the hash of the binary contents. " + e2.getMessage());
            }
            return null;
        }
    }

    /* renamed from: com.microsoft.cordova.CodePush.2 */
    class C02122 implements Runnable {
        final /* synthetic */ String val$configLaunchUrl;

        C02122(String str) {
            this.val$configLaunchUrl = str;
        }

        public void run() {
            CodePush.this.navigateToURL(this.val$configLaunchUrl);
        }
    }

    /* renamed from: com.microsoft.cordova.CodePush.3 */
    class C02133 implements Runnable {
        final /* synthetic */ String val$finalURL;

        C02133(String str) {
            this.val$finalURL = str;
        }

        public void run() {
            CodePush.this.navigateToURL(this.val$finalURL);
        }
    }

    public CodePush() {
        this.pluginDestroyed = false;
        this.didUpdate = false;
        this.didStartApp = false;
        this.lastPausedTimeMs = 0;
    }

    static {
        ShouldClearHistoryOnLoad = false;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        CodePushPreferences codePushPreferences = new CodePushPreferences(cordova.getActivity());
        this.codePushPackageManager = new CodePushPackageManager(cordova.getActivity(), codePushPreferences);
        this.codePushReportingManager = new CodePushReportingManager(cordova.getActivity(), codePushPreferences);
        this.mainWebView = webView;
    }

    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) {
        if ("getServerURL".equals(action)) {
            returnStringPreference("codepushserverurl", callbackContext);
            return true;
        } else if ("getDeploymentKey".equals(action)) {
            returnStringPreference(DEPLOYMENT_KEY_PREFERENCE, callbackContext);
            return true;
        } else if ("getNativeBuildTime".equals(action)) {
            return execGetNativeBuildTime(callbackContext);
        } else {
            if ("getAppVersion".equals(action)) {
                return execGetAppVersion(callbackContext);
            }
            if ("getBinaryHash".equals(action)) {
                return execGetBinaryHash(callbackContext);
            }
            if ("preInstall".equals(action)) {
                return execPreInstall(args, callbackContext);
            }
            if ("install".equals(action)) {
                return execInstall(args, callbackContext);
            }
            if ("updateSuccess".equals(action)) {
                return execUpdateSuccess(callbackContext);
            }
            if ("restartApplication".equals(action)) {
                return execRestartApplication(args, callbackContext);
            }
            if ("isPendingUpdate".equals(action)) {
                return execIsPendingUpdate(args, callbackContext);
            }
            if ("isFailedUpdate".equals(action)) {
                return execIsFailedUpdate(args, callbackContext);
            }
            if ("isFirstRun".equals(action)) {
                return execIsFirstRun(args, callbackContext);
            }
            return false;
        }
    }

    private boolean execGetBinaryHash(CallbackContext callbackContext) {
        String cachedBinaryHash = this.codePushPackageManager.getCachedBinaryHash();
        if (cachedBinaryHash == null) {
            new C02111(callbackContext).execute(new Void[0]);
        } else {
            callbackContext.success(cachedBinaryHash);
        }
        return true;
    }

    private boolean execUpdateSuccess(CallbackContext callbackContext) {
        if (this.codePushPackageManager.isFirstRun()) {
            this.codePushPackageManager.saveFirstRunFlag();
            try {
                this.codePushReportingManager.reportStatus(Status.STORE_VERSION, null, Utilities.getAppVersionName(this.cordova.getActivity()), this.mainWebView.getPreferences().getString(DEPLOYMENT_KEY_PREFERENCE, null), this.mainWebView);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (this.codePushPackageManager.installNeedsConfirmation()) {
            CodePushPackageMetadata currentMetadata = this.codePushPackageManager.getCurrentPackageMetadata();
            this.codePushReportingManager.reportStatus(Status.UPDATE_CONFIRMED, currentMetadata.label, currentMetadata.appVersion, currentMetadata.deploymentKey, this.mainWebView);
        }
        this.codePushPackageManager.clearInstallNeedsConfirmation();
        cleanOldPackageSilently();
        callbackContext.success();
        return true;
    }

    private boolean execIsFirstRun(CordovaArgs args, CallbackContext callbackContext) {
        int i = 0;
        boolean isFirstRun = false;
        try {
            String packageHash = args.getString(0);
            CodePushPackageMetadata currentPackageMetadata = this.codePushPackageManager.getCurrentPackageMetadata();
            if (currentPackageMetadata != null) {
                if (packageHash == null || packageHash.isEmpty() || !packageHash.equals(currentPackageMetadata.packageHash) || !this.didUpdate) {
                    isFirstRun = false;
                } else {
                    isFirstRun = true;
                }
            }
            if (isFirstRun) {
                i = 1;
            }
            callbackContext.success(i);
        } catch (JSONException e) {
            callbackContext.error("Invalid package hash. " + e.getMessage());
        }
        return true;
    }

    private boolean execIsPendingUpdate(CordovaArgs args, CallbackContext callbackContext) {
        try {
            callbackContext.success(this.codePushPackageManager.getPendingInstall() != null ? 1 : 0);
        } catch (Exception e) {
            callbackContext.error("An error occurred. " + e.getMessage());
        }
        return true;
    }

    private boolean execIsFailedUpdate(CordovaArgs args, CallbackContext callbackContext) {
        int i = 0;
        try {
            if (this.codePushPackageManager.isFailedUpdate(args.getString(0))) {
                i = 1;
            }
            callbackContext.success(i);
        } catch (JSONException e) {
            callbackContext.error("Could not read the package hash: " + e.getMessage());
        }
        return true;
    }

    private boolean execInstall(CordovaArgs args, CallbackContext callbackContext) {
        try {
            String startLocation = args.getString(0);
            InstallMode installMode = InstallMode.fromValue(args.optInt(1));
            int minimumBackgroundDuration = args.optInt(2);
            File startPage = getStartPageForPackage(startLocation);
            if (startPage != null) {
                if (InstallMode.IMMEDIATE.equals(installMode)) {
                    navigateToFile(startPage);
                    markUpdate();
                } else {
                    this.codePushPackageManager.savePendingInstall(new InstallOptions(installMode, minimumBackgroundDuration));
                }
                callbackContext.success();
            } else {
                callbackContext.error("Could not find the package start page.");
            }
        } catch (Exception e) {
            callbackContext.error("Cound not read webview URL: " + e.getMessage());
        }
        return true;
    }

    private boolean execRestartApplication(CordovaArgs args, CallbackContext callbackContext) {
        try {
            if (this.codePushPackageManager.getCurrentPackageMetadata() != null) {
                callbackContext.success();
                this.didStartApp = false;
                onStart();
            } else {
                String configLaunchUrl = getConfigLaunchUrl();
                if (!this.pluginDestroyed) {
                    callbackContext.success();
                    this.cordova.getActivity().runOnUiThread(new C02122(configLaunchUrl));
                }
            }
        } catch (Exception e) {
            callbackContext.error("An error occurred while restarting the application." + e.getMessage());
        }
        return true;
    }

    private void markUpdate() {
        this.didUpdate = true;
        this.codePushPackageManager.markInstallNeedsConfirmation();
    }

    private void cleanOldPackageSilently() {
        try {
            this.codePushPackageManager.cleanOldPackage();
        } catch (Exception e) {
            Utilities.logException(e);
        }
    }

    private boolean execPreInstall(CordovaArgs args, CallbackContext callbackContext) {
        try {
            if (getStartPageForPackage(args.getString(0)) != null) {
                callbackContext.success();
            } else {
                callbackContext.error("Could not get the package start page");
            }
        } catch (Exception e) {
            callbackContext.error("Could not get the package start page");
        }
        return true;
    }

    private boolean execGetAppVersion(CallbackContext callbackContext) {
        try {
            callbackContext.success(Utilities.getAppVersionName(this.cordova.getActivity()));
        } catch (NameNotFoundException e) {
            callbackContext.error("Cannot get application version.");
        }
        return true;
    }

    private boolean execGetNativeBuildTime(CallbackContext callbackContext) {
        long millis = Utilities.getApkEntryBuildTime(RESOURCES_BUNDLE, this.cordova.getActivity());
        if (millis == -1) {
            callbackContext.error("Could not get the application buildstamp.");
        } else {
            callbackContext.success(String.valueOf(millis));
        }
        return true;
    }

    private void returnStringPreference(String preferenceName, CallbackContext callbackContext) {
        String result = this.mainWebView.getPreferences().getString(preferenceName, null);
        if (result != null) {
            callbackContext.success(result);
        } else {
            callbackContext.error("Could not get preference: " + preferenceName);
        }
    }

    private void handleAppStart() {
        try {
            CodePushPackageMetadata deployedPackageMetadata = this.codePushPackageManager.getCurrentPackageMetadata();
            if (deployedPackageMetadata != null) {
                String deployedPackageTimeStamp = deployedPackageMetadata.nativeBuildTime;
                long nativeBuildTime = Utilities.getApkEntryBuildTime(RESOURCES_BUNDLE, this.cordova.getActivity());
                if (nativeBuildTime != -1) {
                    String currentAppTimeStamp = String.valueOf(nativeBuildTime);
                    if (deployedPackageTimeStamp != null && currentAppTimeStamp != null) {
                        if (!deployedPackageTimeStamp.equals(currentAppTimeStamp)) {
                            this.codePushPackageManager.cleanDeployments();
                            this.codePushPackageManager.clearFailedUpdates();
                            this.codePushPackageManager.clearPendingInstall();
                            this.codePushPackageManager.clearInstallNeedsConfirmation();
                            try {
                                this.codePushReportingManager.reportStatus(Status.STORE_VERSION, null, Utilities.getAppVersionName(this.cordova.getActivity()), this.mainWebView.getPreferences().getString(DEPLOYMENT_KEY_PREFERENCE, null), this.mainWebView);
                            } catch (NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if (deployedPackageMetadata.localPath != null) {
                            File startPage = getStartPageForPackage(deployedPackageMetadata.localPath);
                            if (startPage != null) {
                                navigateToFile(startPage);
                            }
                        }
                    }
                }
            }
        } catch (Exception e2) {
        }
    }

    private void handleUnconfirmedInstall(boolean navigate) {
        if (this.codePushPackageManager.installNeedsConfirmation()) {
            CodePushPackageMetadata currentMetadata = this.codePushPackageManager.getCurrentPackageMetadata();
            this.codePushReportingManager.reportStatus(Status.UPDATE_ROLLED_BACK, currentMetadata.label, currentMetadata.appVersion, currentMetadata.deploymentKey, this.mainWebView);
            this.codePushPackageManager.clearInstallNeedsConfirmation();
            this.codePushPackageManager.revertToPreviousVersion();
            if (navigate) {
                String url;
                try {
                    url = getStartPageURLForPackage(this.codePushPackageManager.getCurrentPackageMetadata().localPath);
                } catch (Exception e) {
                    url = getConfigLaunchUrl();
                }
                String finalURL = url;
                if (!this.pluginDestroyed) {
                    this.cordova.getActivity().runOnUiThread(new C02133(finalURL));
                }
            }
        }
    }

    private void navigateToFile(File startPageFile) throws MalformedURLException {
        if (startPageFile != null) {
            navigateToURL(startPageFile.toURI().toURL().toString());
        }
    }

    private void navigateToURL(String url) {
        if (url != null) {
            ShouldClearHistoryOnLoad = true;
            this.mainWebView.loadUrlIntoView(url, false);
        }
    }

    private File getStartPageForPackage(String packageLocation) {
        if (packageLocation != null) {
            File startPage = new File(this.cordova.getActivity().getFilesDir() + packageLocation, "www/" + getConfigStartPageName());
            if (startPage.exists()) {
                return startPage;
            }
        }
        return null;
    }

    private String getStartPageURLForPackage(String packageLocation) throws MalformedURLException {
        File startPageFile = getStartPageForPackage(packageLocation);
        if (startPageFile != null) {
            return startPageFile.toURI().toURL().toString();
        }
        return null;
    }

    private String getConfigStartPageName() {
        String launchUrl = getConfigLaunchUrl();
        int launchUrlLength = launchUrl.length();
        if (launchUrl.startsWith(WWW_ASSET_PATH_PREFIX)) {
            return launchUrl.substring(WWW_ASSET_PATH_PREFIX.length(), launchUrlLength);
        }
        return launchUrl;
    }

    private String getConfigLaunchUrl() {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this.cordova.getActivity());
        return parser.getLaunchUrl();
    }

    public void onPause(boolean multitasking) {
        this.lastPausedTimeMs = new Date().getTime();
    }

    public void onResume(boolean multitasking) {
        this.pluginDestroyed = false;
    }

    public void onStart() {
        InstallOptions pendingInstall;
        if (this.didStartApp) {
            pendingInstall = this.codePushPackageManager.getPendingInstall();
            long durationInBackground = (new Date().getTime() - this.lastPausedTimeMs) / 1000;
            if (pendingInstall != null && InstallMode.ON_NEXT_RESUME.equals(pendingInstall.installMode) && durationInBackground >= ((long) pendingInstall.minimumBackgroundDuration)) {
                handleAppStart();
                markUpdate();
                this.codePushPackageManager.clearPendingInstall();
                return;
            }
            return;
        }
        this.didStartApp = true;
        pendingInstall = this.codePushPackageManager.getPendingInstall();
        if (pendingInstall == null) {
            handleUnconfirmedInstall(false);
        }
        handleAppStart();
        if (pendingInstall == null) {
            return;
        }
        if (InstallMode.ON_NEXT_RESUME.equals(pendingInstall.installMode) || InstallMode.ON_NEXT_RESTART.equals(pendingInstall.installMode)) {
            markUpdate();
            this.codePushPackageManager.clearPendingInstall();
        }
    }

    public void onDestroy() {
        this.pluginDestroyed = true;
    }

    public Object onMessage(String id, Object data) {
        if ("onPageFinished".equals(id) && ShouldClearHistoryOnLoad) {
            ShouldClearHistoryOnLoad = false;
            if (this.mainWebView != null) {
                this.mainWebView.clearHistory();
            }
        }
        return null;
    }
}
