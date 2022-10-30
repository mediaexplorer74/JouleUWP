package com.microsoft.cordova;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;

public class CodePushPackageManager {
    public static final String CODEPUSH_CURRENT_PACKAGE_PATH = "/codepush/currentPackage.json";
    public static final String CODEPUSH_OLD_PACKAGE_PATH = "/codepush/oldPackage.json";
    private CodePushPreferences codePushPreferences;
    private Context context;

    public CodePushPackageManager(Context context, CodePushPreferences codePushPreferences) {
        this.context = context;
        this.codePushPreferences = codePushPreferences;
    }

    public void revertToPreviousVersion() {
        CodePushPackageMetadata failedUpdateMetadata = getCurrentPackageMetadata();
        if (failedUpdateMetadata != null) {
            if (failedUpdateMetadata.packageHash != null) {
                this.codePushPreferences.saveFailedUpdate(failedUpdateMetadata.packageHash);
            }
            File failedUpdateDir = new File(this.context.getFilesDir() + failedUpdateMetadata.localPath);
            if (failedUpdateDir.exists()) {
                Utilities.deleteEntryRecursively(failedUpdateDir);
            }
        }
        File currentFile = new File(this.context.getFilesDir() + CODEPUSH_CURRENT_PACKAGE_PATH);
        File oldFile = new File(this.context.getFilesDir() + CODEPUSH_OLD_PACKAGE_PATH);
        if (currentFile.exists()) {
            currentFile.delete();
        }
        if (oldFile.exists()) {
            oldFile.renameTo(currentFile);
        }
    }

    public void cleanDeployments() {
        File file = new File(this.context.getFilesDir() + "/codepush");
        if (file.exists()) {
            Utilities.deleteEntryRecursively(file);
        }
    }

    public void cleanOldPackage() throws IOException, JSONException {
        CodePushPackageMetadata oldPackageMetadata = getOldPackageMetadata();
        if (oldPackageMetadata != null) {
            File file = new File(this.context.getFilesDir() + oldPackageMetadata.localPath);
            if (file.exists()) {
                Utilities.deleteEntryRecursively(file);
            }
        }
    }

    public CodePushPackageMetadata getOldPackageMetadata() {
        return CodePushPackageMetadata.getPackageMetadata(this.context.getFilesDir() + CODEPUSH_OLD_PACKAGE_PATH);
    }

    public CodePushPackageMetadata getCurrentPackageMetadata() {
        return CodePushPackageMetadata.getPackageMetadata(this.context.getFilesDir() + CODEPUSH_CURRENT_PACKAGE_PATH);
    }

    public String getCachedBinaryHash() {
        return this.codePushPreferences.getCachedBinaryHash();
    }

    public void saveBinaryHash(String binaryHash) {
        this.codePushPreferences.saveBinaryHash(binaryHash);
    }

    public boolean isFailedUpdate(String packageHash) {
        return this.codePushPreferences.isFailedUpdate(packageHash);
    }

    public void clearFailedUpdates() {
        this.codePushPreferences.clearFailedUpdates();
    }

    public void savePendingInstall(InstallOptions options) {
        this.codePushPreferences.savePendingInstall(options);
    }

    public InstallOptions getPendingInstall() {
        return this.codePushPreferences.getPendingInstall();
    }

    public void clearPendingInstall() {
        this.codePushPreferences.clearPendingInstall();
    }

    public void markInstallNeedsConfirmation() {
        this.codePushPreferences.markInstallNeedsConfirmation();
    }

    public void clearInstallNeedsConfirmation() {
        this.codePushPreferences.clearInstallNeedsConfirmation();
    }

    public boolean installNeedsConfirmation() {
        return this.codePushPreferences.installNeedsConfirmation();
    }

    public boolean isFirstRun() {
        return this.codePushPreferences.isFirstRun();
    }

    public void saveFirstRunFlag() {
        this.codePushPreferences.saveFirstRunFlag();
    }
}
