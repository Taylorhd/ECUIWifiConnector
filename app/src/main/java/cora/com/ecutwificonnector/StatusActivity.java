package cora.com.ecutwificonnector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

//http://172.21.255.105/  信息页
//这是注销的请求
//  http://172.21.255.105:801/eportal/?c=Portal&a=logout&callback=dr1568650307392&login_method=1&user_account=drcom&user_password=123&ac_logout=0
//下拉刷新

public class StatusActivity extends AppCompatActivity {
    private TextView textStatus ;
    private static final String TAG = "StatusActivity";
    private Button logoutButton;
    private Button refreshButton;
    private String address = "http://172.21.255.105";
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_status);
        setSupportActionBar(toolbar);
        textStatus = (TextView)findViewById(R.id.text_status);
        logoutButton = (Button)findViewById(R.id.logout_button);
        refreshButton = (Button)findViewById(R.id.refresht_button);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //请求，查看当前状态

        ckeckStatus();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ckeckStatus();
                Toast.makeText(StatusActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://172.21.255.105:801/eportal/?c=Portal&a=logout&callback=dr1568650307392&login_method=1&user_account=drcom&user_password=123&ac_logout=0";
                try {
                    HttpUtil.sendOkHttpRequest(url, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, ""+response);
                                    if ("dr1568650307392({\"result\":\"0\",\"msg\":\"注销失败\"})".equals(response)) {
                                        Toast.makeText(StatusActivity.this,"注销失败",Toast.LENGTH_SHORT).show();
                                    }else{
                                    Toast.makeText(StatusActivity.this,"注销成功",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StatusActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    }
                                }
                            });

//                            Log.d(TAG, "onFinish: 注销成功");
                        }
    
                        @Override
                        public void onError(Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(StatusActivity.this,"注销失败",Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "onError: 注销失败");

                                }
                            });

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ckeckStatus() {
        HttpUtil.checlStatus(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
            final String title = HttpUtil.parseHTMLWithRegExp(response);
                Log.d(TAG, "onFinish: "+title);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("<title>上网登录页</title>".equals(title)){
                            textStatus.setText("当前设备未登录");
                        }else{
                            textStatus.setText("当前设备已登录");
                        }

                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: ");
                //网络未连接 ，失败
                Log.d(TAG, "onError: 未连接至wifi");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textStatus.setText("当前设备未连接至WIFI");
                    }
                });

            }
        });
    }
}
