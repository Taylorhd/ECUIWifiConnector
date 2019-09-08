package cora.com.ecutwificonnector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private String loginStatus = "";
    private static final String TAG = "LoginActivity";
    private final int TOAST_DATA = 1;

    //创建 账号控件  、密码控件 、checkbox控件 登录控件
    private EditText editTextAccount ;
    private EditText editTextPassword;
    private CheckBox checkBoxRemember;
    private Button buttonLogin;

    private List<String> list = new ArrayList<>();
    private Spinner  spinnerText;
    private ArrayAdapter<String> adapter;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String isp = "南昌移动";
    private int location;
    private String account;
    private String password;



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
            switch (msg.what){
                case TOAST_DATA:
                    Bundle bundle =msg.getData();
                    String data = bundle.getString("result",null);
                    Toast.makeText(LoginActivity.this,""+data, Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "handleMessage: result "+data);
                  loginStatus = data;
                    Log.d(TAG, "handleMessage: handle loginstatus "+loginStatus);
                    break;
                    default:break;
                }
            }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //请求定位权限
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            //如果大于Android O ，请求定位权限
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(LoginActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

        //定义下拉列表
        list.add("南昌移动");
        list.add("南昌电信");
        list.add("南昌联通");
        spinnerText = (Spinner)findViewById(R.id.spinnerISP);
        //定义适配器
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        //下拉样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //适配器添加到下拉拉列表
        spinnerText.setAdapter(adapter);
        //注意位置(0,1,2)
        spinnerText.setSelection(0,true);

        //添加监听器
        spinnerText.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isp = adapter.getItem(position);
                Log.d(TAG, "onItemSelected: isp"+isp);
                location = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //assign pref
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //引入视图
        editTextAccount  = (EditText) findViewById(R.id.et_account);
        editTextPassword = (EditText)findViewById(R.id.et_password);
        checkBoxRemember = (CheckBox)findViewById(R.id.cb_Remember);
        buttonLogin = (Button)findViewById(R.id.bt_login);

        //检查是否勾选记住密码，若记住密码状态，则读取数据
        //若未记住密码，不处理
        boolean isRemember = pref.getBoolean("remember_password",false);
//        Log.d(TAG, "onCreate: "+isRemember);
        if (isRemember){        //记住的情况，读取数据
            account = pref.getString("account","");
            password = pref.getString("password","");
            spinnerText.setSelection(pref.getInt("ISP",0));
            editTextAccount.setText(account);
            editTextPassword.setText(password);
            editTextPassword.setSelection(password.length());
            checkBoxRemember.setChecked(true);
        }

        //点击登录按钮，判断是否勾选记住密码，
        // 若记住密码，写入SharedPrefrence.
        //若未记住密码，则不处理

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            String address = "http://172.21.255.105:801/eportal/?c=Portal";
            String action = "login";
            String method = "1";
            @Override
            public void onClick(View v) {
                editor = pref.edit();
                if (checkBoxRemember.isChecked()){      //勾选了，写入
                    editor.putBoolean("remember_password",true);
                    editor.putString("account",editTextAccount.getText().toString());
                    editor.putString("password",editTextPassword.getText().toString());
                    editor.putInt("ISP",location);
                }else{
                    editor.clear();
                }
                    editor.apply();

                account = editTextAccount.getText().toString();
                password = editTextPassword.getText().toString();
                getWifiInfo();

                Log.d(TAG, "onClick: account "+account+"    isp:  "+isp);

                if (isp.equals("南昌移动")){
                    account = account+"%40cmcc";
                }else if (isp.equals("南昌电信")){
                    //电信
                    account = account+"%40telecom";
                }else if (isp.equals("南昌联通")){
                    //联通
                    account = account+"%40unicom";
                }

                Log.d(TAG, "account : "+account);

//                       HttpUtil.sendOkHttpRequest("http://172.21.255.105:801/eportal/?c=Portal","login","dr1567854710457","1","201720182028%40cmcc","Taotaoyuyu9926",handler);
                    try {
                        Log.d(TAG, "onClick: password"+password);
                        HttpUtil.sendOkHttpRequest(address,action,"result",method,account,password,handler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                Log.d(TAG, "onClick: login status:"+loginStatus);

                if ("result({\"result\":\"1\",\"msg\":\"认证成功\"})".equals(loginStatus)){
//                    if (loginStatus.equals("({\"result\":\"1\",\"msg\":\"认证成功\"})")){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this,"请检查用户名、密码以及运营商",Toast.LENGTH_LONG).show();
                    }
                }
        });
        //判读账号是否正确，其他业务逻辑
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "你拒绝了定位权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void  getWifiInfo(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo= wm.getConnectionInfo();
//        Log.d(TAG, "getWifiInfo: "+wifiInfo);

        String wifiSSID = wifiInfo.getSSID();
        Log.d(TAG, "getWifiInfo: ssid"+wifiSSID);
        String ECUT = "\"ECUT_S\"";
        String ECUT_5G = "\"ECUT_S_5G\"";


        if (ECUT.equals(wifiSSID)||ECUT_5G.equals(wifiSSID)){
               //已连接

        }else {
            Toast.makeText(LoginActivity.this,"未连接至ECUT",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
             startActivity(intent);
        }

    }


}
