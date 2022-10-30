package org.chromium.content.browser;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.util.List;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;

public class LocationProviderFactory {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static LocationProvider sProviderImpl;

    public interface LocationProvider {
        boolean isRunning();

        void start(boolean z);

        void stop();
    }

    private static class LocationProviderImpl implements LocationListener, LocationProvider {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final String TAG = "cr.LocationProvider";
        private Context mContext;
        private boolean mIsRunning;
        private LocationManager mLocationManager;

        /* renamed from: org.chromium.content.browser.LocationProviderFactory.LocationProviderImpl.1 */
        class C03451 implements Runnable {
            final /* synthetic */ Location val$location;

            C03451(Location location) {
                this.val$location = location;
            }

            public void run() {
                LocationProviderImpl.this.updateNewLocation(this.val$location);
            }
        }

        static {
            $assertionsDisabled = !LocationProviderFactory.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        LocationProviderImpl(Context context) {
            this.mContext = context;
        }

        public void start(boolean gpsEnabled) {
            unregisterFromLocationUpdates();
            registerForLocationUpdates(gpsEnabled);
        }

        public void stop() {
            unregisterFromLocationUpdates();
        }

        public boolean isRunning() {
            return this.mIsRunning;
        }

        public void onLocationChanged(Location location) {
            if (this.mIsRunning) {
                updateNewLocation(location);
            }
        }

        private void updateNewLocation(Location location) {
            LocationProviderAdapter.newLocationAvailable(location.getLatitude(), location.getLongitude(), ((double) location.getTime()) / 1000.0d, location.hasAltitude(), location.getAltitude(), location.hasAccuracy(), (double) location.getAccuracy(), location.hasBearing(), (double) location.getBearing(), location.hasSpeed(), (double) location.getSpeed());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }

        private void ensureLocationManagerCreated() {
            if (this.mLocationManager == null) {
                this.mLocationManager = (LocationManager) this.mContext.getSystemService("location");
                if (this.mLocationManager == null) {
                    Log.m32e(TAG, "Could not get location manager.", new Object[0]);
                }
            }
        }

        private void registerForLocationUpdates(boolean isGpsEnabled) {
            ensureLocationManagerCreated();
            if (!usePassiveOneShotLocation()) {
                if ($assertionsDisabled || !this.mIsRunning) {
                    this.mIsRunning = true;
                    try {
                        Criteria criteria = new Criteria();
                        if (isGpsEnabled) {
                            criteria.setAccuracy(1);
                        }
                        this.mLocationManager.requestLocationUpdates(0, 0.0f, criteria, this, ThreadUtils.getUiThreadLooper());
                        return;
                    } catch (SecurityException e) {
                        Log.m32e(TAG, "Caught security exception while registering for location updates from the system. The application does not have sufficient geolocation permissions.", new Object[0]);
                        unregisterFromLocationUpdates();
                        LocationProviderAdapter.newErrorAvailable("application does not have sufficient geolocation permissions.");
                        return;
                    } catch (IllegalArgumentException e2) {
                        Log.m32e(TAG, "Caught IllegalArgumentException registering for location updates.", new Object[0]);
                        unregisterFromLocationUpdates();
                        if (!$assertionsDisabled) {
                            throw new AssertionError();
                        }
                        return;
                    }
                }
                throw new AssertionError();
            }
        }

        private void unregisterFromLocationUpdates() {
            if (this.mIsRunning) {
                this.mIsRunning = $assertionsDisabled;
                this.mLocationManager.removeUpdates(this);
            }
        }

        private boolean usePassiveOneShotLocation() {
            if (!isOnlyPassiveLocationProviderEnabled()) {
                return $assertionsDisabled;
            }
            Location location = this.mLocationManager.getLastKnownLocation("passive");
            if (location != null) {
                ThreadUtils.runOnUiThread(new C03451(location));
            }
            return true;
        }

        private boolean isOnlyPassiveLocationProviderEnabled() {
            List<String> providers = this.mLocationManager.getProviders(true);
            return (providers != null && providers.size() == 1 && ((String) providers.get(0)).equals("passive")) ? true : $assertionsDisabled;
        }
    }

    static {
        $assertionsDisabled = !LocationProviderFactory.class.desiredAssertionStatus();
    }

    private LocationProviderFactory() {
    }

    @VisibleForTesting
    public static void setLocationProviderImpl(LocationProvider provider) {
        if ($assertionsDisabled || sProviderImpl == null) {
            sProviderImpl = provider;
            return;
        }
        throw new AssertionError();
    }

    public static LocationProvider get(Context context) {
        if (sProviderImpl == null) {
            sProviderImpl = new LocationProviderImpl(context);
        }
        return sProviderImpl;
    }
}
