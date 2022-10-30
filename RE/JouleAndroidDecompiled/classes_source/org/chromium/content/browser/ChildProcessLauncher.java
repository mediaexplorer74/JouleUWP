package org.chromium.content.browser;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.SurfaceTexture;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Surface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import org.chromium.base.CalledByNative;
import org.chromium.base.CommandLine;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.TraceEvent;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.library_loader.Linker;
import org.chromium.content.app.ChildProcessService;
import org.chromium.content.app.ChromiumLinkerParams;
import org.chromium.content.app.PrivilegedProcessService;
import org.chromium.content.app.SandboxedProcessService;
import org.chromium.content.browser.ChildProcessConnection.ConnectionCallback;
import org.chromium.content.browser.ChildProcessConnection.DeathCallback;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessCallback.Stub;
import org.chromium.content.common.SurfaceWrapper;

@JNINamespace("content")
public class ChildProcessLauncher {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final int CALLBACK_FOR_GPU_PROCESS = 1;
    static final int CALLBACK_FOR_RENDERER_PROCESS = 2;
    static final int CALLBACK_FOR_UNKNOWN_PROCESS = 0;
    static final int CALLBACK_FOR_UTILITY_PROCESS = 3;
    private static final long FREE_CONNECTION_DELAY_MILLIS = 1;
    private static final int NULL_PROCESS_HANDLE = 0;
    private static final String NUM_PRIVILEGED_SERVICES_KEY = "org.chromium.content.browser.NUM_PRIVILEGED_SERVICES";
    private static final String NUM_SANDBOXED_SERVICES_KEY = "org.chromium.content.browser.NUM_SANDBOXED_SERVICES";
    private static final String SWITCH_GPU_PROCESS = "gpu-process";
    @VisibleForTesting
    public static final String SWITCH_NUM_SANDBOXED_SERVICES_FOR_TESTING = "num-sandboxed-services";
    private static final String SWITCH_PROCESS_TYPE = "type";
    private static final String SWITCH_RENDERER_PROCESS = "renderer";
    private static final String SWITCH_UTILITY_PROCESS = "utility";
    private static final String TAG = "cr.ChildProcessLaunch";
    private static boolean sApplicationInForeground;
    private static BindingManager sBindingManager;
    private static boolean sLinkerInitialized;
    private static long sLinkerLoadAddress;
    private static final PendingSpawnQueue sPendingSpawnQueue;
    private static ChildConnectionAllocator sPrivilegedChildConnectionAllocator;
    private static ChildConnectionAllocator sSandboxedChildConnectionAllocator;
    private static Map<Integer, ChildProcessConnection> sServiceMap;
    private static ChildProcessConnection sSpareSandboxedConnection;
    private static Map<Pair<Integer, Integer>, Surface> sSurfaceTextureSurfaceMap;
    private static Map<Integer, Surface> sViewSurfaceMap;

    /* renamed from: org.chromium.content.browser.ChildProcessLauncher.2 */
    static class C03302 implements Runnable {
        final /* synthetic */ ChildProcessConnection val$conn;

        /* renamed from: org.chromium.content.browser.ChildProcessLauncher.2.1 */
        class C03291 implements Runnable {
            final /* synthetic */ PendingSpawnData val$pendingSpawn;

            C03291(PendingSpawnData pendingSpawnData) {
                this.val$pendingSpawn = pendingSpawnData;
            }

            public void run() {
                ChildProcessLauncher.startInternal(this.val$pendingSpawn.context(), this.val$pendingSpawn.commandLine(), this.val$pendingSpawn.childProcessId(), this.val$pendingSpawn.filesToBeMapped(), this.val$pendingSpawn.clientContext(), this.val$pendingSpawn.callbackType(), this.val$pendingSpawn.inSandbox());
            }
        }

        C03302(ChildProcessConnection childProcessConnection) {
            this.val$conn = childProcessConnection;
        }

