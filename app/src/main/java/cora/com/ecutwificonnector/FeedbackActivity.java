package cora.com.ecutwificonnector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {
    private Button feedback ;
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
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_feedback);
        setSupportActionBar(toolbar);
        feedback = (Button)findViewById(R.id.feedback_buttom);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + "2469458627";
                    //uin是发送过去的qq号码
                    getBaseContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) {
//                    ToastUtils.showShortToast(context, "您还没有安装QQ，请先安装软件");
                  Toast.makeText(MyApplication.getContext(),"未安装QQ，请安装后再试",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
