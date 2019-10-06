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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class LoginActivity extends AppCompatActivity {
    private String loginStatus = "";
    private static final String TAG = "LoginActivity";
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
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton fab;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
                default:break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        fab = (FloatingActionButton)findViewById(R.id.fab_login);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"hello",Snackbar.LENGTH_SHORT).show();
                fab.setImageResource(R.drawable.ic_done_black_48dp);
//                fab.setBackgroundResource(R.drawable.ic_done_black_48dp);
            }
        });

        navView.setCheckedItem(R.id.nav_login);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_login:
                        mDrawerLayout.closeDrawers();
                        intent = new Intent(MyApplication.getContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nac_status:
                        mDrawerLayout.closeDrawers();
                        intent = new Intent(MyApplication.getContext(), StatusActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.ques_and_answer:
                        mDrawerLayout.closeDrawers();
                        intent = new Intent(MyApplication.getContext(), QuesAnsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.feedback:
                        mDrawerLayout.closeDrawers();
                        intent = new Intent(MyApplication.getContext(), FeedbackActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.quit:
                        finish();
                        break;
                }
                return false;
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
                Log.d(TAG, "onItemSelected: isp" + isp);
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
        buttonLogin = (Button) findViewById(R.id.bt_login);

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

        //点击登录按钮，判断是否勾选记住密码，
        // 若记住密码，写入SharedPrefrence.
        //若未记住密码，则不处理

        buttonLogin.setOnClickListener(new View.OnClickListener() {



           String address = "http://172.21.255.105:801/eportal/?c=Portal";
           String action = "login";
           String method = "1";

           @Override
           public void onClick(View v) {
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


                   Log.d(TAG, "onClick: account " + account + "    isp:  " + isp);




                   if (isp.equals("南昌移动")) {
                       account = account + "%40cmcc";
                   } else if (isp.equals("南昌电信")) {
                       //电信
                       account = account + "%40telecom";
                   } else if (isp.equals("南昌联通")) {
                       //联通
                       account = account + "%40unicom";
                   }

                   Log.d(TAG, "account : " + account);

                   try {
                       Log.d(TAG, "onClick: password" + password);

                       String url = address + "&a=" + action + "&callback=" + "result" + "&login_method=" + method + "&user_account=" + account + "&user_password=" + password;
                       HttpUtil.sendOkHttpRequest(url, new HttpCallbackListener() {
//                       HttpUtil.sendOkHttpRequest(address, action, "result", method, account, password, new HttpCallbackListener() {
                           @Override
                           public void onFinish(String response) {
                               loginStatus = response;
                               Log.d(TAG, "onFinish: "+loginStatus);
                               if ("result({\"result\":\"1\",\"msg\":\"认证成功\"})".equals(loginStatus)) {
                                   Intent intent = new Intent(LoginActivity.this, StatusActivity.class);
                                   startActivity(intent);
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(MyApplication.getContext(), "认证成功", Toast.LENGTH_LONG).show();
                                       }
                                   });

                               } else if("result({\"result\":\"0\",\"msg\":\"UmFkOk9wcHAgZXJyb3I6IDR8ODJ81cu6xdLR1NrP36Gj\",\"ret_code\":\"1\"})".equals(loginStatus)){
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(MyApplication.getContext(), "已在其他设备登录", Toast.LENGTH_LONG).show();
                                       }
                                   });
                               }else{
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(MyApplication.getContext(), "请检查用户名、密码以及运营商", Toast.LENGTH_LONG).show();
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

       }
);
        //判读账号是否正确，其他业务逻辑
}
    public boolean getWifiInfo() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo= wm.getConnectionInfo();

        String wifiSSID = wifiInfo.getSSID();
        Log.d(TAG, "getWifiInfo: ssid"+wifiSSID);
        String ECUT = "\"ECUT_S\"";
        String ECUT_STUD = "\"ECUT_STUD\"";
        String ECUTSTUD = "\"ECUT-STUD\"";
        String fish = "\"子非鱼\"";


        if (ECUT.equals(wifiSSID)||(ECUT_STUD.equals(wifiSSID))||(ECUTSTUD.equals(wifiSSID))||(fish.equals(wifiSSID))){
            //已连接
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