        public void run() {
            ChildProcessLauncher.getConnectionAllocator(this.val$conn.isInSandbox()).free(this.val$conn);
            PendingSpawnData pendingSpawn = ChildProcessLauncher.sPendingSpawnQueue.dequeue();
            if (pendingSpawn != null) {
                new Thread(new C03291(pendingSpawn)).start();
            }
        }
    }

    private static class ChildConnectionAllocator {
        static final /* synthetic */ boolean $assertionsDisabled;
        private Class<? extends ChildProcessService> mChildClass;
        private final ChildProcessConnection[] mChildProcessConnections;
        private final Object mConnectionLock;
        private final ArrayList<Integer> mFreeConnectionIndices;
        private final boolean mInSandbox;

        static {
            $assertionsDisabled = !ChildProcessLauncher.class.desiredAssertionStatus() ? true : ChildProcessLauncher.$assertionsDisabled;
        }

        public ChildConnectionAllocator(boolean inSandbox, int numChildServices) {
            this.mConnectionLock = new Object();
            this.mChildProcessConnections = new ChildProcessConnectionImpl[numChildServices];
            this.mFreeConnectionIndices = new ArrayList(numChildServices);
            for (int i = ChildProcessLauncher.NULL_PROCESS_HANDLE; i < numChildServices; i += ChildProcessLauncher.CALLBACK_FOR_GPU_PROCESS) {
                this.mFreeConnectionIndices.add(Integer.valueOf(i));
            }
            this.mChildClass = inSandbox ? SandboxedProcessService.class : PrivilegedProcessService.class;
            this.mInSandbox = inSandbox;
        }

        public ChildProcessConnection allocate(Context context, DeathCallback deathCallback, ChromiumLinkerParams chromiumLinkerParams, boolean alwaysInForeground) {
            ChildProcessConnection childProcessConnection;
            synchronized (this.mConnectionLock) {
                if (this.mFreeConnectionIndices.isEmpty()) {
                    Log.m24d(ChildProcessLauncher.TAG, "Ran out of services to allocate.");
                    childProcessConnection = null;
                } else {
                    int slot = ((Integer) this.mFreeConnectionIndices.remove(ChildProcessLauncher.NULL_PROCESS_HANDLE)).intValue();
                    if ($assertionsDisabled || this.mChildProcessConnections[slot] == null) {
                        this.mChildProcessConnections[slot] = new ChildProcessConnectionImpl(context, slot, this.mInSandbox, deathCallback, this.mChildClass, chromiumLinkerParams, alwaysInForeground);
                        Log.m26d(ChildProcessLauncher.TAG, "Allocator allocated a connection, sandbox: %b, slot: %d", Boolean.valueOf(this.mInSandbox), Integer.valueOf(slot));
                        childProcessConnection = this.mChildProcessConnections[slot];
                    } else {
                        throw new AssertionError();
                    }
                }
            }
            return childProcessConnection;
        }

        public void free(ChildProcessConnection connection) {
            synchronized (this.mConnectionLock) {
                int slot = connection.getServiceNumber();
                if (this.mChildProcessConnections[slot] != connection) {
                    int occupier = this.mChildProcessConnections[slot] == null ? -1 : this.mChildProcessConnections[slot].getServiceNumber();
                    Object[] objArr = new Object[ChildProcessLauncher.CALLBACK_FOR_RENDERER_PROCESS];
                    objArr[ChildProcessLauncher.NULL_PROCESS_HANDLE] = Integer.valueOf(slot);
                    objArr[ChildProcessLauncher.CALLBACK_FOR_GPU_PROCESS] = Integer.valueOf(occupier);
                    Log.m32e(ChildProcessLauncher.TAG, "Unable to find connection to free in slot: %d already occupied by service: %d", objArr);
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                }
                this.mChildProcessConnections[slot] = null;
                if ($assertionsDisabled || !this.mFreeConnectionIndices.contains(Integer.valueOf(slot))) {
                    this.mFreeConnectionIndices.add(Integer.valueOf(slot));
                    Log.m26d(ChildProcessLauncher.TAG, "Allocator freed a connection, sandbox: %b, slot: %d", Boolean.valueOf(this.mInSandbox), Integer.valueOf(slot));
                } else {
                    throw new AssertionError();
                }
            }
        }

