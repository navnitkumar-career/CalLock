package com.example.gallerylock.panicswitch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

/* loaded from: classes2.dex */
public class AccelerometerManager {
    private static Context aContext = null;
    private static int interval = 200;
    private static AccelerometerListener listener = null;
    private static boolean running = false;
    private static Sensor sensor = null;
    private static SensorEventListener sensorEventListener = new SensorEventListener() { // from class: net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerManager.1
        private long now = 0;
        private long timeDiff = 0;
        private long lastUpdate = 0;
        private long lastShake = 0;
        private float x = 0.0f;
        private float y = 0.0f;
        private float z = 0.0f;
        private float lastX = 0.0f;
        private float lastY = 0.0f;
        private float lastZ = 0.0f;
        private float force = 0.0f;

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor2, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            this.now = sensorEvent.timestamp;
            this.x = sensorEvent.values[0];
            this.y = sensorEvent.values[1];
            float f = sensorEvent.values[2];
            this.z = f;
            long j = this.lastUpdate;
            if (j == 0) {
                long j2 = this.now;
                this.lastUpdate = j2;
                this.lastShake = j2;
                this.lastX = this.x;
                this.lastY = this.y;
                this.lastZ = f;
            } else {
                long j3 = this.now - j;
                this.timeDiff = j3;
                if (j3 > 0) {
                    float abs = Math.abs(((((this.x + this.y) + f) - this.lastX) - this.lastY) - this.lastZ);
                    this.force = abs;
                    if (Float.compare(abs, AccelerometerManager.threshold) > 0) {
                        if (this.now - this.lastShake >= AccelerometerManager.interval) {
                            AccelerometerManager.listener.onShake(this.force);
                        }
                        this.lastShake = this.now;
                    }
                    this.lastX = this.x;
                    this.lastY = this.y;
                    this.lastZ = this.z;
                    this.lastUpdate = this.now;
                }
            }
            AccelerometerManager.listener.onAccelerationChanged(this.x, this.y, this.z);
        }
    };
    private static SensorManager sensorManager = null;
    private static Boolean supported = null;
    private static float threshold = 15.0f;

    public static boolean isListening() {
        return running;
    }

    public static void stopListening() {
        running = false;
        try {
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        } catch (Exception unused) {
        }
    }

    public static boolean isSupported(Context context) {
        aContext = context;
        if (supported == null) {
            if (context != null) {
                SensorManager sensorManager2 = (SensorManager) context.getSystemService("sensor");
                sensorManager = sensorManager2;
                boolean z = true;
                if (sensorManager2.getSensorList(1).size() <= 0) {
                    z = false;
                }
                supported = new Boolean(z);
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported.booleanValue();
    }

    public static void configure(int i, int i2) {
        threshold = i;
        interval = i2;
    }

    public static void startListening(AccelerometerListener accelerometerListener) {
        SensorManager sensorManager2 = (SensorManager) aContext.getSystemService("sensor");
        sensorManager = sensorManager2;
        List<Sensor> sensorList = sensorManager2.getSensorList(1);
        if (sensorList.size() > 0) {
            Sensor sensor2 = sensorList.get(0);
            sensor = sensor2;
            running = sensorManager.registerListener(sensorEventListener, sensor2, 1);
            listener = accelerometerListener;
        }
    }

    public static void startListening(AccelerometerListener accelerometerListener, int i, int i2) {
        configure(i, i2);
        startListening(accelerometerListener);
    }
}
