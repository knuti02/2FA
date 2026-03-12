package com.example.a2fa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duosecurity.client.Auth;
import com.example.a2fa.databinding.CiscoWaitingStaticBinding;
import com.example.a2fa.databinding.CiscoWaitingTesterBinding;

import java.util.Objects;
import java.util.Random;

public class CiscoWaitingScreen extends Fragment {

    private Object binding; // Changed to Object to handle both binding types
    private final boolean USE_VERSION_2 = true;
    private String message = "";

    // Fun fact logic variables
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable factRunnable;
    private final int[] factResources = {
            R.string.fun_fact_1, R.string.fun_fact_2, R.string.fun_fact_3,
            R.string.fun_fact_4, R.string.fun_fact_5, R.string.fun_fact_6
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        triggerPush();

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
            /*
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

            */
            // scrollview
            final int[] currentIndex = {0};


// Show first fact immediately
            testerBinding.funFactTextview.setText(factResources[0]);

            testerBinding.backButton.setOnClickListener(v -> {
                if (currentIndex[0] > 0) {
                    currentIndex[0]--;
                } else {
                    currentIndex[0] = factResources.length - 1;
                }
                testerBinding.funFactTextview.setText(factResources[currentIndex[0]]);
            });

            testerBinding.nextButton.setOnClickListener(v -> {
                if (currentIndex[0] < factResources.length - 1) {
                    currentIndex[0]++;
                } else {
                    currentIndex[0] = 0;
                }
                testerBinding.funFactTextview.setText(factResources[currentIndex[0]]);
            });
        }
    }

    private void handleSuccess() {
        handler.post(() -> {
            if (!isAdded()) return;

            System.out.println("Success!!!:D");

            // 1. Stop your fun fact timer
            if (factRunnable != null) handler.removeCallbacks(factRunnable);

            // 2. Open the URL
            String url = "https://ntnu.1024.no/2026/var/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

            // 3. Optional: Close the app/activity so they don't go back to the "Waiting" screen
            //requireActivity().finish();

        });
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

    public void triggerPush() {
        new Thread(() -> {
            try {
                // 1️⃣ Create Auth instance
                String host = "api-cb21dc92.duosecurity.com";
                String uri = "/auth/v2/auth";
                Auth auth = new Auth("POST", host, uri, 30);

                // 2️⃣ Set Duo parameters
                auth.addParam("username", "user2");
                auth.addParam("factor", "auto");
                auth.addParam("device", "auto");

                // 3️⃣ Sign the request with your keys
                auth.signRequest("DI6BM6P06YBQXFU8EOT8", "vb13i6M78qrJo9Ebw9TitijLwHaMiSopLbgA347S");

                // 4️⃣ Execute request
                Object response = auth.executeRequest(); // returns response object from Duo

                System.out.println("Duo push response: " + response);
                
                // For now, assuming any non-exception response is success
                // You might want to parse 'response' to be sure
                this.message = "Success";
                handleSuccess();
                
            } catch (Exception e) {
                Log.e("DuoPush", "Error sending push", e);
            }
        }).start();
    }
}