        public boolean isFreeConnectionAvailable() {
            boolean z;
            synchronized (this.mConnectionLock) {
                z = !this.mFreeConnectionIndices.isEmpty() ? true : ChildProcessLauncher.$assertionsDisabled;
            }
            return z;
        }

        @VisibleForTesting
        int allocatedConnectionsCountForTesting() {
            return this.mChildProcessConnections.length - this.mFreeConnectionIndices.size();
        }
    }

    private static class PendingSpawnData {
        private final int mCallbackType;
        private final int mChildProcessId;
        private final long mClientContext;
        private final String[] mCommandLine;
        private final Context mContext;
        private final FileDescriptorInfo[] mFilesToBeMapped;
        private final boolean mInSandbox;

        private PendingSpawnData(Context context, String[] commandLine, int childProcessId, FileDescriptorInfo[] filesToBeMapped, long clientContext, int callbackType, boolean inSandbox) {
            this.mContext = context;
            this.mCommandLine = commandLine;
            this.mChildProcessId = childProcessId;
            this.mFilesToBeMapped = filesToBeMapped;
            this.mClientContext = clientContext;
            this.mCallbackType = callbackType;
            this.mInSandbox = inSandbox;
        }

        private Context context() {
            return this.mContext;
        }

        private String[] commandLine() {
            return this.mCommandLine;
        }

        private int childProcessId() {
            return this.mChildProcessId;
        }

        private FileDescriptorInfo[] filesToBeMapped() {
            return this.mFilesToBeMapped;
        }

        private long clientContext() {
            return this.mClientContext;
        }

        private int callbackType() {
            return this.mCallbackType;
        }

        private boolean inSandbox() {
            return this.mInSandbox;
        }
    }

    private static class PendingSpawnQueue {
        private static Queue<PendingSpawnData> sPendingSpawns;
        static final Object sPendingSpawnsLock;

        private PendingSpawnQueue() {
        }

        static {
            sPendingSpawns = new LinkedList();
            sPendingSpawnsLock = new Object();
        }

        public void enqueue(PendingSpawnData pendingSpawn) {
            synchronized (sPendingSpawnsLock) {
                sPendingSpawns.add(pendingSpawn);
            }
        }

        public PendingSpawnData dequeue() {
            PendingSpawnData pendingSpawnData;
            synchronized (sPendingSpawnsLock) {
                pendingSpawnData = (PendingSpawnData) sPendingSpawns.poll();
            }
            return pendingSpawnData;
        }

        public int size() {
            int size;
            synchronized (sPendingSpawnsLock) {
                size = sPendingSpawns.size();
            }
            return size;
        }
    }

    /* renamed from: org.chromium.content.browser.ChildProcessLauncher.1 */
    static class C05971 implements DeathCallback {
        C05971() {
        }

        public void onChildProcessDied(ChildProcessConnection connection) {
            if (connection.getPid() != 0) {
                ChildProcessLauncher.stop(connection.getPid());
            } else {
                ChildProcessLauncher.freeConnection(connection);
            }
        }
    }

    /* renamed from: org.chromium.content.browser.ChildProcessLauncher.3 */
    static class C05983 implements ConnectionCallback {
        final /* synthetic */ int val$callbackType;
        final /* synthetic */ long val$clientContext;
        final /* synthetic */ ChildProcessConnection val$connection;

        C05983(long j, int i, ChildProcessConnection childProcessConnection) {
            this.val$clientContext = j;
            this.val$callbackType = i;
            this.val$connection = childProcessConnection;
        }

