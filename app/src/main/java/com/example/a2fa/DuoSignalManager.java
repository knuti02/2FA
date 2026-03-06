package com.example.a2fa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * This is the file your teammate will use to signal the UI.
 */
public class DuoSignalManager {
    private static DuoSignalManager instance;
    private final MutableLiveData<Boolean> authSignal = new MutableLiveData<>(false);

    private DuoSignalManager() {}

    public static synchronized DuoSignalManager getInstance() {
        if (instance == null) {
            instance = new DuoSignalManager();
        }
        return instance;
    }

    public LiveData<Boolean> getAuthSignal() {
        return authSignal;
    }

    // Your teammate calls this when Duo says "Approved"
    public void setPushAccepted(boolean isAccepted) {
        authSignal.postValue(isAccepted);
    }
}