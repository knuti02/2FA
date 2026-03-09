package com.example.a2fa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.a2fa.databinding.FragmentFirstBinding;



import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;


import com.duosecurity.client.Auth;
import com.duosecurity.client.Http;
import com.duosecurity.client.Util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;





public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        binding.buttonFirst.setOnClickListener(v -> {
                binding.buttonFirst.setText("Sending Push...");
                triggerPush();
                //sendDuoPush("user1", "auto");
                //PostmanTriggerHelper postmanTriggerHelper = new PostmanTriggerHelper();

                // Call the function to trigger Duo Push
                //postmanTriggerHelper.triggerDuoPush("user1", "auto");

                // https://api-b13ed975.duosecurity.com/auth/v2/auth

                //NavHostFragment.findNavController(FirstFragment.this)
                        //.navigate(R.id.action_FirstFragment_to_SecondFragment)
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                auth.addParam("username", "user1");
                auth.addParam("factor", "push");
                auth.addParam("device", "auto");

                // 3️⃣ Sign the request with your keys
                auth.signRequest("DI6BM6P06YBQXFU8EOT8", "vb13i6M78qrJo9Ebw9TitijLwHaMiSopLbgA347S");

                // 4️⃣ Execute request
                Object response = auth.executeRequest(); // returns response object from Duo

                System.out.println("Duo push response: " + response);
            } catch (Exception e) {
                Log.e("DuoPush", "Error sending push", e);
            }
        }).start();
    }

//no  API key
//no  ID



    /*curl -X POST https://api.getpostman.com/collection-runs \
            -H "X-Api-Key: <YOUR_API_KEY>" \
            -H "Content-Type: application/json" \
            -d '{"collection":{"uid":"<COLLECTION_UID>"}}'*/


    /*public class PostmanTriggerHelper {

        // Replace with your Postman API key and Collection UID
        private final String postmanApiKey = "no";
        private final String collectionUid = "no";

        // Constructor to initialize API key and Collection UID


        public PostmanTriggerHelper() {
            // Constructor
        }


        public void triggerDuoPush(String username, String device) {
            new Thread(() -> {
                try {
                    OkHttpClient client = new OkHttpClient();

                    // Prepare the JSON body with username and device
                    String jsonBody = "{ \"data\": { " +
                            "\"username\": \"" + username + "\"," +
                            "\"device\": \"" + device + "\"" +
                            "} }";

                    // Build the request to trigger Postman API
                    Request request = new Request.Builder()
                            .url("https://api.getpostman.com/collections/" + collectionUid + "/run")
                            .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                            .addHeader("X-Api-Key", postmanApiKey)
                            .addHeader("Content-Type", "application/json")
                            .build();

                    // Execute the request
                    Response response = client.newCall(request).execute();

                    // Check if the response is successful
                    if (response.isSuccessful()) {
                        String body = response.body() != null ? response.body().string() : "";
                        Log.d("PostmanAPI", "HTTP code: " + response.code());
                        Log.d("PostmanAPI", "Response: " + body);
                    } else {
                        // Log unsuccessful response details (headers, body)
                        String errorBody = response.body() != null ? response.body().string() : "No response body";
                        Log.e("PostmanAPI", "Request failed with HTTP code: " + response.code());
                        Log.e("PostmanAPI", "Error Response Body: " + errorBody);

                        // Log response headers
                        Log.e("PostmanAPI", "Response Headers: " + response.headers().toString());
                    }

                } catch (Exception e) {
                    // Catch network errors or any other exceptions
                    Log.e("PostmanAPI", "Error triggering collection", e);

                    // Log network exception details (stack trace)
                    String stackTrace = Log.getStackTraceString(e);
                    Log.e("PostmanAPI", "Stack trace: " + stackTrace);

                    // Specific network error types (timeouts, unknown hosts, etc.)
                    if (e instanceof java.net.SocketTimeoutException) {
                        Log.e("PostmanAPI", "Network Timeout Error: " + e.getMessage());
                    } else if (e instanceof java.net.UnknownHostException) {
                        Log.e("PostmanAPI", "Unknown Host Error: " + e.getMessage());
                    }
                }
            }).start();
        }
    } */



    private void sendDuoPush(String username, String device) {
        new Thread(() -> {
            try {
                String factor = "push";
                String path = "/auth/v2/auth";
                String ikey = "DI9VXDIQJZC34ZOY8R6S";
                String skey = "y30rWjz9yiXh3TEio0SOmE4vTBToOSLACFmzx2DS";
                String host = "api-b13ed975.duosecurity.com";

                // 1️⃣ Build parameters
                Map<String, String> params = new LinkedHashMap<>();
                params.put("username", username);
                params.put("factor", "push"); // Duo push factor
                params.put("device", device);


                // 2️⃣ Sort and URL-encode parameters
                List<String> sortedParams = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sortedParams.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                Collections.sort(sortedParams);
                String encodedParams = String.join("&", sortedParams);

                // 3️⃣ Date header in RFC 2822 format
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                String date = dateFormat.format(new Date());

                // 4️⃣ Canonical string
                String canonical = date + "\n" + "POST"+ "\n" + host + "\n/auth/v2/auth\n" + encodedParams;

                // 5️⃣ HMAC-SHA1 signature - Generate the HMAC-SHA1 hash.
                Mac mac = Mac.getInstance("HmacSHA1");
                SecretKeySpec secret = new SecretKeySpec(skey.getBytes("UTF-8"), "HmacSHA1");
                mac.init(secret);
                byte[] hmac = mac.doFinal(canonical.getBytes("UTF-8"));

                // 6️⃣ Base64 encode raw HMAC bytes (no hex!)
                String sigB64 = Base64.encodeToString(hmac, Base64.NO_WRAP);  // Base64 encode the HMAC

                // 7️⃣ Authorization header - Use Basic authentication with the encoded HMAC and ikey
                String prebase = ikey + ":" + sigB64;
                String authHeader = "Basic " + Base64.encodeToString(prebase.getBytes("UTF-8"), Base64.NO_WRAP);

                // 8️⃣ Build POST body (using multipart/form-data as per Postman)
                FormBody body = new FormBody.Builder()
                        .add("username", username)
                        .add("factor", factor) // must match canonical
                        .add("device", device)
                        .build();

                // 9️⃣ Build the request with OkHttpClient
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://" + host + path)
                        .post(body)
                        .addHeader("Authorization", authHeader)
                        .addHeader("Date", date)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                // 1️⃣0️⃣ Log full request - To log and debug request URL, headers, and body.
                System.out.println("=== REQUEST ===");
                System.out.println("URL: " + request.url());  // Logs URL
                System.out.println("Method: " + request.method());  // Logs HTTP method (POST)
                System.out.println("Headers: " + request.headers());  // Logs all headers
                System.out.println("Body: " + encodedParams);  // Logs encoded parameters (form-data)
                Log.d("DuoPush", "AUTH HEADER: " + authHeader);
                Log.d("DuoPush", "Date header: " + date);
                Log.d("DuoPush", "Canonical: " + canonical);
                Log.d("DuoPush", "Request body: device=auto&factor=auto&username=" + username);


                // 1️⃣1️⃣ Execute the request and handle the response
                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d("DuoPush", "Duo Push Response Code: " + responseBody);
                Log.d("DuoPush", "HTTP code: " + response.code());

            } catch (Exception e) {
            Log.e("DuoPush", "Error sending push", e);
            }
        }).start();
    }
}