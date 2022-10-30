package com.microsoft.cordova;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.HashSet;
import java.util.Set;

public class CodePushPreferences {
    private static final String BINARY_HASH_PREFERENCE = "BINARY_HASH";
    private static final String BINARY_HASH_PREFERENCE_KEY = "BINARY_HASH_KEY";
    private static final String FAILED_UPDATES_KEY = "FAILED_UPDATES_KEY";
    private static final String FAILED_UPDATES_PREFERENCE = "FAILED_UPDATES";
    private static final String FIRST_RUN_PREFERENCE = "CODE_PUSH_FIRST_RUN";
    private static final String FIRST_RUN_PREFERENCE_KEY = "CODE_PUSH_FIRST_RUN_KEY";
    private static final String INSTALL_MIN_BACKGROUND_DURATION = "INSTALL_MINIMUM_BACKGROUND_DURATION";
    private static final String INSTALL_MODE_KEY = "INSTALL_MODE_KEY";
    private static final String INSTALL_NEEDS_CONFIRMATION = "INSTALL_NEEDS_CONFIRMATION";
    private static final String INSTALL_NEEDS_CONFIRMATION_KEY = "INSTALL_NEEDS_CONFIRMATION_KEY";
    private static final String LAST_VERSION_DEPLOYMENT_KEY_KEY = "LAST_VERSION_DEPLOYMENT_KEY_KEY";
    private static final String LAST_VERSION_LABEL_OR_APP_VERSION_KEY = "LAST_VERSION_LABEL_OR_APP_VERSION_KEY";
    private static final String LAST_VERSION_PREFERENCE = "CODE_PUSH_LAST_VERSION";
    private static final String PENDING_INSTALL_PREFERENCE = "PENDING_INSTALL";
    private Context context;

    public CodePushPreferences(Context context) {
        this.context = context;
    }

    public String getCachedBinaryHash() {
        return this.context.getSharedPreferences(BINARY_HASH_PREFERENCE, 0).getString(BINARY_HASH_PREFERENCE_KEY, null);
    }

    public void saveBinaryHash(String binaryHash) {
        Editor editor = this.context.getSharedPreferences(BINARY_HASH_PREFERENCE, 0).edit();
        editor.putString(BINARY_HASH_PREFERENCE_KEY, binaryHash);
        editor.commit();
    }

    public void saveFailedUpdate(String hashCode) {
        SharedPreferences preferences = this.context.getSharedPreferences(FAILED_UPDATES_PREFERENCE, 0);
        Set<String> failedUpdatesSet = preferences.getStringSet(FAILED_UPDATES_KEY, null);
        if (failedUpdatesSet == null) {
            failedUpdatesSet = new HashSet();
        }
        failedUpdatesSet.add(hashCode);
        Editor editor = preferences.edit();
        editor.putStringSet(FAILED_UPDATES_KEY, failedUpdatesSet);
        editor.commit();
    }

    public boolean isFailedUpdate(String hashCode) {
        if (hashCode == null) {
            return false;
        }
        Set<String> failedUpdatesSet = this.context.getSharedPreferences(FAILED_UPDATES_PREFERENCE, 0).getStringSet(FAILED_UPDATES_KEY, null);
        if (failedUpdatesSet == null || !failedUpdatesSet.contains(hashCode)) {
            return false;
        }
        return true;
    }

    public void clearFailedUpdates() {
        clearPreferences(FAILED_UPDATES_PREFERENCE);
    }

    public void savePendingInstall(InstallOptions installOptions) {
        Editor editor = this.context.getSharedPreferences(PENDING_INSTALL_PREFERENCE, 0).edit();
        editor.putInt(INSTALL_MODE_KEY, installOptions.installMode.getValue());
        editor.putInt(INSTALL_MIN_BACKGROUND_DURATION, installOptions.minimumBackgroundDuration);
        editor.commit();
    }

    public void clearPendingInstall() {
        clearPreferences(PENDING_INSTALL_PREFERENCE);
    }

    public InstallOptions getPendingInstall() {
        SharedPreferences preferences = this.context.getSharedPreferences(PENDING_INSTALL_PREFERENCE, 0);
        int installMode = preferences.getInt(INSTALL_MODE_KEY, -1);
        int minimumBackgroundDuration = preferences.getInt(INSTALL_MIN_BACKGROUND_DURATION, -1);
        if (installMode == -1 || minimumBackgroundDuration == -1) {
            return null;
        }
        return new InstallOptions(InstallMode.fromValue(installMode), minimumBackgroundDuration);
    }

    public void markInstallNeedsConfirmation() {
        Editor editor = this.context.getSharedPreferences(INSTALL_NEEDS_CONFIRMATION, 0).edit();
        editor.putBoolean(INSTALL_NEEDS_CONFIRMATION_KEY, true);
        editor.commit();
    }

    public void clearInstallNeedsConfirmation() {
        clearPreferences(INSTALL_NEEDS_CONFIRMATION);
    }

    public boolean installNeedsConfirmation() {
        return this.context.getSharedPreferences(INSTALL_NEEDS_CONFIRMATION, 0).getBoolean(INSTALL_NEEDS_CONFIRMATION_KEY, false);
    }

    public void saveFirstRunFlag() {
        Editor editor = this.context.getSharedPreferences(FIRST_RUN_PREFERENCE, 0).edit();
        editor.putBoolean(FIRST_RUN_PREFERENCE_KEY, false);
        editor.commit();
    }

    public boolean isFirstRun() {
        return this.context.getSharedPreferences(FIRST_RUN_PREFERENCE, 0).getBoolean(FIRST_RUN_PREFERENCE_KEY, true);
    }

    public void clearPreferences(String preferencesId) {
        Editor editor = this.context.getSharedPreferences(preferencesId, 0).edit();
        editor.clear();
        editor.commit();
    }

    public void saveLastVersion(String labelOrAppVersion, String deploymentKey) {
        Editor editor = this.context.getSharedPreferences(LAST_VERSION_PREFERENCE, 0).edit();
        editor.putString(LAST_VERSION_LABEL_OR_APP_VERSION_KEY, labelOrAppVersion);
        editor.putString(LAST_VERSION_DEPLOYMENT_KEY_KEY, deploymentKey);
        editor.commit();
    }

    public String getLastVersionDeploymentKey() {
        return this.context.getSharedPreferences(LAST_VERSION_PREFERENCE, 0).getString(LAST_VERSION_DEPLOYMENT_KEY_KEY, null);
    }

    public String getLastVersionLabelOrAppVersion() {
        return this.context.getSharedPreferences(LAST_VERSION_PREFERENCE, 0).getString(LAST_VERSION_LABEL_OR_APP_VERSION_KEY, null);
    }
}
