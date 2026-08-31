package com.example.app_marifin_javadroid.core.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

/**
 * Debounce Utility for throttling UI search inputs, filtering, and AI queries (300-500ms).
 */
public class DebounceHelper {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private final long delayMillis;

    public DebounceHelper(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public DebounceHelper() {
        this(350); // Default 350ms
    }

    public void debounce(@NonNull Runnable action) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnable = action;
        handler.postDelayed(runnable, delayMillis);
    }

    public void cancel() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }
}
