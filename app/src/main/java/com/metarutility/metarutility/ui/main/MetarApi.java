package com.metarutility.metarutility.ui.main;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


import org.json.JSONException;
import org.json.JSONObject;

import static com.metarutility.metarutility.utils.Constants.AVWX_API_KEY;

public class MetarApi extends AsyncTask<String, Void, String> {

    /*
     * AVWX Token is required for this application to work. Sign up for token at
     * https://account.avwx.rest/ and generate token. Add generated token as value
     * for variable 'authToken'
     */

    String authToken = AVWX_API_KEY;

    //Setting connection timeout to 15 seconds
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    public JSONObject GetStationInfo(String station) throws JSONException,
            ExecutionException, InterruptedException {
        /*Pulling Airport information from API and formatting into a JSON String
         */

        String stationInfo;
        //format: https://avwx.rest/api/metar/KLAX?format=json&token=[INSERT TOKEN HERE]

        String urlFormed = "https://avwx.rest/api/station/" + station
                + "?format=json&token=" + authToken;
        stationInfo = new MetarApi().execute(urlFormed).get();

        //Convert String from AsyncTask background task to a JSONObject
        JSONObject stationJson = null;
        if (stationInfo != null) {
            stationJson = new JSONObject(stationInfo);
        }

        return stationJson;
    }

    public JSONObject GetMetarInfo(String station) throws JSONException,
            ExecutionException, InterruptedException {
        /*GET METAR information from API and formatting into a JSON Object
         */
        String stationInfo = null;
        //format: https://avwx.rest/api/metar/KLAX?format=json&token=[INSERT TOKEN]
        String urlFormed = "https://avwx.rest/api/metar/" + station +
                "?format=json&token=" + authToken;
        stationInfo = new MetarApi().execute(urlFormed).get();

        //Convert String from AsyncTask background task to a JSONObject
        JSONObject stationJson = null;
        if (stationInfo != null) {
            stationJson = new JSONObject(stationInfo);
        }

        return stationJson;
    }


    @Override
    protected String doInBackground(String... params) {
        /*Running the API get call via network on a separate thread to prevent
            NetworkOnMainThreadException.
         */
        String stringURL = params[0];
        String stationInfo = null;
        try {
            URL getStationRequest = new URL(stringURL);
            HttpURLConnection connection = (HttpURLConnection) getStationRequest.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                while ((stationInfo = in.readLine()) != null) {
                    response.append(stationInfo);
                } in .close();

                stationInfo = response.toString();
            } else {
                System.out.println("Error Fetching Data");
                stationInfo = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stationInfo;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}