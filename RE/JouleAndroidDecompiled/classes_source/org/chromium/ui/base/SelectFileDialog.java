package org.chromium.ui.base;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.chromium.base.CalledByNative;
import org.chromium.base.ContentUriUtils;
import org.chromium.base.JNINamespace;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.ui.C0408R;
import org.chromium.ui.UiUtils;
import org.chromium.ui.base.WindowAndroid.IntentCallback;
import org.chromium.ui.base.WindowAndroid.PermissionCallback;

@JNINamespace("ui")
class SelectFileDialog implements IntentCallback, PermissionCallback {
    private static final String ALL_AUDIO_TYPES = "audio/*";
    private static final String ALL_IMAGE_TYPES = "image/*";
    private static final String ALL_VIDEO_TYPES = "video/*";
    private static final String ANY_TYPES = "*/*";
    private static final String AUDIO_TYPE = "audio/";
    private static final String IMAGE_TYPE = "image/";
    private static final String TAG = "SelectFileDialog";
    private static final String VIDEO_TYPE = "video/";
    private boolean mAllowMultiple;
    private Uri mCameraOutputUri;
    private boolean mCapture;
    private List<String> mFileTypes;
    private final long mNativeSelectFileDialog;
    private boolean mSupportsAudioCapture;
    private boolean mSupportsImageCapture;
    private boolean mSupportsVideoCapture;
    private WindowAndroid mWindowAndroid;

    private class GetDisplayNameTask extends AsyncTask<Uri, Void, String[]> {
        final ContentResolver mContentResolver;
        String[] mFilePaths;
        final boolean mIsMultiple;

        public GetDisplayNameTask(ContentResolver contentResolver, boolean isMultiple) {
            this.mContentResolver = contentResolver;
            this.mIsMultiple = isMultiple;
        }

        protected String[] doInBackground(Uri... uris) {
            this.mFilePaths = new String[uris.length];
            String[] displayNames = new String[uris.length];
            int i = 0;
            while (i < uris.length) {
                try {
                    this.mFilePaths[i] = uris[i].toString();
                    displayNames[i] = ContentUriUtils.getDisplayName(uris[i], this.mContentResolver, "_display_name");
                    i++;
                } catch (SecurityException e) {
                    Log.w(SelectFileDialog.TAG, "Unable to extract results from the content provider");
                    return null;
                }
            }
            return displayNames;
        }

        protected void onPostExecute(String[] result) {
            if (result == null) {
                SelectFileDialog.this.onFileNotSelected();
            } else if (this.mIsMultiple) {
                SelectFileDialog.this.nativeOnMultipleFilesSelected(SelectFileDialog.this.mNativeSelectFileDialog, this.mFilePaths, result);
            } else {
                SelectFileDialog.this.nativeOnFileSelected(SelectFileDialog.this.mNativeSelectFileDialog, this.mFilePaths[0], result[0]);
            }
        }
    }

    private native void nativeOnFileNotSelected(long j);

    private native void nativeOnFileSelected(long j, String str, String str2);

    private native void nativeOnMultipleFilesSelected(long j, String[] strArr, String[] strArr2);

    private SelectFileDialog(long nativeSelectFileDialog) {
        this.mNativeSelectFileDialog = nativeSelectFileDialog;
    }

    @TargetApi(18)
    @CalledByNative
    private void selectFile(String[] fileTypes, boolean capture, boolean multiple, WindowAndroid window) {
        this.mFileTypes = new ArrayList(Arrays.asList(fileTypes));
        this.mCapture = capture;
        this.mAllowMultiple = multiple;
        this.mWindowAndroid = window;
        this.mSupportsImageCapture = this.mWindowAndroid.canResolveActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
        this.mSupportsVideoCapture = this.mWindowAndroid.canResolveActivity(new Intent("android.media.action.VIDEO_CAPTURE"));
        this.mSupportsAudioCapture = this.mWindowAndroid.canResolveActivity(new Intent("android.provider.MediaStore.RECORD_SOUND"));
        List<String> missingPermissions = new ArrayList();
        if (((this.mSupportsImageCapture && shouldShowImageTypes()) || (this.mSupportsVideoCapture && shouldShowVideoTypes())) && !window.hasPermission("android.permission.CAMERA")) {
            missingPermissions.add("android.permission.CAMERA");
        }
        if (this.mSupportsAudioCapture && shouldShowAudioTypes() && !window.hasPermission("android.permission.RECORD_AUDIO")) {
            missingPermissions.add("android.permission.RECORD_AUDIO");
        }
        if (missingPermissions.isEmpty()) {
            launchSelectFileIntent();
        } else {
            window.requestPermissions((String[]) missingPermissions.toArray(new String[missingPermissions.size()]), this);
        }
    }

