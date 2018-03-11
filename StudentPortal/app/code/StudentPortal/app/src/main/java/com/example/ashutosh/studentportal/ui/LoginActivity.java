package com.example.ashutosh.studentportal.ui;

/**
 * Created by Ashutosh on 06-03-2018.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.studentportal.BaseApplication;
import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.modal.UserDetails;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    TextView signupBtn,LoginBtn;
    EditText email,Password;

    int verification=0;
    public String tempEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        signupBtn =(TextView) findViewById(R.id.signBtn);
        LoginBtn = (TextView) findViewById(R.id.loginBtn);
        email = (EditText) findViewById(R.id.emailId);
        Password = (EditText) findViewById(R.id.password);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isIntenetConnected())
                {
                    userLogin();
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"No Internet Connection Found",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userLogin()
    {
        String emailID = email.getText().toString();
        final String password = Password.getText().toString();

        if(TextUtils.isEmpty(emailID))
        {
            email.setError("Please Enter Email ID");
            email.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            Password.setError("Please Enter Password");
            Password.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
        {
            email.setError("Wrong Email");
            email.requestFocus();
            return;
        }

        tempEmail = emailID.substring(0,emailID.indexOf('@'));
        Log.d("parentID",tempEmail);

        mAuth.signInWithEmailAndPassword(emailID, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if(user.isEmailVerified())
                    {
                        //new UserDetailsFetcherTask().execute();
                        String url="https://studentcare-8e935.firebaseio.com/users.json";
                        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                        pd.setMessage("Loading...");
                        pd.show();

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                if(response.equals("null"))
                                {
                                    Toast.makeText(LoginActivity.this, "Not Registered!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    try
                                    {
                                        JSONObject loginObject=new JSONObject(response);

                                        if(!loginObject.has(tempEmail))
                                        {
                                            //phoneNumberET.setText("Given user is not Registered! Please Register first.");
                                            //phoneNumberET.setVisibility(View.VISIBLE);
                                        }
                                        else if(loginObject.getJSONObject(tempEmail).getString("Password").equals(password))
                                        {
                                            String parentID,emailID,firstName,lastName,password,profileUrl;
                                            parentID = tempEmail;
                                            emailID = loginObject.getJSONObject(tempEmail).getString("emailID");
                                            firstName = loginObject.getJSONObject(tempEmail).getString("FirstName");
                                            lastName = loginObject.getJSONObject(tempEmail).getString("LastName");
                                            password = loginObject.getJSONObject(tempEmail).getString("Password");
                                            profileUrl = loginObject.getJSONObject(tempEmail).getString("ProfileURL");
                                            UserDetails userDetails = new UserDetails(parentID,firstName,lastName,emailID,password,profileUrl);
                                            MyPreferenceManager.setUserDetail(LoginActivity.this,userDetails);
                                            pd.dismiss();

                                            Intent intent = new Intent(LoginActivity.this,BaseDashboardActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this,"Incorrect Password",Toast.LENGTH_LONG).show();
                                            //phoneNumberET.setText("Incorrect Password!!");
                                            //phoneNumberET.setVisibility(View.VISIBLE);
                                        }
                                        pd.dismiss();
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
                        rQueue.add(request);
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Email not verified",Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    public class UserDetailsFetcherTask extends AsyncTask<Void,Void,Void>
    {
        DatabaseReference dbRef;
        String parent = tempEmail;
        ProgressDialog pd;
        String profileURL;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(tempEmail);
            /*pd = new ProgressDialog(getApplicationContext());
            pd.setTitle("Collecting Data");
            pd.setMessage("Loading...");
            pd.show();*/
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String parentID,emailID,firstName,lastName,password,profileUrl;
                    parentID = tempEmail;
                    if(dataSnapshot.exists())
                    {
                        emailID = dataSnapshot.child("emailID").getValue().toString();
                        firstName = dataSnapshot.child("FirstName").getValue().toString();
                        lastName = dataSnapshot.child("LastName").getValue().toString();
                        password = dataSnapshot.child("Password").getValue().toString();
                        profileUrl = dataSnapshot.child("ProfileURL").getValue().toString();
                        profileURL = profileUrl;
                        UserDetails userDetails = new UserDetails(parentID,firstName,lastName,emailID,password,profileUrl);
                        MyPreferenceManager.setUserDetail(BaseApplication.getDefaultContext(),userDetails);
                    }
                    else
                    {
                        Log.d("Error","Something went wrong!!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pd.dismiss();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
