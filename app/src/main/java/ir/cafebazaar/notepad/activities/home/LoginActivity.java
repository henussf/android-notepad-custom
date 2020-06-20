package ir.cafebazaar.notepad.activities.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.cafebazaar.notepad.App;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import ir.cafebazaar.notepad.R;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final String TAG = "LoginActivity";

    EditText mUserTextView = null;
    EditText mPasswordTextView = null;
    Button mLoginButton = null;

    private String mUser = null;
    private String mPassword = null;

    protected OkHttpClient mClient;

//    private static String url = "http://localhost:8083/auth/login";

    private static String url = "http://172.20.30.171:8083/auth/login";

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onCreate()" );
        super.onCreate(arg0);
        mClient = App.getHttpClient();
        this.setTitle("登录");
        this.setContentView(R.layout.activity_login);

        mUserTextView = (EditText) this.findViewById(R.id.et_username);
        mPasswordTextView = (EditText) this.findViewById(R.id.et_password);
        mLoginButton = (Button) this.findViewById(R.id.btn_login);
        mLoginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick()" );
        int id = v.getId();
        mUser = mUserTextView.getText().toString();
        mPassword = mPasswordTextView.getText().toString();
        if(mUser ==null){
            mUser = "user";
        }
        if(mPassword ==null){
            mPassword = "123456";
        }
        login();

    }

    private void login() {
        Log.e(TAG, "login mUser " + mUser + " mPassword " + mPassword );
        //创建一个FormBody.Builder
        FormBody.Builder builder=new FormBody.Builder();
        builder.add("uname", mUser);
        builder.add("pword", mPassword);
        RequestBody formBody=builder.build();

        Request request=new Request.Builder()
                .post(formBody)
                .url(url)
                .build();


        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure()" );
                Log.e(TAG, "onFailure()" + e.toString() );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonStr = response.body().string();
                Log.e(TAG, "onResponse " + jsonStr );
                parseJson(jsonStr);
            }
        });
        Log.e(TAG, "login end ");
    }


    private void parseJson(String data){
//        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement dataJson = parser.parse(data);
        JsonObject json = dataJson.getAsJsonObject();

        if(json.has("status")) {
            int status = json.get("status").getAsInt();
            Log.e(TAG, "parseJson status " + status);
            if (status == 200) {
                String token = json.get("data").getAsString();
                Log.e(TAG, "parseJson token " + token);
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }else {
            Log.e(TAG, "parseJson error ");
        }
    }

//    private void login(JsonObject json) {
//        if (!checkNetStatus()) {
//            return;
//        }
//        RequestBody body=RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
//        Request request=new Request.Builder()
//                .post(body)
//                .url(CodeUtil.APP_REQUEST_Login)
//                .build();
//        mClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                updateToast(R.string.net_connect_error);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String jsonStr = response.body().string();
//                JsonObject json;
//                try {
//                    json = jsonParser.parse(jsonStr).getAsJsonObject();
//                    JsonObject jsonObject=json.get("meta").getAsJsonObject();
//                    String code=jsonObject.get("code").getAsString();
//                    if (checkResponseCode(code)) {
//                        String token = json.get("data").getAsString();
//                        TLog.log("login()", json.toString());
//                        CodeUtil.setToken(getApplicationContext(), token);
//                        updateToast("登录成功");
//                        startActivity(new Intent(LoginActivity.this,MainActivity.class)
//                                .putExtra("isOpenDialog",0));
//                        LoginActivity.this.finish();
//                    }else{
//                        if( jsonObject.has("message") ) {
//                            JsonElement tempElement = jsonObject.get("message");
//                            if( tempElement.toString() != null&&!tempElement.toString().equals("null") ) {
//                                updateToast(tempElement.getAsString());
//                            }
//                        }
//                    }
//                }catch (JsonSyntaxException e){
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

}
