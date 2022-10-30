package org.chromium.base.metrics;

import java.util.concurrent.TimeUnit;
import org.chromium.base.JNINamespace;
import org.chromium.base.VisibleForTesting;

@JNINamespace("base::android")
public class RecordHistogram {
    private static native int nativeGetHistogramValueCountForTesting(String str, int i);

    private static native void nativeInitialize();

    private static native void nativeRecordBooleanHistogram(String str, int i, boolean z);

    private static native void nativeRecordCustomCountHistogram(String str, int i, int i2, int i3, int i4, int i5);

    private static native void nativeRecordCustomTimesHistogramMilliseconds(String str, int i, long j, long j2, long j3, int i2);

    private static native void nativeRecordEnumeratedHistogram(String str, int i, int i2, int i3);

    private static native void nativeRecordSparseHistogram(String str, int i, int i2);

    public static void recordBooleanHistogram(String name, boolean sample) {
        nativeRecordBooleanHistogram(name, System.identityHashCode(name), sample);
    }

    public static void recordEnumeratedHistogram(String name, int sample, int boundary) {
        nativeRecordEnumeratedHistogram(name, System.identityHashCode(name), sample, boundary);
    }

    public static void recordCountHistogram(String name, int sample) {
        recordCustomCountHistogram(name, sample, 1, 1000000, 50);
    }

    public static void recordCount100Histogram(String name, int sample) {
        recordCustomCountHistogram(name, sample, 1, 100, 50);
    }

    public static void recordCustomCountHistogram(String name, int sample, int min, int max, int numBuckets) {
        nativeRecordCustomCountHistogram(name, System.identityHashCode(name), sample, min, max, numBuckets);
    }

    public static void recordSparseSlowlyHistogram(String name, int sample) {
        nativeRecordSparseHistogram(name, System.identityHashCode(name), sample);
    }

    public static void recordTimesHistogram(String name, long duration, TimeUnit timeUnit) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), 1, TimeUnit.SECONDS.toMillis(10), 50);
    }

    public static void recordMediumTimesHistogram(String name, long duration, TimeUnit timeUnit) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), 10, TimeUnit.MINUTES.toMillis(3), 50);
    }

    public static void recordLongTimesHistogram(String name, long duration, TimeUnit timeUnit) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), 1, TimeUnit.HOURS.toMillis(1), 50);
    }

    public static void recordCustomTimesHistogram(String name, long duration, long min, long max, TimeUnit timeUnit, int numBuckets) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), timeUnit.toMillis(min), timeUnit.toMillis(max), numBuckets);
    }

    private static void recordCustomTimesHistogramMilliseconds(String name, long duration, long min, long max, int numBuckets) {
        nativeRecordCustomTimesHistogramMilliseconds(name, System.identityHashCode(name), duration, min, max, numBuckets);
    }

    @VisibleForTesting
    public static int getHistogramValueCountForTesting(String name, int sample) {
        return nativeGetHistogramValueCountForTesting(name, sample);
    }

    public static void initialize() {
        nativeInitialize();
    }
}
