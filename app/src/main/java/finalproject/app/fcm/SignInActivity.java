package finalproject.app.fcm;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

import finalproject.app.fcm.util.PreferenceManager;
import finalproject.app.fcm.vo.MemberVO;

public class SignInActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> signIn_Launcher;
    EditText id, pwd;
    String point;
    int resultCode;
    Context mContext;
    CheckBox autoSignIn, saveIdAndPwd;
    MemberVO User;
    JSONObject Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mContext = this;
        int resultCode = 0;
        MemberVO User = null;
        JSONObject Info = null;
        // autoSignIn = findViewById(R.id.autoLogin);
        saveIdAndPwd = findViewById(R.id.rememberIdandPwd);
        id = findViewById(R.id.signInId);
        pwd = findViewById(R.id.signInPwd);

        signIn_Launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            autoSignIn.setChecked(false);
                            saveIdAndPwd.setChecked(false);
                            PreferenceManager.clear(mContext);
                        }
                    }
                });

        /* save Id And Pwd */
        saveIdAndPwd.setOnClickListener(view -> {
            if(saveIdAndPwd.isChecked()){
                PreferenceManager.setString(mContext,"id",id.getText().toString());
                PreferenceManager.setString(mContext,"pwd",pwd.getText().toString());
                PreferenceManager.setBoolean(mContext,"saveIdAndPwd",saveIdAndPwd.isChecked());
                Log.d("signIn","saveIdAndPwd: "+PreferenceManager.getBoolean(mContext,"saveIdAndPwd")+", (id="+PreferenceManager.getString(mContext,"id")+", pwd="+PreferenceManager.getString(mContext,"pwd")+")");
            }else {
                PreferenceManager.setBoolean(mContext,"saveIdAndPwd",saveIdAndPwd.isChecked());
                PreferenceManager.clear(mContext);
                Log.d("signIn","saveIdAndPwd: "+PreferenceManager.getBoolean(mContext,"saveIdAndPwd"));
            }
        });
        Boolean saveIdAndPwdCheck = PreferenceManager.getBoolean(mContext,"saveIdAndPwd");
        if(saveIdAndPwdCheck){
            id.setText(PreferenceManager.getString(mContext,"id"));
            pwd.setText(PreferenceManager.getString(mContext,"pwd"));
            saveIdAndPwd.setChecked(true);
        }

        /* Auto Sign In *//*
        autoSignIn.setOnClickListener(view -> {
            if(autoSignIn.isChecked()){
                saveIdAndPwd.setChecked(true);
                PreferenceManager.setBoolean(mContext,"autoSignIn",saveIdAndPwd.isChecked());
                Log.d("signIn","saveIdAndPwd: "+PreferenceManager.getBoolean(mContext,"autoSignIn")+", (id="+PreferenceManager.getString(mContext,"id")+", pwd="+PreferenceManager.getString(mContext,"pwd")+")");
            }else {
                saveIdAndPwd.setChecked(false);
                PreferenceManager.setBoolean(mContext,"autoSignIn",saveIdAndPwd.isChecked());
                Log.d("signIn","saveIdAndPwd: "+PreferenceManager.getBoolean(mContext,"autoSignIn"));
                PreferenceManager.clear(mContext);
            }
        });
        Boolean autoSignInCheck = PreferenceManager.getBoolean(mContext,"autoSignIn");
        if(autoSignInCheck){
            List<String> signInInfo = new ArrayList<String>();
            signInInfo.add(PreferenceManager.getString(mContext,"id"));
            signInInfo.add(PreferenceManager.getString(mContext,"pwd"));
            try {
                resultCode = signINHttp(signInInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(resultCode==0){
                id.setText("");
                pwd.setText("");
                toast("자동 로그인에 실패했습니다.");
            }else if (resultCode==1){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (signINHttp(signInInfo)==0) {
                                id.setText("");
                                pwd.setText("");
                            } else {
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                intent.putExtra("MemberId", User.getMem_id());
                                intent.putExtra("MemberPwd", User.getMem_pwd());
                                intent.putExtra("MemberName", User.getMem_name());
                                intent.putExtra("MemberTel", User.getMem_tel() + "");
                                intent.putExtra("MemberPoint", User.getMem_money() + "");
                                intent.putExtra("MemberCar1", User.getMem_car1());
                                intent.putExtra("MemberCar2", User.getMem_car2());
                                Log.d("signIn", "User: " + User.toString());
                                autoSignIn.setChecked(true);
                                saveIdAndPwd.setChecked(true);
                                signIn_Launcher.launch(intent);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }*/
    }
    public void goSignUp(View v) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
    public void signIn(View v) {
        List<String> signInInfo = new ArrayList<String>();
        id = findViewById(R.id.signInId);
        pwd = findViewById(R.id.signInPwd);
        String idval = id.getText().toString();
        String pwdval = pwd.getText().toString();
        if(idval!=null&pwdval!=null){
            signInInfo.add(idval);
            signInInfo.add(pwdval);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        resultCode = signINHttp(signInInfo);
                        if (resultCode==0) {
                            id.setText("");
                            pwd.setText("");
                        } else {
                            resultCode = 1;
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.putExtra("MemberId",User.getMem_id());
                            intent.putExtra("MemberPwd",User.getMem_pwd());
                            intent.putExtra("MemberName",User.getMem_name());
                            intent.putExtra("MemberTel",User.getMem_tel()+"");
                            intent.putExtra("MemberPoint",User.getMem_money()+"");
                            intent.putExtra("MemberCar1",User.getMem_car1());
                            intent.putExtra("MemberCar2",User.getMem_car2());
                            Log.d("signIn","User: "+User.toString());
                            signIn_Launcher.launch(intent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            toast("아이디와 비밀번호를 확인해 주세요.");
        }
    }
    public void toast(String msg){
        Toast.makeText(this,msg+"",Toast.LENGTH_SHORT).show();
    }
    public void goFindId(View v) {
        Intent intent = new Intent(SignInActivity.this, FindIdActivity.class);
        startActivity(intent);
    }

    public void goFindPwd(View v) {
        Intent intent = new Intent(SignInActivity.this, FindPwdActivity.class);
        startActivity(intent);
    }

    public int signINHttp(List<String> signInInfo) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.0.123:80/Finalweb/userlogin.mc");
        ArrayList<NameValuePair> MemberInfo = new ArrayList<NameValuePair>();
        try {
            MemberInfo.add(new BasicNameValuePair("id", URLDecoder.decode(signInInfo.get(0), "UTF-8")));
            MemberInfo.add(new BasicNameValuePair("pwd", URLDecoder.decode(signInInfo.get(1), "UTF-8")));
            post.setEntity(new UrlEncodedFormEntity(MemberInfo, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Log.e("signIn", ex.toString());
        }
        HttpResponse response = client.execute(post);
        Log.d("signIn", "response StatusCode:" + response.getStatusLine().getStatusCode()); // response StatusCode: 200
        HttpEntity resEntity = response.getEntity();
        String ResultInfo = EntityUtils.toString(resEntity);
        if(!ResultInfo.equals("null")){
            Log.d("signIn", point + "");
            try {
                JSONArray UserInfo = new JSONArray(ResultInfo);
                Info = UserInfo.getJSONObject(0);
                String mem_id = Info.getString("mem_id");
                String mem_pwd = Info.getString("mem_pwd");
                String mem_name = Info.getString("mem_name");
                String mem_tel = Info.getString("mem_tel");
                String mem_money = Info.getString("mem_money");
                String mem_car1 = Info.getString("mem_car1");
                String mem_car2 = Info.getString("mem_car2");
                Log.d("signIn",mem_id+", "+mem_pwd+", "+mem_name+", "+mem_tel+", "+mem_money+", "+mem_car1+", "+mem_car2);
                User = new MemberVO(mem_id, mem_pwd, mem_name, Integer.parseInt(mem_tel), Integer.parseInt(mem_money), mem_car1, mem_car2);
                Log.d("signIn",User.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 1;
        }
        return 0;
    }
}