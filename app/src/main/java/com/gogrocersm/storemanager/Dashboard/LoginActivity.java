package com.gogrocersm.storemanager.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.franmontiel.localechanger.LocaleChanger;
import com.gogrocersm.storemanager.AppController;
import com.gogrocersm.storemanager.Config.BaseURL;
import com.gogrocersm.storemanager.MainActivity;
import com.gogrocersm.storemanager.MyFirebaseMessagingService;
import com.gogrocersm.storemanager.R;
import com.gogrocersm.storemanager.util.ConnectivityReceiver;
import com.gogrocersm.storemanager.util.CustomVolleyJsonRequest;
import com.gogrocersm.storemanager.util.Session_management;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.Registrar;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = LoginActivity.class.getSimpleName();
    private RelativeLayout btn_continue;
    public String token;

    private EditText et_password, et_email,et_address;
    SharedPreferences sharedPreferences;
    private TextView tv_password, tv_email, btn_forgot,tv_address;
    Context context;
    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(newBase);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        et_password = (EditText) findViewById(R.id.et_login_pass);
        et_email = (EditText) findViewById(R.id.et_login_email);
        tv_password = (TextView) findViewById(R.id.tv_login_password);
        tv_email = (TextView) findViewById(R.id.tv_login_email);
        btn_continue = (RelativeLayout) findViewById(R.id.btnContinue);
        btn_forgot = (TextView) findViewById(R.id.btnForgot);


        btn_forgot.setVisibility(View.INVISIBLE);


        btn_continue.setOnClickListener(this);
        btn_forgot.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnContinue) {
            attemptLogin();
        } else if (id == R.id.btnForgot) {
            Intent startRegister = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(startRegister);
        }
    }

    private void attemptLogin() {
        token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("grocery");


        tv_email.setText(getResources().getString(R.string.tv_login_email));
        tv_password.setText(getResources().getString(R.string.tv_login_password));


        tv_password.setTextColor(getResources().getColor(R.color.green));
        tv_email.setTextColor(getResources().getColor(R.color.green));

        String getpassword = et_password.getText().toString();
        String getemail = et_email.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(getpassword)) {
            tv_password.setTextColor(getResources().getColor(R.color.green));
            focusView = et_password;
            cancel = true;
        } else if (!isPasswordValid(getpassword)) {
            tv_password.setText(getResources().getString(R.string.password_too_short));
            tv_password.setTextColor(getResources().getColor(R.color.green));
            focusView = et_password;
            cancel = true;
        }


        if (TextUtils.isEmpty(getemail)) {

            tv_email.setTextColor(getResources().getColor(R.color.green));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(getemail)) {
            tv_email.setText(getResources().getString(R.string.invalide_email_address));
            tv_email.setTextColor(getResources().getColor(R.color.green));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
        } else {
            if (ConnectivityReceiver.isConnected()) {
                makeLoginRequest(getemail, getpassword);
            }
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeLoginRequest(String email, final String password) {
        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_email", email);
        params.put("password", password);
       //


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.LOGIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        JSONObject obj = response.getJSONObject("data");
                        String user_id = obj.getString("user_id");
                        SharedPreferences.Editor editor = getSharedPreferences("logindata", MODE_PRIVATE).edit();
                        editor.putString("id", user_id);
                        editor.commit();

                        String user_fullname = obj.getString("user_fullname");
                        String user_email = obj.getString("user_email");
                        String user_phone = obj.getString("user_phone");
                        String user_image = obj.getString("user_image");
                        String user_lat=obj.getString("lat");
                        String user_lang=obj.getString("lang");

                        Session_management sessionManagement = new Session_management(LoginActivity.this);
                        sessionManagement.createLoginSession(user_id, user_email, user_fullname, user_phone, user_image, "", "", "", "", password);

                       // .makeText(LoginActivity.this, user_id, Toast.LENGTH_SHORT).show();
                        if(user_lat.equals("")|| user_lang.equals("")){
                            Toast.makeText(LoginActivity.this, "Please pick your location", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this,EditProfile.class);
                            startActivity(intent);
                            finish();


                            return;



                        }

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("userid",user_id);
                        startActivity(i);
                        finish();
                        checkLogin(user_id, token);
                        btn_continue.setEnabled(false);

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                        btn_continue.setEnabled(true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void checkLogin(String user_id, String token) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_login_req";

        //showpDialog();

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("device","android");

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.JSON_RIGISTER_FCM, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        JSONObject obj = new JSONObject();



                        //onBackPressed();


                    }else{
                        String error = response.getString("error");
                        Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                //hidepDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
