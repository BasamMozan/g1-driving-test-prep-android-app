package com.harsh_bhardwaj.g1prep.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.harsh_bhardwaj.g1prep.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterLoginActivity extends AppCompatActivity {

    public static final String CREDENTIALS_PREFERENCES = "CREDENTIALS_PREFERENCES";
    public static Pattern VALID_EMAIL_ADDRESS_REGEX;
    SharedPreferences sharedpreferences;
    Vibrator vibrator;

    private static final String LOGIN_MODE = "login", REGISTER_MODE = "register", SUCCESS = "200";
    private String curMode;
    private static String URL;
    Context context;
    TextView toggleModeTextView;
    View nameDividerView;
    EditText nameEditText, emailEditText, passwordEditText;
    Button registerLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        toggleModeTextView = findViewById(R.id.toggleModeTextView);
        nameDividerView = findViewById(R.id.nameDividerView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerLoginButton = findViewById(R.id.registerLoginButton);

        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        sharedpreferences = getSharedPreferences(CREDENTIALS_PREFERENCES, Context.MODE_PRIVATE);
        changeUIBasedOnMode(LOGIN_MODE);

        toggleModeTextView.setOnClickListener(view -> {
            if (curMode.equals(LOGIN_MODE)) {
                changeUIBasedOnMode(REGISTER_MODE);
            } else {
                changeUIBasedOnMode(LOGIN_MODE);
            }
        });

        registerLoginButton.setOnClickListener(view -> {
            vibrator.vibrate(50);
            if (!validate(String.valueOf(emailEditText.getText()))) {
                Toast.makeText(context, "Invalid email id", Toast.LENGTH_SHORT).show();
                return;
            }
            sendRequest();
        });
    }

    private void changeUIBasedOnMode(String newMode) {
        curMode = newMode;

        if (curMode.equals(LOGIN_MODE)) {
            URL = getResources().getString(R.string.login_url);

            String emailString = sharedpreferences.getString("email", "");
            String passwordString = sharedpreferences.getString("password", "");

            emailEditText.setText(emailString);
            passwordEditText.setText(passwordString);

            registerLoginButton.setText("LOGIN");
            nameDividerView.setVisibility(View.GONE);
            nameEditText.setVisibility(View.GONE);

            toggleModeTextView.setText("New User? Register");

        } else {
            URL = getResources().getString(R.string.register_url);

            emailEditText.setText("");
            passwordEditText.setText("");

            registerLoginButton.setText("REGISTER");
            nameDividerView.setVisibility(View.VISIBLE);
            nameEditText.setVisibility(View.VISIBLE);
            toggleModeTextView.setText("Existing User? Login");
        }
    }

    private void sendRequest() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", emailEditText.getText());
            jsonBody.put("password", passwordEditText.getText());

            if (curMode.equals(REGISTER_MODE)) {
                jsonBody.put("name", nameEditText.getText());
            }
            registerLoginButton.setEnabled(false);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                if (response.equals(SUCCESS)) {
                    registerLoginButton.setEnabled(true);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("email", String.valueOf(emailEditText.getText()));
                    editor.putString("password", String.valueOf(passwordEditText.getText()));
                    editor.apply();

                    Intent intent = new Intent(context, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }, error -> {
                registerLoginButton.setEnabled(true);
                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}