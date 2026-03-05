package com.example.a2fa;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a2fa.databinding.CiscoWaitingStaticBinding;
import com.example.a2fa.databinding.CiscoWaitingTesterBinding;

import java.util.Random;

public class FirstFragment extends Fragment {

    private Object binding; // Changed to Object to handle both binding types
    private final boolean USE_VERSION_2 = true;

    // Fun fact logic variables
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable factRunnable;
    private final int[] factResources = {
            R.string.fun_fact_1, R.string.fun_fact_2, R.string.fun_fact_3,
            R.string.fun_fact_4, R.string.fun_fact_5, R.string.fun_fact_6
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (USE_VERSION_2) {
            binding = CiscoWaitingTesterBinding.inflate(inflater, container, false);
            return ((CiscoWaitingTesterBinding) binding).getRoot();
        } else {
            binding = CiscoWaitingStaticBinding.inflate(inflater, container, false);
            return ((CiscoWaitingStaticBinding) binding).getRoot();
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Only run the timer if we are using Version 2 (the one with the TextView)
        if (USE_VERSION_2 && binding instanceof CiscoWaitingTesterBinding) {
            CiscoWaitingTesterBinding testerBinding = (CiscoWaitingTesterBinding) binding;

            factRunnable = new Runnable() {
                @Override
                public void run() {
                    int randomFact = factResources[new Random().nextInt(factResources.length)];
                    testerBinding.funFactTextview.setText(randomFact);

                    // Repeat every 6 seconds
                    handler.postDelayed(this, 6000);
                }
            };

            // Start the loop immediately
            handler.post(factRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Crucial: Stop the timer to prevent memory leaks when the fragment is destroyed
        if (factRunnable != null) {
            handler.removeCallbacks(factRunnable);
        }
        binding = null;
    }
}