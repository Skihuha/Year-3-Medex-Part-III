package ke.co.comsterhomes.www.firebase24;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    TextView text2;
    SignInButton signinbtn;
    private ProgressDialog progressDialog;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Login Activity:";
    private Button submitbtn;
    private EditText emailText, passwordText;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text2 = findViewById(R.id.statustxt2);
        emailText = (EditText) findViewById(R.id.lploginemail);
        passwordText = (EditText) findViewById(R.id.lploginpassword);
        submitbtn = (Button) findViewById(R.id.lpLoginBtn);
        submitbtn.setOnClickListener(this);
        /*signinbtn = (SignInButton) findViewById(R.id.signingooglebtn3);
        signinbtn.setOnClickListener(this);*/

        progressDialog = new ProgressDialog(this);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null){
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Logged in as :"+ firebaseAuth.getCurrentUser(),Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lpLoginBtn:
                text2.setText("HELLO");
                if (TextUtils.isEmpty(emailText.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(),"Email Field is empty",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(passwordText.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(),"Password Field is empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    logIn();
                }
                break;
        }
    }

    private void logIn() {
        String email = emailText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Email field empty",Toast.LENGTH_SHORT);
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Password field empty",Toast.LENGTH_SHORT);
            return;
        }

        progressDialog.setTitle("Loging In user.");
        progressDialog.setMessage("Please wait while we Log you in!");
        progressDialog.setProgress(100);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    if (password.length()<6){
                        passwordText.setError("Password too short");
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_SHORT);
                    }

                }else {

                    finish();
                }
            }
        });



    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