    private void launchSelectFileIntent() {
        boolean hasCameraPermission = this.mWindowAndroid.hasPermission("android.permission.CAMERA");
        boolean hasAudioPermission = this.mWindowAndroid.hasPermission("android.permission.RECORD_AUDIO");
        Intent camera = null;
        if (this.mSupportsImageCapture && hasCameraPermission) {
            camera = getCameraIntent(this.mWindowAndroid.getApplicationContext());
            if (camera == null && captureCamera()) {
                onFileNotSelected();
                return;
            }
        }
        Intent camcorder = null;
        if (this.mSupportsVideoCapture && hasCameraPermission) {
            camcorder = new Intent("android.media.action.VIDEO_CAPTURE");
        }
        Intent soundRecorder = null;
        if (this.mSupportsAudioCapture && hasAudioPermission) {
            soundRecorder = new Intent("android.provider.MediaStore.RECORD_SOUND");
        }
        if (!captureCamera() || camera == null) {
            if (!captureCamcorder() || camcorder == null) {
                if (captureMicrophone() && soundRecorder != null && this.mWindowAndroid.showIntent(soundRecorder, (IntentCallback) this, Integer.valueOf(C0408R.string.low_memory_error))) {
                    return;
                }
            } else if (this.mWindowAndroid.showIntent(camcorder, (IntentCallback) this, Integer.valueOf(C0408R.string.low_memory_error))) {
                return;
            }
        } else if (this.mWindowAndroid.showIntent(camera, (IntentCallback) this, Integer.valueOf(C0408R.string.low_memory_error))) {
            return;
        }
        Intent getContentIntent = new Intent("android.intent.action.GET_CONTENT");
        getContentIntent.addCategory("android.intent.category.OPENABLE");
        if (VERSION.SDK_INT >= 18 && this.mAllowMultiple) {
            getContentIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        }
        ArrayList<Intent> extraIntents = new ArrayList();
        if (!noSpecificType()) {
            if (shouldShowImageTypes()) {
                if (camera != null) {
                    extraIntents.add(camera);
                }
                getContentIntent.setType(ALL_IMAGE_TYPES);
            } else if (shouldShowVideoTypes()) {
                if (camcorder != null) {
                    extraIntents.add(camcorder);
                }
                getContentIntent.setType(ALL_VIDEO_TYPES);
            } else if (shouldShowAudioTypes()) {
                if (soundRecorder != null) {
                    extraIntents.add(soundRecorder);
                }
                getContentIntent.setType(ALL_AUDIO_TYPES);
            }
        }
        if (extraIntents.isEmpty()) {
            getContentIntent.setType(ANY_TYPES);
            if (camera != null) {
                extraIntents.add(camera);
            }
            if (camcorder != null) {
                extraIntents.add(camcorder);
            }
            if (soundRecorder != null) {
                extraIntents.add(soundRecorder);
            }
        }
        Intent chooser = new Intent("android.intent.action.CHOOSER");
        if (!extraIntents.isEmpty()) {
            chooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) extraIntents.toArray(new Intent[0]));
        }
        chooser.putExtra("android.intent.extra.INTENT", getContentIntent);
        if (!this.mWindowAndroid.showIntent(chooser, (IntentCallback) this, Integer.valueOf(C0408R.string.low_memory_error))) {
            onFileNotSelected();
        }
    }

    private Intent getCameraIntent(Context context) {
        Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
        camera.setFlags(3);
        try {
            this.mCameraOutputUri = UiUtils.getUriForImageCaptureFile(context, getFileForImageCapture(context));
        } catch (IOException e) {
            Log.e(TAG, "Cannot retrieve content uri from file", e);
        }
        if (this.mCameraOutputUri == null) {
            return null;
        }
        camera.putExtra("output", this.mCameraOutputUri);
        if (VERSION.SDK_INT < 18) {
            return camera;
        }
        camera.setClipData(ClipData.newUri(context.getContentResolver(), UiUtils.IMAGE_FILE_PATH, this.mCameraOutputUri));
        return camera;
    }

    private File getFileForImageCapture(Context context) throws IOException {
        return File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", UiUtils.getDirectoryForImageCapture(context));
    }

    @TargetApi(18)
    public void onIntentCompleted(WindowAndroid window, int resultCode, ContentResolver contentResolver, Intent results) {
        if (resultCode != -1) {
            onFileNotSelected();
        } else if (results == null || (results.getData() == null && (VERSION.SDK_INT < 18 || results.getClipData() == null))) {
            String path;
            if (AndroidProtocolHandler.FILE_SCHEME.equals(this.mCameraOutputUri.getScheme())) {
                path = this.mCameraOutputUri.getPath();
            } else {
                path = this.mCameraOutputUri.toString();
            }
            nativeOnFileSelected(this.mNativeSelectFileDialog, path, this.mCameraOutputUri.getLastPathSegment());
            window.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", this.mCameraOutputUri));
        } else if (VERSION.SDK_INT >= 18 && results.getData() == null && results.getClipData() != null) {
            ClipData clipData = results.getClipData();
            int itemCount = clipData.getItemCount();
            if (itemCount == 0) {
                onFileNotSelected();
                return;
            }
            Uri[] filePathArray = new Uri[itemCount];
            for (int i = 0; i < itemCount; i++) {
                filePathArray[i] = clipData.getItemAt(i).getUri();
            }
            new GetDisplayNameTask(contentResolver, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filePathArray);
        } else if (AndroidProtocolHandler.FILE_SCHEME.equals(results.getData().getScheme())) {
            nativeOnFileSelected(this.mNativeSelectFileDialog, results.getData().getSchemeSpecificPart(), CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        } else if ("content".equals(results.getScheme())) {
            new GetDisplayNameTask(contentResolver, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Uri[]{results.getData()});
        } else {
            onFileNotSelected();
            window.showError(C0408R.string.opening_file_error);
        }
    }

    public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        for (int i : grantResults) {
            if (i == -1 && this.mCapture) {
                onFileNotSelected();
                return;
            }
        }
        launchSelectFileIntent();
    }

    private void onFileNotSelected() {
        nativeOnFileNotSelected(this.mNativeSelectFileDialog);
    }

    private boolean noSpecificType() {
        return this.mFileTypes.size() != 1 || this.mFileTypes.contains(ANY_TYPES);
    }

    private boolean shouldShowTypes(String allTypes, String specificType) {
        if (noSpecificType() || this.mFileTypes.contains(allTypes)) {
            return true;
        }
        return acceptSpecificType(specificType);
    }

    private boolean shouldShowImageTypes() {
        return shouldShowTypes(ALL_IMAGE_TYPES, IMAGE_TYPE);
    }

    private boolean shouldShowVideoTypes() {
        return shouldShowTypes(ALL_VIDEO_TYPES, VIDEO_TYPE);
    }

    private boolean shouldShowAudioTypes() {
        return shouldShowTypes(ALL_AUDIO_TYPES, AUDIO_TYPE);
    }

    private boolean acceptsSpecificType(String type) {
        return this.mFileTypes.size() == 1 && TextUtils.equals((CharSequence) this.mFileTypes.get(0), type);
    }

    private boolean captureCamera() {
        return this.mCapture && acceptsSpecificType(ALL_IMAGE_TYPES);
    }

    private boolean captureCamcorder() {
        return this.mCapture && acceptsSpecificType(ALL_VIDEO_TYPES);
    }

    private boolean captureMicrophone() {
        return this.mCapture && acceptsSpecificType(ALL_AUDIO_TYPES);
    }

    private boolean acceptSpecificType(String accept) {
        for (String type : this.mFileTypes) {
            if (type.startsWith(accept)) {
                return true;
            }
        }
        return false;
    }

    @CalledByNative
    private static SelectFileDialog create(long nativeSelectFileDialog) {
        return new SelectFileDialog(nativeSelectFileDialog);
    }
}
