package com.lexxdigitals.easyfoodvone.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/*
* Created by  SHAKTI
* WE CREATE EVERYTHING ,WE ARE GOD.
** Last Update - 06/02/18
*/
public class ConnectToWeb {
    // public static final String BASE_URL = "http://........./";
    Context context;
    boolean status;
    int statusCode = 0;
    String responseString;
    File file = null;
    //Config Settings
    int timeOut = 30000;
    int timeOutMultipart = 60000;
    boolean errorLog = true;
    boolean requestLog = true;
    boolean responseLog = true;
    String TAG_RESPONSE = "api";
    String INTERNET_CONNECTION_ERROR = "Please check internet connection";
    String API_ERROR = "Failed to connect. Please try again later.";

    public ConnectToWeb(Context context) {
        this.context = context;
    }

    public ConnectToWeb setTimeout(int timeout) {
        this.timeOut = timeout;
        return this;
    }

    public static boolean isOnline(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    //===========================================GET REQUEST ========================================//
    public ConnectToWeb getRequest(final String tag, final String getUrl, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    try {
                        URL url = new URL(getUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("GET");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setDoInput(true);
                        con.connect();
                        statusCode = con.getResponseCode();
       Log.d(TAG_RESPONSE, "===========" + tag + "===========");
                        if (statusCode == 200) {
                            is = con.getInputStream();
                            responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + responseString);
                        } else {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Unable to connect to server." + statusCode);
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString);
                                else
                                    errorListener.onError(tag, API_ERROR);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
        return this;
    }

    //========================================== Post JSON REQUEST ================================================//
    public ConnectToWeb postJsonRequest(final String tag, final String postUrl, final String postParams, final ResponseListener responseListener, final ErrorListener errorListener) {
        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    String JsonResponse = null;
                    String JsonDATA = postParams;
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(postUrl);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setRequestProperty("Content-Type", "application/json");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                        writer.write(JsonDATA);
                        writer.close();
                        statusCode = urlConnection.getResponseCode();
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            // Nothing to do.
                            Log.d("INPUT_STRM", "ResponseModel :- " + "INPUT STREAM  NULL.");
                            //   return null;
                        }else
                            Log.d("INPUT_STRM", "ResponseModel :- " + "INPUT STREAM NOT NULL.");

                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String inputLine;
                        while ((inputLine = reader.readLine()) != null)
                            buffer.append(inputLine + "\n");
                        if (buffer.length() == 0) {
                            Log.e("","");
                            // Stream was empty. No point in parsing.
                            //    return null;
                        }
                        statusCode = urlConnection.getResponseCode();
                        if (statusCode == 200) {
                            responseString = buffer.toString();
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + responseString);
                        } else {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Unable to connect to server." + statusCode);
                        }


                    } catch (IOException excep) {
                        excep.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException excep) {
                                Log.e( "Error closing stream", ""+excep);
                            }
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString);
                                else
                                    errorListener.onError(tag, API_ERROR);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
        return this;
    }

    //========================================== Post JSON REQUEST With Header================================================//
    public ConnectToWeb postJsonRequestWithHeader(final String tag, final String postUrl, final String postParams, final ResponseListener responseListener, final ErrorListener errorListener) {
        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    String JsonResponse = null;
                    String JsonDATA = postParams;
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(postUrl);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        ////
                      //  String userCredentials = "rajmangal:a$Sv5F";
                      //  String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
                        urlConnection.setRequestProperty ("authorization", tag);
                        ////
                        urlConnection.setUseCaches(false);
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setRequestProperty("Content-Type", "application/json");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                        writer.write(JsonDATA);
                        writer.close();
                        statusCode = urlConnection.getResponseCode();
                        Log.e( "status Code", ""+statusCode);
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            // Nothing to do.
                            Log.d("INPUT_STRM", "ResponseModel :- " + "INPUT STREAM  NULL.");
                            //   return null;
                        }else
                            Log.d("INPUT_STRM", "ResponseModel :- " + "INPUT STREAM NOT NULL.");

                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String inputLine;
                        while ((inputLine = reader.readLine()) != null)
                            buffer.append(inputLine + "\n");
                        if (buffer.length() == 0) {
                            Log.e("","");
                            // Stream was empty. No point in parsing.
                            //    return null;
                        }
                        statusCode = urlConnection.getResponseCode();
                        if (statusCode == 200) {
                            responseString = buffer.toString();
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + responseString);
                        } else {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Unable to connect to server." + statusCode);
                        }


                    } catch (IOException excep) {
                        excep.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException excep) {
                                Log.e( "Error closing stream", ""+excep);
                            }
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString);
                                else
                                    errorListener.onError(tag, API_ERROR);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
        return this;
    }

    //===========================================GET GENERIC=========================================//
    public ConnectToWeb getRequestGeneric(final String tag, final String getUrl, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    try {
                        URL url = new URL(getUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("GET");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setDoInput(true);
                        con.connect();
                        statusCode = con.getResponseCode();

                        Log.d(TAG_RESPONSE, "===========" + tag + "===========");

                        if (statusCode == 200) {
                            is = con.getInputStream();
                            responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + responseString);
                        } else {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Unable to connect to server." + statusCode);
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString);
                                else
                                    errorListener.onError(tag, API_ERROR);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
        return this;
    }

    //===========================================POST REQUEST=========================================//
    public ConnectToWeb postRequest(final String tag, final String postUrl, final HashMap<String, String> postParams, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    try {
                        URL url = new URL(postUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setDoOutput(true);
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(getPostDataString(postParams).getBytes());
                        os.flush();
                        os.close();

                        if (requestLog && postParams != null)
                            Log.d(TAG_RESPONSE, "===========" + tag + "===========");
                        //Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                        statusCode = con.getResponseCode();
                        if (statusCode == 200) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + responseString);
                        } else {
                            status = false;
                            is = con.getErrorStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Unable to connect to server." + statusCode);
                            if (responseLog && responseString != null) {
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + responseString);
                                responseString = "Failed to connect. Please try again later.";
                            }
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString);
                                else
                                    errorListener.onError(tag, API_ERROR);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
        return this;
    }

    //===========================================Multipart REQUEST FOR SINGLE FILE=========================================//
    public void MultipartRequest(final String tag, final String url, final HashMap<String, String> postParams, final File file, final String fileKey, final ResponseListener responseListener, final ErrorListener errorListener) {
        if (isOnline(context)) {
            if (file != null) {
                HandlerThread handlerThread = new HandlerThread("HandlerThread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {

                        try {
                            String charset = "UTF-8";
                            String requestURL = url;

                            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                            for (HashMap.Entry<String, String> entry : postParams.entrySet())
                                multipart.addFormField(entry.getKey().toString(), entry.getValue().toString());

                            multipart.addFilePart(fileKey, file);
                            responseString = multipart.execute();

                        } catch (Exception ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                        }
                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (status) {
                                    if (responseString != null && responseString.length() > 0)
                                        responseListener.onResponse(tag, responseString);
                                } else {
                                    if (responseString != null && responseString.length() > 0)
                                        errorListener.onError(tag, responseString);
                                    else
                                        errorListener.onError(tag, API_ERROR);
                                }
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                };

                handler.sendEmptyMessage(0);
            } else {
                postRequest(tag, url, postParams, responseListener, errorListener);
            }
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
    }

    //===========================================Multipart REQUEST FOR MULTIPLE FILES=========================================//

    public void MultipartRequestFiles(final String tag, final String url, final HashMap<String, String> postParams, final HashMap<String, File> files, final ResponseListener responseListener, final ErrorListener errorListener) {
        if (isOnline(context)) {
            if (files != null && files.size() > 0) {
                HandlerThread handlerThread = new HandlerThread("HandlerThread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {

                        try {
                            String charset = "UTF-8";
                            String requestURL = url;

                            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                            for (HashMap.Entry<String, String> entry : postParams.entrySet())
                                multipart.addFormField(entry.getKey().toString(), entry.getValue().toString());

                            for (String key : files.keySet())
                                multipart.addFilePart(key, files.get(key));

                            responseString = multipart.execute();


                        } catch (Exception ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: " + ex.getLocalizedMessage());
                        }
                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (status) {
                                    if (responseString != null && responseString.length() > 0)
                                        responseListener.onResponse(tag, responseString);
                                } else {
                                    if (responseString != null && responseString.length() > 0)
                                        errorListener.onError(tag, responseString);
                                    else
                                        errorListener.onError(tag, API_ERROR);
                                }
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                };

                handler.sendEmptyMessage(0);
            } else {
                postRequest(tag, url, postParams, responseListener, errorListener);
            }
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
    }

    //===========================================DOWNLOAD REQUEST=========================================//
    public ConnectToWeb downloadFile(final String tag, final String postUrl, final HashMap<String, String> postParams, final String fileName, final FileDownloadListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            final int BUFFER_SIZE = 4096;
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    InputStream is = null;
                    try {
                        URL url = new URL(postUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        con.setDoOutput(true);
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(getPostDataString(postParams).getBytes());
                        os.flush();
                        os.close();

                        if (requestLog && postParams != null)
                            Log.d(TAG_RESPONSE, "===========" + tag + "===========");
                        //  Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                        statusCode = con.getResponseCode();
                        if (statusCode == 200 /*|| statusCode ==400*/) {
                            is = con.getInputStream();
                            File createFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            file = new File(/*context.getFilesDir()*/createFolder, fileName);
                            FileOutputStream outputStream = new FileOutputStream(file);
                            int bytesRead = -1;
                            byte[] buffer = new byte[BUFFER_SIZE];
                            while ((bytesRead = is.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            outputStream.close();
                            is.close();
                            status = true;
                            return "success";
                        } else {
                            status = false;
                            Log.d(TAG_RESPONSE, "Error: Failed to connect to server: " + statusCode);
                            return "Failed to connect. Please try again";
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        Log.d(TAG_RESPONSE, "Error: Request timed out" + statusCode);
                        return "Connection timed out.Please try again";
                    } catch (Exception ex) {
                        status = false;
                        Log.d(TAG_RESPONSE, "Error: " + ex.getLocalizedMessage());
                        return "An error occurred. Please try again";
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException e) {
                            status = false;
                            Log.d(TAG_RESPONSE, "Error: " + e.getLocalizedMessage());
                            return "An error occurred. Please try again";
                        }
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (status) {
                        if (responseLog && result != null && result.equalsIgnoreCase("success"))
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + result);
                        responseListener.onResponse(tag, result, file);
                    } else {
                        if (errorLog && result != null)
                            Log.d(TAG_RESPONSE, "ResponseModel :- " + result);
                        errorListener.onError(tag, result);
                    }
                }
            }.execute();

        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR);
        }
        return this;
    }

    //=========================================ConnectionUtils=====================================================//
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder result = null;
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            result = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                result.append(line).append('\n');
            }
            return result.toString();
        } catch (Exception ex) {
            return "";
        }

    }


    public String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public interface ResponseListener {
        void onResponse(String tag, String response);
    }

    public interface FileDownloadListener {
        void onResponse(String tag, String response, File file);
    }

    public interface ErrorListener {
        void onError(String tag, String errorMsg);
    }

    public ConnectToWeb enableErrorLog() {
        errorLog = true;
        return this;
    }

    public ConnectToWeb enableResponseLog() {
        responseLog = true;
        return this;
    }

    class MultipartUtility {
        private final String boundary;
        private static final String LINE_FEED = "\r\n";
        private HttpURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;

        public MultipartUtility(String requestURL, String charset)
                throws IOException {
            this.charset = charset;
            boundary = "-------" + System.currentTimeMillis() + "-------";
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setReadTimeout(timeOutMultipart);
            httpConn.setConnectTimeout(timeOutMultipart);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
        }

        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
            // writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }

        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            if (uploadFile != null) {
                String fileName = uploadFile.getName();
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                FileInputStream inputStream = new FileInputStream(uploadFile);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();
                writer.append(LINE_FEED);
                writer.flush();
            }

        }


        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }


        public String execute() throws IOException {
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            statusCode = httpConn.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                httpConn.disconnect();
                String responseStr = stringBuilder.toString();
                status = true;
                return responseStr;
            } else {
                status = false;

                if (errorLog)
                    Log.d(TAG_RESPONSE, "ResponseModel :- " + "Error: Unable to connect to server." + statusCode/*+"\n"+responseStr*/);
                return null;
            }

        }
    }

}