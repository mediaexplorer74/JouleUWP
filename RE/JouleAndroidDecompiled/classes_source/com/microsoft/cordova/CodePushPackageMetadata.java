package com.microsoft.cordova;

import java.io.File;
import org.json.JSONObject;

public class CodePushPackageMetadata {
    public String appVersion;
    public String deploymentKey;
    public boolean isMandatory;
    public String label;
    public String localPath;
    public String nativeBuildTime;
    public String packageDescription;
    public String packageHash;
    public long packageSize;

    static final class JsonField {
        public static final String AppVersion = "appVersion";
        public static final String DeploymentKey = "deploymentKey";
        public static final String Description = "description";
        public static final String IsMandatory = "isMandatory";
        public static final String Label = "label";
        public static final String LocalPath = "localPath";
        public static final String NativeBuildTime = "nativeBuildTime";
        public static final String PackageHash = "packageHash";
        public static final String PackageSize = "packageSize";

        JsonField() {
        }
    }

    public static CodePushPackageMetadata getPackageMetadata(String filePath) {
        Exception e;
        CodePushPackageMetadata result = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String content = Utilities.readFileContents(file);
            CodePushPackageMetadata result2 = new CodePushPackageMetadata();
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has(JsonField.DeploymentKey)) {
                    result2.deploymentKey = jsonObject.getString(JsonField.DeploymentKey);
                }
                if (jsonObject.has(JsonField.Description)) {
                    result2.packageDescription = jsonObject.getString(JsonField.Description);
                }
                if (jsonObject.has(JsonField.Label)) {
                    result2.label = jsonObject.getString(JsonField.Label);
                }
                if (jsonObject.has(JsonField.AppVersion)) {
                    result2.appVersion = jsonObject.getString(JsonField.AppVersion);
                }
                if (jsonObject.has(JsonField.IsMandatory)) {
                    result2.isMandatory = jsonObject.getBoolean(JsonField.IsMandatory);
                }
                if (jsonObject.has(JsonField.PackageHash)) {
                    result2.packageHash = jsonObject.getString(JsonField.PackageHash);
                }
                if (jsonObject.has(JsonField.PackageSize)) {
                    result2.packageSize = jsonObject.getLong(JsonField.PackageSize);
                }
                if (jsonObject.has(JsonField.NativeBuildTime)) {
                    result2.nativeBuildTime = jsonObject.getString(JsonField.NativeBuildTime);
                }
                if (jsonObject.has(JsonField.LocalPath)) {
                    result2.localPath = jsonObject.getString(JsonField.LocalPath);
                }
                return result2;
            } catch (Exception e2) {
                e = e2;
                result = result2;
                Utilities.logException(e);
                return result;
            }
        } catch (Exception e3) {
            e = e3;
            Utilities.logException(e);
            return result;
        }
    }
}
