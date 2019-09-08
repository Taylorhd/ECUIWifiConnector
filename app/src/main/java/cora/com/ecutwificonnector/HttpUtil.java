package cora.com.ecutwificonnector;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class HttpUtil {
    private static final String TAG = "HttpUtil";


    public static void sendOkHttpRequest(final String address, final String action, final String result, final String method, final String account, final String password,final HttpCallbackListener listener) throws IOException {
         final int TOAST_DATA = 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String data = "";

//        String url ="http://172.21.255.105:801/eportal/?c=Portal&a=login&callback=dr1567854710457&login_method=1&user_account=201720182028%40cmcc&user_password=Taotaoyuyu9926";
//                String url = "https://api.github.com/markdown/raw";
                String url = address + "&a=" + action + "&callback=" + result + "&login_method=" + method + "&user_account=" + account + "&user_password=" + password;
                Log.d(TAG, "run: address"+url);
                Request request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    data = response.body().string();

                    if (listener != null){
                        listener.onFinish(data);
                    }
//                    hello = data;
                    Log.d(TAG, "run: +data" + data);
                } catch (IOException e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                }
//                Message message = new Message();
//                Bundle bundle = new Bundle();
//                bundle.putString("result",data);
//                message.setData(bundle);
//                message.what = TOAST_DATA;
//                handler.sendMessage(message);

            }
        }).start();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient client = new OkHttpClient();
//                Log.d(TAG, "sendOkHttpRequest: ");
////        String url ="http://172.21.255.105:801/eportal/?c=Portal&a=login&callback=dr1567854710457&login_method=1&user_account=201720182028%40cmcc&user_password=Taotaoyuyu9926";
////                String url = "https://api.github.com/markdown/raw";
//                String url = address+"&a="+action+"&=callback="+result+"&login_method="+method+"&user_account="+account+"&user_password="+password;
//                Request request = new Request.Builder()
//                        .get()
//                        .url(url)
//                        .build();
//                Response response = null;
//                try {
//                    response = client.newCall(request).execute();
//                    String data = response.body().string();
//                    Log.d(TAG, "run: "+data);
////                    String msg = parseJSONWithJSONObject(data);
//
////                    Toast.makeText(MyApplication.getContext(),""+msg,Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
////        Log.d(TAG, "sendOkHttpRequest: "+data);
//
//
//            }
//        }).start();
    }

}







