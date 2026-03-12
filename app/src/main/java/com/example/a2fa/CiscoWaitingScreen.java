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

import java.util.Random;
import java.util.Stack;

public class CiscoWaitingScreen extends Fragment {

    private Object binding;
    private final boolean USE_VERSION_2 = true; // Enabled to use the fun facts screen
    private final int SLEEP_TIME = 4; // 4, 8, 12
    private String message = "";

    // Fun fact logic variables
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable factRunnable;
    private final int[] factResources = {
            R.string.fun_fact_1, R.string.fun_fact_2, R.string.fun_fact_3,
            R.string.fun_fact_4, R.string.fun_fact_5, R.string.fun_fact_6
    };

    private int currentFactIndex = new Random().nextInt(factResources.length);
    private final Stack<Integer> factHistory = new Stack<>();
    private final Stack<Integer> forwardHistory = new Stack<>();

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

        if (USE_VERSION_2 && binding instanceof CiscoWaitingTesterBinding) {
            CiscoWaitingTesterBinding testerBinding = (CiscoWaitingTesterBinding) binding;

            factRunnable = new Runnable() {
                @Override
                public void run() {
                    showNextFact(testerBinding);
                    handler.postDelayed(this, 6000);
                }
            };

            // Show first fact immediately and start timer
            testerBinding.funFactTextview.setText(factResources[currentFactIndex]);
            handler.postDelayed(factRunnable, 6000);
        }
    }

    private void showNextFact(CiscoWaitingTesterBinding testerBinding) {
        // Save current index to history
        factHistory.push(currentFactIndex);
        if (factHistory.size() > 50) factHistory.remove(0); // Prevent stack from growing indefinitely

        if (!forwardHistory.isEmpty()) {
            // If we have "forward" history (from clicking back), use it
            currentFactIndex = forwardHistory.pop();
        } else {
            // Otherwise, pick a new random index
            int nextIndex;
            Random random = new Random();
            do {
                nextIndex = random.nextInt(factResources.length);
            } while (nextIndex == currentFactIndex && factResources.length > 1);
            currentFactIndex = nextIndex;
        }

        testerBinding.funFactTextview.setText(factResources[currentFactIndex]);
    }

    private void handleSuccess() {
        handler.post(() -> {
            if (!isAdded()) return;
            if (factRunnable != null) handler.removeCallbacks(factRunnable);

            String url = "https://ntnu.1024.no/2026/var/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (factRunnable != null) {
            handler.removeCallbacks(factRunnable);
        }
        binding = null;
    }

    public void triggerPush() {
        new Thread(() -> {
            try {
                Thread.sleep((this.SLEEP_TIME - 1) * 1000);
                String host = "api-cb21dc92.duosecurity.com";
                String uri = "/auth/v2/auth";
                Auth auth = new Auth("POST", host, uri, 30);

                auth.addParam("username", "user1");
                auth.addParam("factor", "push");
                auth.addParam("device", "auto");
                auth.signRequest("DI6BM6P06YBQXFU8EOT8", "vb13i6M78qrJo9Ebw9TitijLwHaMiSopLbgA347S");

                Object response = auth.executeRequest();
                this.message = "Success";
                handleSuccess();
                
            } catch (Exception e) {
                Log.e("DuoPush", "Error sending push", e);
            }
        }).start();
    }
}