        public void onConnected(int pid) {
            Log.m27d(ChildProcessLauncher.TAG, "on connect callback, pid=%d context=%d callbackType=%d", Integer.valueOf(pid), Long.valueOf(this.val$clientContext), Integer.valueOf(this.val$callbackType));
            if (pid != 0) {
                ChildProcessLauncher.sBindingManager.addNewConnection(pid, this.val$connection);
                ChildProcessLauncher.sServiceMap.put(Integer.valueOf(pid), this.val$connection);
            }
            if (this.val$clientContext != 0) {
                ChildProcessLauncher.nativeOnChildProcessStarted(this.val$clientContext, pid);
            }
        }
    }

    /* renamed from: org.chromium.content.browser.ChildProcessLauncher.4 */
    static class C06784 extends Stub {
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ int val$callbackType;
        final /* synthetic */ int val$childProcessId;

        static {
            $assertionsDisabled = !ChildProcessLauncher.class.desiredAssertionStatus() ? true : ChildProcessLauncher.$assertionsDisabled;
        }

        C06784(int i, int i2) {
            this.val$callbackType = i;
            this.val$childProcessId = i2;
        }

        public void establishSurfacePeer(int pid, Surface surface, int primaryID, int secondaryID) {
            if (this.val$callbackType != ChildProcessLauncher.CALLBACK_FOR_GPU_PROCESS) {
                Log.m32e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[ChildProcessLauncher.NULL_PROCESS_HANDLE]);
            } else {
                ChildProcessLauncher.nativeEstablishSurfacePeer(pid, surface, primaryID, secondaryID);
            }
        }

