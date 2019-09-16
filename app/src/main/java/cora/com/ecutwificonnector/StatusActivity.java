package cora.com.ecutwificonnector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

//http://172.21.255.105/  信息页
//这是注销的请求
//  http://172.21.255.105:801/eportal/?c=Portal&a=logout&callback=dr1568650307392&login_method=1&user_account=drcom&user_password=123&ac_logout=0
public class StatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
    }
}
