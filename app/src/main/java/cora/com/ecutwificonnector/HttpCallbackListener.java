package cora.com.ecutwificonnector;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}