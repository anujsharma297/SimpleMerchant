package com.example.merchantapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

/**
 * This is the sample app which will make use of the PG SDK. This activity will
 * show the usage of Paytm PG SDK API's.
 **/

public class MerchantActivity extends Activity {

    private static final String MID = "BmCkqX60890197298690";
    private static final String INDUSTRY_TYPE_ID = "Retail";
    private static final String CHANNEL_ID = "WAP";
    private static final String WEBSITE = "APPSTAGING";
    private static final String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";


    private static String MOBILE_NO = "7777777777";
    private static String CUST_ID = "cust1";
    private static String ORDER_ID = "";
    private static String TXN_AMOUNT = "10";
    private static String CHECKSUMHASH = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchantapp);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //This is to refresh the order id: Only for the Sample App's purpose.
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void pay_now(View view) {

        generateChecksum();


    }

    private void generateChecksum() {

        Random r = new Random(System.currentTimeMillis());
        ORDER_ID = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);

        Log.e("Order ID : ", ORDER_ID);


        String url = "https://narrowed-malfunctio.000webhostapp.com/paytm/checksum.php";

        HashMap<String, String> params = new HashMap<>();

        params.put("MID", MID);
        params.put("ORDER_ID", ORDER_ID);
        params.put("CUST_ID", CUST_ID);
        params.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
        params.put("CHANNEL_ID", CHANNEL_ID);
        params.put("TXN_AMOUNT", TXN_AMOUNT);
        params.put("WEBSITE", WEBSITE);
        params.put("CALLBACK_URL", CALLBACK_URL + ORDER_ID);
        params.put("MOBILE_NO", MOBILE_NO);

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                CHECKSUMHASH = response.optString("CHECKSUMHASH");

                Log.e("Response received : ", String.valueOf(response));

                if (CHECKSUMHASH.trim().length() != 0) {
                    onStartTransaction();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error", error.toString());
                error.printStackTrace();

            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getStagingService();
        HashMap<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters

        paramMap.put("CALLBACK_URL", CALLBACK_URL + ORDER_ID);
        paramMap.put("CHANNEL_ID", CHANNEL_ID);
        paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
        paramMap.put("CUST_ID", CUST_ID);
        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
        paramMap.put("MID", MID);
        paramMap.put("MOBILE_NO", MOBILE_NO);
        paramMap.put("ORDER_ID", ORDER_ID);
        paramMap.put("TXN_AMOUNT", TXN_AMOUNT);
        paramMap.put("WEBSITE", WEBSITE);

        PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.

                        Log.e("LOG", "Some UI error occured" + inErrorMessage);
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.e("LOG", "Payment Transaction Response " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Log.e("LOG", "Network Not Available ");
                        Toast.makeText(getApplicationContext(), "Network Not Available ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.

                        Log.e("LOG", "Network Not Available " + inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                        Log.e("LOG", "Network Not Available " + inErrorMessage + " @ " + inFailingUrl);

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(MerchantActivity.this, "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.e("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }
}

