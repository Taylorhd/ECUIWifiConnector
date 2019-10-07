package cora.com.ecutwificonnector;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class LoginActivity extends AppCompatActivity {
    private String loginStatus = "";
//    private static final String TAG = "LoginActivity";
    //创建 账号控件  、密码控件 、checkbox控件 登录控件
    private EditText editTextAccount ;
    private EditText editTextPassword;
    private CheckBox checkBoxRemember;
    private List<String> list = new ArrayList<>();
    private Spinner  spinnerText;
    private ArrayAdapter<String> adapter;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String isp = "南昌移动";
    private int location;
    private String account;
    private String password;
    private FloatingActionButton fab;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ques_and_answer:
              Intent intent = new Intent(LoginActivity.this,QuesAnsActivity.class);
              startActivity(intent);
              break;
            case R.id.menu_quit:
                finish();
            default:
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        ckeckStatus();
        fab = (FloatingActionButton)findViewById(R.id.fab_login);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        //请求定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //如果大于Android O ，请求定位权限
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        //定义下拉列表
        list.add("南昌移动");
        list.add("南昌电信");
        list.add("南昌联通");
        spinnerText = (Spinner) findViewById(R.id.spinnerISP);
        //定义适配器
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        //下拉样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //适配器添加到下拉拉列表
        spinnerText.setAdapter(adapter);
        //注意位置(0,1,2)
        spinnerText.setSelection(0, true);

        //添加监听器
        spinnerText.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isp = adapter.getItem(position);
//                Log.d(TAG, "onItemSelected: isp" + isp);
                location = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //assign pref
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //引入视图
        editTextAccount = (EditText) findViewById(R.id.et_account);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        checkBoxRemember = (CheckBox) findViewById(R.id.cb_Remember);
//        buttonLogin = (Button) findViewById(R.id.bt_login);
        //检查是否勾选记住密码，若记住密码状态，则读取数据
        //若未记住密码，不处理
        boolean isRemember = pref.getBoolean("remember_password", false);
//        Log.d(TAG, "onCreate: "+isRemember);
        if (isRemember) {        //记住的情况，读取数据
            account = pref.getString("account", "");
            password = pref.getString("password", "");
            spinnerText.setSelection(pref.getInt("ISP", 0));
            editTextAccount.setText(account);
            editTextPassword.setText(password);
            editTextPassword.setSelection(password.length());
            checkBoxRemember.setChecked(true);
        }
    }
        //点击登录按钮，判断是否勾选记住密码，
        // 若记住密码，写入SharedPrefrence.
        //若未记住密码，则不处理
        //判读账号是否正确，其他业务逻辑
    private void login() {
        String address = "http://172.21.255.105:801/eportal/?c=Portal";
        String action = "login";
        String method = "1";
        if (getWifiInfo()){
            editor = pref.edit();
            if (checkBoxRemember.isChecked()) {      //勾选了，写入
                editor.putBoolean("remember_password", true);
                editor.putString("account", editTextAccount.getText().toString());
                editor.putString("password", editTextPassword.getText().toString());
                editor.putInt("ISP", location);
            } else {
                editor.clear();
            }
            editor.apply();
            account = editTextAccount.getText().toString();
            password = editTextPassword.getText().toString();

            if (isp.equals("南昌移动")) {
                account = account + "%40cmcc";
            } else if (isp.equals("南昌电信")) {
                //电信
                account = account + "%40telecom";
            } else if (isp.equals("南昌联通")) {
                //联通
                account = account + "%40unicom";
            }
            try {
                String url = address + "&a=" + action + "&callback=" + "result" + "&login_method=" + method + "&user_account=" + account + "&user_password=" + password;
                HttpUtil.sendOkHttpRequest(url, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        loginStatus = response;
//                        Log.d(TAG, "onFinish: "+loginStatus);
                        if ("result({\"result\":\"1\",\"msg\":\"认证成功\"})".equals(loginStatus)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(fab,"认证成功",Snackbar.LENGTH_SHORT).show();
                                    fab.setImageResource(R.drawable.ic_done_black_48dp);
                                }
                            });

                        } else if(loginStatus.contains("\"ret_code\":\"1\"")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(fab,"已在其他设备登录",Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }else if(loginStatus.contains("\"ret_code\":\"2\"")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(fab,"本设备登录",Snackbar.LENGTH_SHORT).setAction("注销", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            logout();
                                            fab.setImageResource(R.drawable.ic_compare_arrows_black_48dp);
                                        }
                                    }).show();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(fab,"请检查用户名、密码以及运营商是否正确",Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onError(Exception e) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        }
    }
    private void logout() {
        String url = "http://172.21.255.105:801/eportal/?c=Portal&a=logout&callback=dr1568650307392&login_method=1&user_account=drcom&user_password=123&ac_logout=0";
        try {
            HttpUtil.sendOkHttpRequest(url, new HttpCallbackListener() {
                @Override
                public void onFinish(final String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Log.d(TAG, ""+response);
                            if ("dr1568650307392({\"result\":\"0\",\"msg\":\"注销失败\"})".equals(response)) {
//                                Toast.makeText(LoginActivity.this,"注销失败",Toast.LENGTH_SHORT).show();
                                Snackbar.make(fab,"注销失败",Snackbar.LENGTH_SHORT).show();
                            }else{
                                Snackbar.make(fab,"注销成功",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,"注销失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ckeckStatus() {
        String address = "http://172.21.255.105";
        HttpUtil.checlStatus(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final String title = HttpUtil.parseHTMLWithRegExp(response);
//                Log.d(TAG, "onFinish: "+title);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!"<title>上网登录页</title>".equals(title)){
                            fab.setImageResource(R.drawable.ic_done_black_48dp);
                            Snackbar.make(fab,"已登录校园网",Snackbar.LENGTH_SHORT).show();
                        }else{
                            Snackbar.make(fab,"未登录校园网",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onError(Exception e) {
//                Log.d(TAG, "onError: ");
                //网络未连接 ，失败
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab,"当前设备未连接至WIFI",Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public boolean getWifiInfo() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo= wm.getConnectionInfo();
        String wifiSSID = wifiInfo.getSSID();
//        Log.d(TAG, "getWifiInfo: ssid"+wifiSSID);
        String ECUT = "\"ECUT_S\"";
        String ECUT_STUD = "\"ECUT_STUD\"";
        String ECUTSTUD = "\"ECUT-STUD\"";
        String fish = "\"子非鱼\"";
        if (ECUT.equals(wifiSSID)||(ECUT_STUD.equals(wifiSSID))||(ECUTSTUD.equals(wifiSSID))||(fish.equals(wifiSSID))){            //已连接
            return true;
        }else {
            Toast.makeText(LoginActivity.this,"未连接至ECUT",Toast.LENGTH_SHORT).show();
        }
        return false;
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
}
