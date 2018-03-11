package com.example.ashutosh.studentportal.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.modal.UserDetails;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ashutosh on 06-03-2018.
 */

public class SignUpActivity extends AppCompatActivity {
    EditText firstName,lastname,email,password,confirmpassword;
    TextView signupBtn;

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_signup);

        firstName = (EditText) findViewById(R.id.firstname);
        lastname = (EditText)findViewById(R.id.lastname);
        //username = (EditText)findViewById(R.id.userName);
        email = (EditText)findViewById(R.id.emailRegister);
        password = (EditText)findViewById(R.id.passwordRegister);
        confirmpassword = (EditText)findViewById(R.id.confirmPassword);
        signupBtn = (TextView) findViewById(R.id.SignupBtn);

        mAuth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isIntenetConnected())
                {
                    registerUser();
                }
                else
                {
                    Toast.makeText(SignUpActivity.this,"No Internet Connection Found",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void registerUser()
    {
        final String emailID = email.getText().toString();
        final String pass = password.getText().toString();
        //final String userName = username.getText().toString();
        final String first = firstName.getText().toString();
        final String last = lastname.getText().toString();
        String confirm = confirmpassword.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
        {
            email.setError("Wrong Email");
            email.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(first))
        {
            firstName.setError("Please Enter User Name");
            firstName.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(last))
        {
            lastname.setError("Please Enter User Name");
            lastname.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pass))
        {
            password.setError("Please Enter Password");
            password.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(confirm))
        {
            confirmpassword.setError("Please Enter User Name");
            confirmpassword.requestFocus();
            return;
        }

        if(!TextUtils.equals(pass,confirm))
        {
            confirmpassword.setError("Password not matching");
            confirmpassword.requestFocus();
            return;
        }

        /*if(TextUtils.isEmpty(userName))
        {
            username.setError("Please Enter User Name");
            username.requestFocus();
            return;
        }*/

        if(TextUtils.isEmpty(emailID))
        {
            email.setError("Please Enter Email ID");
            email.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailID,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    databaseReference = FirebaseDatabase.getInstance().getReference("users");

                    //String id = databaseReference.push().getKey();
                    String parent = emailID.substring(0,emailID.indexOf('@'));
                    databaseReference.child(parent).child("FirstName").setValue(first);
                    databaseReference.child(parent).child("LastName").setValue(last);
                    databaseReference.child(parent).child("ProfileURL").setValue("null");
                    //databaseReference.child(parent).child("UserName").setValue(userName);
                    databaseReference.child(parent).child("Password").setValue(pass);
                    databaseReference.child(parent).child("emailID").setValue(emailID);

                    UserDetails userDetails = new UserDetails(parent,first,last,emailID,pass,"");
                    MyPreferenceManager.setUserDetail(SignUpActivity.this,userDetails);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            //text.setText("Verification link sent to your email, verify then login");
                            Toast.makeText(SignUpActivity.this, "Email has been sent to your email id.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(),"Already registered",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //checking connection status of the device
    private boolean isIntenetConnected()
    {
        //initialising status of the device whether is connected to internet or not
        boolean isConnected = false;

        //creating connectivityManager object to check connection status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //getting network information
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if connected to internet then changing its value
        if (networkInfo != null) {
            isConnected = true;
        }

        //returning connection status
        return isConnected;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