        public SurfaceWrapper getViewSurface(int surfaceId) {
            if (this.val$callbackType != ChildProcessLauncher.CALLBACK_FOR_GPU_PROCESS) {
                Log.m32e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[ChildProcessLauncher.NULL_PROCESS_HANDLE]);
                return null;
            }
            Surface surface = (Surface) ChildProcessLauncher.sViewSurfaceMap.get(Integer.valueOf(surfaceId));
            if (surface == null) {
                Log.m32e(ChildProcessLauncher.TAG, "Invalid surfaceId.", new Object[ChildProcessLauncher.NULL_PROCESS_HANDLE]);
                return null;
            } else if ($assertionsDisabled || surface.isValid()) {
                return new SurfaceWrapper(surface);
            } else {
                throw new AssertionError();
            }
        }

        public void registerSurfaceTextureSurface(int surfaceTextureId, int clientId, Surface surface) {
            if (this.val$callbackType != ChildProcessLauncher.CALLBACK_FOR_GPU_PROCESS) {
                Log.m32e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[ChildProcessLauncher.NULL_PROCESS_HANDLE]);
            } else {
                ChildProcessLauncher.registerSurfaceTextureSurface(surfaceTextureId, clientId, surface);
            }
        }

        public void unregisterSurfaceTextureSurface(int surfaceTextureId, int clientId) {
            if (this.val$callbackType != ChildProcessLauncher.CALLBACK_FOR_GPU_PROCESS) {
                Log.m32e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[ChildProcessLauncher.NULL_PROCESS_HANDLE]);
            } else {
                ChildProcessLauncher.unregisterSurfaceTextureSurface(surfaceTextureId, clientId);
            }
        }

        public SurfaceWrapper getSurfaceTextureSurface(int surfaceTextureId) {
            if (this.val$callbackType == ChildProcessLauncher.CALLBACK_FOR_RENDERER_PROCESS) {
                return ChildProcessLauncher.getSurfaceTextureSurface(surfaceTextureId, this.val$childProcessId);
            }
            Log.m32e(ChildProcessLauncher.TAG, "Illegal callback for non-renderer process.", new Object[ChildProcessLauncher.NULL_PROCESS_HANDLE]);
            return null;
        }
    }

    private static native void nativeEstablishSurfacePeer(int i, Surface surface, int i2, int i3);

    private static native boolean nativeIsSingleProcess();

    private static native void nativeOnChildProcessStarted(long j, int i);

    static {
        boolean z;
        if (ChildProcessLauncher.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        sPendingSpawnQueue = new PendingSpawnQueue();
        sLinkerInitialized = $assertionsDisabled;
        sLinkerLoadAddress = 0;
        sServiceMap = new ConcurrentHashMap();
        sSpareSandboxedConnection = null;
        sBindingManager = BindingManagerImpl.createBindingManager();
        sViewSurfaceMap = new ConcurrentHashMap();
        sSurfaceTextureSurfaceMap = new ConcurrentHashMap();
        sApplicationInForeground = true;
    }

    private static int getNumberOfServices(Context context, boolean inSandbox) {
        try {
            int numServices = context.getPackageManager().getApplicationInfo(context.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).metaData.getInt(inSandbox ? NUM_SANDBOXED_SERVICES_KEY : NUM_PRIVILEGED_SERVICES_KEY);
            if (inSandbox && CommandLine.getInstance().hasSwitch(SWITCH_NUM_SANDBOXED_SERVICES_FOR_TESTING)) {
                String value = CommandLine.getInstance().getSwitchValue(SWITCH_NUM_SANDBOXED_SERVICES_FOR_TESTING);
                if (!TextUtils.isEmpty(value)) {
                    try {
                        numServices = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        Log.m42w(TAG, "The value of --num-sandboxed-services is formatted wrongly: " + value, new Object[NULL_PROCESS_HANDLE]);
                    }
                }
            }
            if (numServices > 0) {
                return numServices;
            }
            throw new RuntimeException("Illegal meta data value for number of child services");
        } catch (NameNotFoundException e2) {
            throw new RuntimeException("Could not get application info");
        }
    }

    private static void initConnectionAllocatorsIfNecessary(Context context) {
        synchronized (ChildProcessLauncher.class) {
            if (sSandboxedChildConnectionAllocator == null) {
                sSandboxedChildConnectionAllocator = new ChildConnectionAllocator(true, getNumberOfServices(context, true));
            }
            if (sPrivilegedChildConnectionAllocator == null) {
                sPrivilegedChildConnectionAllocator = new ChildConnectionAllocator($assertionsDisabled, getNumberOfServices(context, $assertionsDisabled));
            }
        }
    }

    private static ChildConnectionAllocator getConnectionAllocator(boolean inSandbox) {
        return inSandbox ? sSandboxedChildConnectionAllocator : sPrivilegedChildConnectionAllocator;
    }

    private static ChildProcessConnection allocateConnection(Context context, boolean inSandbox, ChromiumLinkerParams chromiumLinkerParams, boolean alwaysInForeground) {
        DeathCallback deathCallback = new C05971();
        initConnectionAllocatorsIfNecessary(context);
        return getConnectionAllocator(inSandbox).allocate(context, deathCallback, chromiumLinkerParams, alwaysInForeground);
    }

    private static ChromiumLinkerParams getLinkerParamsForNewConnection() {
        Linker linker = Linker.getInstance();
        if (!sLinkerInitialized) {
            if (linker.isUsed()) {
                sLinkerLoadAddress = linker.getBaseLoadAddress();
                if (sLinkerLoadAddress == 0) {
                    Log.m33i(TAG, "Shared RELRO support disabled!", new Object[NULL_PROCESS_HANDLE]);
                }
            }
            sLinkerInitialized = true;
        }
        if (sLinkerLoadAddress == 0) {
            return null;
        }
        return new ChromiumLinkerParams(sLinkerLoadAddress, true, linker.getTestRunnerClassName());
    }

    private static ChildProcessConnection allocateBoundConnection(Context context, String[] commandLine, boolean inSandbox, boolean alwaysInForeground) {
        ChildProcessConnection connection = allocateConnection(context, inSandbox, getLinkerParamsForNewConnection(), alwaysInForeground);
        if (connection != null) {
            connection.start(commandLine);
            if (inSandbox && !sSandboxedChildConnectionAllocator.isFreeConnectionAvailable()) {
                sBindingManager.releaseAllModerateBindings();
            }
        }
        return connection;
    }

    private static void freeConnection(ChildProcessConnection connection) {
        if (connection.equals(sSpareSandboxedConnection)) {
            sSpareSandboxedConnection = null;
        }
        ThreadUtils.postOnUiThreadDelayed(new C03302(connection), FREE_CONNECTION_DELAY_MILLIS);
    }

    @VisibleForTesting
    public static void setBindingManagerForTesting(BindingManager manager) {
        sBindingManager = manager;
    }

    @CalledByNative
    private static boolean isOomProtected(int pid) {
        return sBindingManager.isOomProtected(pid);
    }

    @CalledByNative
    private static void registerViewSurface(int surfaceId, Surface surface) {
        sViewSurfaceMap.put(Integer.valueOf(surfaceId), surface);
    }

    @CalledByNative
    private static void unregisterViewSurface(int surfaceId) {
        sViewSurfaceMap.remove(Integer.valueOf(surfaceId));
    }

    private static void registerSurfaceTextureSurface(int surfaceTextureId, int clientId, Surface surface) {
        sSurfaceTextureSurfaceMap.put(new Pair(Integer.valueOf(surfaceTextureId), Integer.valueOf(clientId)), surface);
    }

    private static void unregisterSurfaceTextureSurface(int surfaceTextureId, int clientId) {
        Surface surface = (Surface) sSurfaceTextureSurfaceMap.remove(new Pair(Integer.valueOf(surfaceTextureId), Integer.valueOf(clientId)));
        if (surface != null) {
            if ($assertionsDisabled || surface.isValid()) {
                surface.release();
                return;
            }
            throw new AssertionError();
        }
    }

    @CalledByNative
    private static void createSurfaceTextureSurface(int surfaceTextureId, int clientId, SurfaceTexture surfaceTexture) {
        registerSurfaceTextureSurface(surfaceTextureId, clientId, new Surface(surfaceTexture));
    }

    @CalledByNative
    private static void destroySurfaceTextureSurface(int surfaceTextureId, int clientId) {
        unregisterSurfaceTextureSurface(surfaceTextureId, clientId);
    }

    @CalledByNative
    private static SurfaceWrapper getSurfaceTextureSurface(int surfaceTextureId, int clientId) {
        Surface surface = (Surface) sSurfaceTextureSurfaceMap.get(new Pair(Integer.valueOf(surfaceTextureId), Integer.valueOf(clientId)));
        if (surface == null) {
            Log.m32e(TAG, "Invalid Id for surface texture.", new Object[NULL_PROCESS_HANDLE]);
            return null;
        } else if ($assertionsDisabled || surface.isValid()) {
            return new SurfaceWrapper(surface);
        } else {
            throw new AssertionError();
        }
    }

    @CalledByNative
    public static void setInForeground(int pid, boolean inForeground) {
        sBindingManager.setInForeground(pid, inForeground);
    }

    public static void determinedVisibility(int pid) {
        sBindingManager.determinedVisibility(pid);
    }

    public static void onSentToBackground() {
        sApplicationInForeground = $assertionsDisabled;
        sBindingManager.onSentToBackground();
    }

    public static void startModerateBindingManagement(Context context, float lowReduceRatio, float highReduceRatio) {
        sBindingManager.startModerateBindingManagement(context, getNumberOfServices(context, true), lowReduceRatio, highReduceRatio);
    }

    public static void onBroughtToForeground() {
        sApplicationInForeground = true;
        sBindingManager.onBroughtToForeground();
    }

    static boolean isApplicationInForeground() {
        return sApplicationInForeground;
    }

    public static void warmUp(Context context) {
        synchronized (ChildProcessLauncher.class) {
            if ($assertionsDisabled || !ThreadUtils.runningOnUiThread()) {
                if (sSpareSandboxedConnection == null) {
                    sSpareSandboxedConnection = allocateBoundConnection(context, null, true, $assertionsDisabled);
                }
            } else {
                throw new AssertionError();
            }
        }
    }

    private static String getSwitchValue(String[] commandLine, String switchKey) {
        if (commandLine == null || switchKey == null) {
            return null;
        }
        String switchKeyPrefix = "--" + switchKey + "=";
        String[] arr$ = commandLine;
        int len$ = arr$.length;
        for (int i$ = NULL_PROCESS_HANDLE; i$ < len$; i$ += CALLBACK_FOR_GPU_PROCESS) {
            String command = arr$[i$];
            if (command != null && command.startsWith(switchKeyPrefix)) {
                return command.substring(switchKeyPrefix.length());
            }
        }
        return null;
    }

    @CalledByNative
    private static FileDescriptorInfo makeFdInfo(int id, int fd, boolean autoClose, long offset, long size) {
        ParcelFileDescriptor pFd;
        if (autoClose) {
            pFd = ParcelFileDescriptor.adoptFd(fd);
        } else {
            try {
                pFd = ParcelFileDescriptor.fromFd(fd);
            } catch (IOException e) {
                Object[] objArr = new Object[CALLBACK_FOR_GPU_PROCESS];
                objArr[NULL_PROCESS_HANDLE] = e;
                Log.m32e(TAG, "Invalid FD provided for process connection, aborting connection.", objArr);
                return null;
            }
        }
        return new FileDescriptorInfo(id, pFd, offset, size);
    }

    @CalledByNative
    private static void start(Context context, String[] commandLine, int childProcessId, FileDescriptorInfo[] filesToBeMapped, long clientContext) {
        if ($assertionsDisabled || clientContext != 0) {
            int callbackType = NULL_PROCESS_HANDLE;
            boolean inSandbox = true;
            String processType = getSwitchValue(commandLine, SWITCH_PROCESS_TYPE);
            if (SWITCH_RENDERER_PROCESS.equals(processType)) {
                callbackType = CALLBACK_FOR_RENDERER_PROCESS;
            } else if (SWITCH_GPU_PROCESS.equals(processType)) {
                callbackType = CALLBACK_FOR_GPU_PROCESS;
                inSandbox = $assertionsDisabled;
            } else if (SWITCH_UTILITY_PROCESS.equals(processType)) {
                callbackType = CALLBACK_FOR_UTILITY_PROCESS;
            } else if (!$assertionsDisabled) {
                throw new AssertionError();
            }
            startInternal(context, commandLine, childProcessId, filesToBeMapped, clientContext, callbackType, inSandbox);
            return;
        }
        throw new AssertionError();
    }

    private static void startInternal(Context context, String[] commandLine, int childProcessId, FileDescriptorInfo[] filesToBeMapped, long clientContext, int callbackType, boolean inSandbox) {
        try {
            TraceEvent.begin("ChildProcessLauncher.startInternal");
            ChildProcessConnection allocatedConnection = null;
            synchronized (ChildProcessLauncher.class) {
                if (inSandbox) {
                    allocatedConnection = sSpareSandboxedConnection;
                    sSpareSandboxedConnection = null;
                }
            }
            if (allocatedConnection == null) {
                boolean alwaysInForeground = $assertionsDisabled;
                if (callbackType == CALLBACK_FOR_GPU_PROCESS) {
                    alwaysInForeground = true;
                }
                ChildProcessConnection allocatedConnection2 = allocateBoundConnection(context, commandLine, inSandbox, alwaysInForeground);
                if (allocatedConnection2 == null) {
                    Log.m24d(TAG, "Allocation of new service failed. Queuing up pending spawn.");
                    PendingSpawnQueue pendingSpawnQueue = sPendingSpawnQueue;
                    r16.enqueue(new PendingSpawnData(commandLine, childProcessId, filesToBeMapped, clientContext, callbackType, inSandbox, null));
                    allocatedConnection = allocatedConnection2;
                    return;
                }
                allocatedConnection = allocatedConnection2;
            }
            Log.m25d(TAG, "Setting up connection to process: slot=%d", Integer.valueOf(allocatedConnection.getServiceNumber()));
            triggerConnectionSetup(allocatedConnection, commandLine, childProcessId, filesToBeMapped, callbackType, clientContext);
            TraceEvent.end("ChildProcessLauncher.startInternal");
        } finally {
            TraceEvent.end("ChildProcessLauncher.startInternal");
        }
    }

    @VisibleForTesting
    static void triggerConnectionSetup(ChildProcessConnection connection, String[] commandLine, int childProcessId, FileDescriptorInfo[] filesToBeMapped, int callbackType, long clientContext) {
        ConnectionCallback connectionCallback = new C05983(clientContext, callbackType, connection);
        if ($assertionsDisabled || callbackType != 0) {
            connection.setupConnection(commandLine, filesToBeMapped, createCallback(childProcessId, callbackType), connectionCallback, Linker.getInstance().getSharedRelros());
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    static void stop(int pid) {
        Log.m25d(TAG, "stopping child connection: pid=%d", Integer.valueOf(pid));
        ChildProcessConnection connection = (ChildProcessConnection) sServiceMap.remove(Integer.valueOf(pid));
        if (connection == null) {
            logPidWarning(pid, "Tried to stop non-existent connection");
            return;
        }
        sBindingManager.clearConnection(pid);
        connection.stop();
        freeConnection(connection);
    }

    private static IChildProcessCallback createCallback(int childProcessId, int callbackType) {
        return new C06784(callbackType, childProcessId);
    }

    static void logPidWarning(int pid, String message) {
        if (pid > 0 && !nativeIsSingleProcess()) {
            Object[] objArr = new Object[CALLBACK_FOR_RENDERER_PROCESS];
            objArr[NULL_PROCESS_HANDLE] = message;
            objArr[CALLBACK_FOR_GPU_PROCESS] = Integer.valueOf(pid);
            Log.m42w(TAG, "%s, pid=%d", objArr);
        }
    }

    @VisibleForTesting
    static ChildProcessConnection allocateBoundConnectionForTesting(Context context) {
        return allocateBoundConnection(context, null, true, $assertionsDisabled);
    }

    @VisibleForTesting
    static void enqueuePendingSpawnForTesting(Context context) {
        sPendingSpawnQueue.enqueue(new PendingSpawnData(new String[NULL_PROCESS_HANDLE], CALLBACK_FOR_GPU_PROCESS, new FileDescriptorInfo[NULL_PROCESS_HANDLE], 0, CALLBACK_FOR_RENDERER_PROCESS, true, null));
    }

    @VisibleForTesting
    static int allocatedConnectionsCountForTesting(Context context) {
        initConnectionAllocatorsIfNecessary(context);
        return sSandboxedChildConnectionAllocator.allocatedConnectionsCountForTesting();
    }

    @VisibleForTesting
    static int connectedServicesCountForTesting() {
        return sServiceMap.size();
    }

    @VisibleForTesting
    static int pendingSpawnsCountForTesting() {
        return sPendingSpawnQueue.size();
    }

    @VisibleForTesting
    public static boolean crashProcessForTesting(int pid) {
        if (sServiceMap.get(Integer.valueOf(pid)) == null) {
            return $assertionsDisabled;
        }
        try {
            ((ChildProcessConnectionImpl) sServiceMap.get(Integer.valueOf(pid))).crashServiceForTesting();
            return true;
        } catch (RemoteException e) {
            return $assertionsDisabled;
        }
    }
}
