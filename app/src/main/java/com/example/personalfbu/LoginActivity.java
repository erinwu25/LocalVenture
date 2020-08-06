package com.example.personalfbu;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    public final String EMAIL = "email";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin, btnCreateAccount;
    private LoginButton btnFBLogin;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.personalfbu",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }
//        catch (PackageManager.NameNotFoundException e) {
//        }
//        catch (NoSuchAlgorithmException e) {
//        }


        // find elements in view
        etUsername = findViewById(R.id.etCreateUsername);
        etPassword = findViewById(R.id.etCreatePassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnFBLogin = (LoginButton) findViewById(R.id.btnFBLogin);


        // see if there is a current logged in user
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // click listeners for login and create account
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }

        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to sign up activity
                Intent toSignup = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(toSignup);
            }
        });
        btnFBLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLoggedIn()) {
                    callbackManager = CallbackManager.Factory.create();
                    btnFBLogin.setPermissions(Arrays.asList(EMAIL));

                    LoginManager.getInstance().registerCallback(callbackManager,
                            new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    // App code

                                    GraphRequest request = GraphRequest.newMeRequest(
                                            loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(JSONObject object, GraphResponse response) {
                                                    //
                                                    try {
                                                        final String email = object.getString("email");
                                                        final String name = object.getString("name");

                                                        // create user?
                                                        ParseUser newuser = new ParseUser();
                                                        newuser.setUsername(email);
                                                        newuser.setPassword(email);
                                                        newuser.put("emailAddress", email);
                                                        newuser.put("Name", name);
                                                        newuser.setEmail(email);
                                                        newuser.signUpInBackground(new SignUpCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                Log.i(TAG, email);
                                                                if (e == null) {
                                                                    loginUser(email, email);
                                                                }
                                                                if ((e!=null) && (e.getCode() == ParseException.USERNAME_TAKEN)) {
                                                                    // parse account created from facebook login already exists and we can proceed to login
                                                                    loginUser(email, email);
                                                                }
                                                                else if ((e!=null) && (e.getCode() == ParseException.EMAIL_TAKEN)) {
                                                                    // parse account created from email (not facebook login) already exists
                                                                    Toast.makeText(LoginActivity.this, "User with that email already exists; cannot continue with Facebook", Toast.LENGTH_LONG).show();
                                                                    return;
                                                                }
                                                            }
                                                        });
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                    Bundle parameters = new Bundle();
                                    parameters.putString("fields", "id,name,email");
                                    request.setParameters(parameters);
                                    request.executeAsync();
                                }

                                @Override
                                public void onCancel() {
                                    // App code
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    // App code
                                }
                            });

                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Toast.makeText(LoginActivity.this, "Error with login. Please try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                // navigate to main activity if user has signed in properly
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, SplashScreen.class);
        startActivity(i);
        finish();
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
