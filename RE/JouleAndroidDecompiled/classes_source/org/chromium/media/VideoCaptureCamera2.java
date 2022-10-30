package org.chromium.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import com.google.android.gms.common.ConnectionResult;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.content.browser.ContentViewCore;

@JNINamespace("media")
@TargetApi(21)
public class VideoCaptureCamera2 extends VideoCapture {
    private static final String TAG = "cr.media";
    private static final double kNanoSecondsToFps = 1.0E-9d;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private byte[] mCapturedData;
    private boolean mConfiguringCamera;
    private ImageReader mImageReader;
    private boolean mOpeningCamera;
    private Builder mPreviewBuilder;

    private class CrCaptureSessionListener extends StateCallback {
        private CrCaptureSessionListener() {
        }

        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            Log.m24d(VideoCaptureCamera2.TAG, "onConfigured");
            VideoCaptureCamera2.this.mCaptureSession = cameraCaptureSession;
            VideoCaptureCamera2.this.mConfiguringCamera = false;
            VideoCaptureCamera2.this.createCaptureRequest();
        }

        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Camera session configuration error");
        }
    }

    private class CrImageReaderListener implements OnImageAvailableListener {
        private CrImageReaderListener() {
        }

        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                if (image == null) {
                    if (image != null) {
                        image.close();
                    }
                } else if (image.getFormat() == 35 && image.getPlanes().length == 3) {
                    VideoCaptureCamera2.readImageIntoBuffer(image, VideoCaptureCamera2.this.mCapturedData);
                    VideoCaptureCamera2.this.nativeOnFrameAvailable(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, VideoCaptureCamera2.this.mCapturedData, VideoCaptureCamera2.this.mCapturedData.length, VideoCaptureCamera2.this.getCameraRotation());
                    if (image != null) {
                        image.close();
                    }
                } else {
                    Log.m32e(VideoCaptureCamera2.TAG, "Unexpected image format: %d or #planes: %d", Integer.valueOf(image.getFormat()), Integer.valueOf(image.getPlanes().length));
                    if (image != null) {
                        image.close();
                    }
                }
            } catch (IllegalStateException ex) {
                Log.m32e(VideoCaptureCamera2.TAG, "acquireLatestImage():" + ex, new Object[0]);
                if (image != null) {
                    image.close();
                }
            } catch (Throwable th) {
                if (image != null) {
                    image.close();
                }
            }
        }
    }

    private class CrStateListener extends CameraDevice.StateCallback {
        private CrStateListener() {
        }

        public void onOpened(CameraDevice cameraDevice) {
            VideoCaptureCamera2.this.mCameraDevice = cameraDevice;
            VideoCaptureCamera2.this.mOpeningCamera = false;
            VideoCaptureCamera2.this.mConfiguringCamera = true;
            if (!VideoCaptureCamera2.this.createCaptureObjects()) {
                VideoCaptureCamera2.this.mConfiguringCamera = false;
                VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Error configuring camera");
            }
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            VideoCaptureCamera2.this.mCameraDevice = null;
            VideoCaptureCamera2.this.mOpeningCamera = false;
        }

        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            VideoCaptureCamera2.this.mCameraDevice = null;
            VideoCaptureCamera2.this.mOpeningCamera = false;
            VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Camera device error " + Integer.toString(error));
        }
    }

    private static CameraCharacteristics getCameraCharacteristics(Context appContext, int id) {
        try {
            return ((CameraManager) appContext.getSystemService("camera")).getCameraCharacteristics(Integer.toString(id));
        } catch (CameraAccessException ex) {
            Log.m32e(TAG, "getNumberOfCameras: getCameraIdList(): " + ex, new Object[0]);
            return null;
        }
    }

    private boolean createCaptureObjects() {
        Log.m24d(TAG, "createCaptureObjects");
        if (this.mCameraDevice == null) {
            return false;
        }
        this.mImageReader = ImageReader.newInstance(this.mCaptureFormat.getWidth(), this.mCaptureFormat.getHeight(), this.mCaptureFormat.getPixelFormat(), 2);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());
        this.mImageReader.setOnImageAvailableListener(new CrImageReaderListener(), backgroundHandler);
        try {
            this.mPreviewBuilder = this.mCameraDevice.createCaptureRequest(1);
            if (this.mPreviewBuilder == null) {
                Log.m32e(TAG, "mPreviewBuilder error", new Object[0]);
                return false;
            }
            this.mPreviewBuilder.addTarget(this.mImageReader.getSurface());
            this.mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(1));
            this.mPreviewBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, Integer.valueOf(1));
            this.mPreviewBuilder.set(CaptureRequest.EDGE_MODE, Integer.valueOf(1));
            this.mPreviewBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, Integer.valueOf(1));
            List<Surface> surfaceList = new ArrayList(1);
            surfaceList.add(this.mImageReader.getSurface());
            try {
                this.mCameraDevice.createCaptureSession(surfaceList, new CrCaptureSessionListener(), null);
                return true;
            } catch (CameraAccessException ex) {
                Log.m32e(TAG, "createCaptureSession: " + ex, new Object[0]);
                return false;
            } catch (IllegalArgumentException ex2) {
                Log.m32e(TAG, "createCaptureSession: " + ex2, new Object[0]);
                return false;
            } catch (SecurityException ex3) {
                Log.m32e(TAG, "createCaptureSession: " + ex3, new Object[0]);
                return false;
            }
        } catch (CameraAccessException ex4) {
            Log.m32e(TAG, "createCaptureRequest: " + ex4, new Object[0]);
            return false;
        } catch (IllegalArgumentException ex22) {
            Log.m32e(TAG, "createCaptureRequest: " + ex22, new Object[0]);
            return false;
        } catch (SecurityException ex32) {
            Log.m32e(TAG, "createCaptureRequest: " + ex32, new Object[0]);
            return false;
        }
    }

    private boolean createCaptureRequest() {
        Log.m24d(TAG, "createCaptureRequest");
        try {
            this.mCaptureSession.setRepeatingRequest(this.mPreviewBuilder.build(), null, null);
            return true;
        } catch (CameraAccessException ex) {
            Log.m32e(TAG, "setRepeatingRequest: " + ex, new Object[0]);
            return false;
        } catch (IllegalArgumentException ex2) {
            Log.m32e(TAG, "setRepeatingRequest: " + ex2, new Object[0]);
            return false;
        } catch (SecurityException ex3) {
            Log.m32e(TAG, "setRepeatingRequest: " + ex3, new Object[0]);
            return false;
        }
    }

    private static void readImageIntoBuffer(Image image, byte[] data) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        Plane[] planes = image.getPlanes();
        int offset = 0;
        int plane = 0;
        while (plane < planes.length) {
            ByteBuffer buffer = planes[plane].getBuffer();
            int rowStride = planes[plane].getRowStride();
            int pixelStride = planes[plane].getPixelStride();
            int planeWidth = plane == 0 ? imageWidth : imageWidth / 2;
            int planeHeight = plane == 0 ? imageHeight : imageHeight / 2;
            if (pixelStride == 1 && rowStride == planeWidth) {
                buffer.get(data, offset, planeWidth * planeHeight);
                offset += planeWidth * planeHeight;
            } else {
                int col;
                int offset2;
                byte[] rowData = new byte[rowStride];
                int row = 0;
                while (row < planeHeight - 1) {
                    buffer.get(rowData, 0, rowStride);
                    col = 0;
                    offset2 = offset;
                    while (col < planeWidth) {
                        offset = offset2 + 1;
                        data[offset2] = rowData[col * pixelStride];
                        col++;
                        offset2 = offset;
                    }
                    row++;
                    offset = offset2;
                }
                buffer.get(rowData, 0, Math.min(rowStride, buffer.remaining()));
                col = 0;
                offset2 = offset;
                while (col < planeWidth) {
                    offset = offset2 + 1;
                    data[offset2] = rowData[col * pixelStride];
                    col++;
                    offset2 = offset;
                }
                offset = offset2;
            }
            plane++;
        }
    }

    static boolean isLegacyDevice(Context appContext, int id) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        return cameraCharacteristics != null && ((Integer) cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue() == 2;
    }

    static int getNumberOfCameras(Context appContext) {
        int i = 0;
        try {
            return ((CameraManager) appContext.getSystemService("camera")).getCameraIdList().length;
        } catch (CameraAccessException ex) {
            Log.m32e(TAG, "getNumberOfCameras: getCameraIdList(): " + ex, new Object[i]);
            return i;
        }
    }

    static int getCaptureApiType(int id, Context appContext) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        if (cameraCharacteristics == null) {
            return 5;
        }
        switch (((Integer) cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue()) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                return 3;
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return 2;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return 1;
            default:
                return 1;
        }
    }

    static String getName(int id, Context appContext) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        if (cameraCharacteristics == null) {
            return null;
        }
        return "camera2 " + id + ", facing " + (((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0 ? "front" : "back");
    }

    static VideoCaptureFormat[] getDeviceSupportedFormats(Context appContext, int id) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        if (cameraCharacteristics == null) {
            return null;
        }
        boolean minFrameDurationAvailable = false;
        for (int cap : (int[]) cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)) {
            if (cap == 1) {
                minFrameDurationAvailable = true;
                break;
            }
        }
        ArrayList<VideoCaptureFormat> formatList = new ArrayList();
        StreamConfigurationMap streamMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        for (int format : streamMap.getOutputFormats()) {
            Size[] sizes = streamMap.getOutputSizes(format);
            if (sizes != null) {
                for (Size size : sizes) {
                    double minFrameRate;
                    if (minFrameDurationAvailable) {
                        long minFrameDuration = streamMap.getOutputMinFrameDuration(format, size);
                        if (minFrameDuration == 0) {
                            minFrameRate = 0.0d;
                        } else {
                            minFrameRate = 9.999999999999999E8d * ((double) minFrameDuration);
                        }
                    } else {
                        minFrameRate = 0.0d;
                    }
                    formatList.add(new VideoCaptureFormat(size.getWidth(), size.getHeight(), (int) minFrameRate, 0));
                }
            }
        }
        return (VideoCaptureFormat[]) formatList.toArray(new VideoCaptureFormat[formatList.size()]);
    }

    VideoCaptureCamera2(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        super(context, id, nativeVideoCaptureDeviceAndroid);
        this.mOpeningCamera = false;
        this.mConfiguringCamera = false;
        this.mCameraDevice = null;
        this.mPreviewBuilder = null;
        this.mCaptureSession = null;
        this.mImageReader = null;
    }

    public boolean allocate(int width, int height, int frameRate) {
        Log.m27d(TAG, "allocate: requested (%d x %d) @%dfps", Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(frameRate));
        if (this.mOpeningCamera || this.mConfiguringCamera) {
            Log.m32e(TAG, "allocate() invoked while Camera is busy opening/configuring.", new Object[0]);
            return false;
        }
        this.mCaptureFormat = new VideoCaptureFormat(width, height, frameRate, 35);
        this.mCapturedData = new byte[(((this.mCaptureFormat.mWidth * this.mCaptureFormat.mHeight) * ImageFormat.getBitsPerPixel(this.mCaptureFormat.mPixelFormat)) / 8)];
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(this.mContext, this.mId);
        this.mCameraNativeOrientation = ((Integer) cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
        this.mInvertDeviceOrientationReadings = ((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 1;
        return true;
    }

    public boolean startCapture() {
        Log.m24d(TAG, "startCapture");
        this.mOpeningCamera = true;
        this.mConfiguringCamera = false;
        CameraManager manager = (CameraManager) this.mContext.getSystemService("camera");
        Handler mainHandler = new Handler(this.mContext.getMainLooper());
        try {
            manager.openCamera(Integer.toString(this.mId), new CrStateListener(), mainHandler);
            return true;
        } catch (CameraAccessException ex) {
            Log.m32e(TAG, "allocate: manager.openCamera: " + ex, new Object[0]);
            return false;
        } catch (IllegalArgumentException ex2) {
            Log.m32e(TAG, "allocate: manager.openCamera: " + ex2, new Object[0]);
            return false;
        } catch (SecurityException ex3) {
            Log.m32e(TAG, "allocate: manager.openCamera: " + ex3, new Object[0]);
            return false;
        }
    }

    public boolean stopCapture() {
        Log.m24d(TAG, "stopCapture");
        if (this.mCaptureSession == null) {
            return false;
        }
        try {
            this.mCaptureSession.abortCaptures();
            if (this.mCameraDevice == null) {
                return false;
            }
            this.mCameraDevice.close();
            return true;
        } catch (CameraAccessException ex) {
            Log.m32e(TAG, "abortCaptures: " + ex, new Object[0]);
            return false;
        } catch (IllegalStateException ex2) {
            Log.m32e(TAG, "abortCaptures: " + ex2, new Object[0]);
            return false;
        }
    }

    public void deallocate() {
        Log.m24d(TAG, "deallocate");
    }
}
