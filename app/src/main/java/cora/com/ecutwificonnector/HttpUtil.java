package cora.com.ecutwificonnector;

import android.util.Log;
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

                String url = address + "&a=" + action + "&callback=" + result + "&login_method=" + method + "&user_account=" + account + "&user_password=" + password;
//                Log.d(TAG, "run: address"+url);
                Request request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    data = response.body().string();
                    Log.d(TAG, "run: data:"+data);
                    if (listener != null){
                        listener.onFinish(data);
                    }
                    Log.d(TAG, "run: +data" + data);
                } catch (IOException e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }

}







