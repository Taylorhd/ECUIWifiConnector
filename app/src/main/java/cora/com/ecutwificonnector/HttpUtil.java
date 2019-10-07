package cora.com.ecutwificonnector;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class HttpUtil {
    private static final String TAG = "HttpUtil";
//检查状态
    public static void checlStatus(final String url , final HttpCallbackListener listener ){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String data = "";
//                Log.d(TAG, "run: address"+url);
                Request request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    data = response.body().string();
//                    Log.d(TAG, "run: data:"+data);
                    if (listener != null){
                        listener.onFinish(data);
                    }
//                    Log.d(TAG, "run: +data" + data);
                } catch (IOException e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                }

            }
        }).start();
    }
    public static String parseHTMLWithRegExp(String data){
        String title = "";
        Pattern pa = Pattern.compile("<title>.*?</title>");
        Matcher ma = pa.matcher(data);
        ma.find();
        title = ma.group();
        return title;
    }

//    public static void sendOkHttpRequest(final String address, final String action, final String result, final String method, final String account, final String password,final HttpCallbackListener listener) throws IOException {
    public static void sendOkHttpRequest(final String url,final HttpCallbackListener listener) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String data = "";
               // String url = address + "&a=" + action + "&callback=" + result + "&login_method=" + method + "&user_account=" + account + "&user_password=" + password;
//                Log.d(TAG, "run: address"+url);
                Request request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    data = response.body().string();
//                    Log.d(TAG, "run: data:"+data);
                    if (listener != null){
                        listener.onFinish(data);
                    }
//                    Log.d(TAG, "run: +data" + data);
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







