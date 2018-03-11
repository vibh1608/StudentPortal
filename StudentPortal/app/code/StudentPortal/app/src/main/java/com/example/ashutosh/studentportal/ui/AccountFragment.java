package com.example.ashutosh.studentportal.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.modal.UserDetails;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.dbRef;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.userDetails;

/**
 * Created by Ashutosh on 10-03-2018.
 */

public class AccountFragment extends Fragment {

    public static String TAG = "accountFragment";
    View view;

    @BindView(R.id.back_btn_iv)
    ImageView backBtnIV;
    @BindView(R.id.fullname_rl)
    RelativeLayout fullNameRL;
    @BindView(R.id.fullname_tv)
    TextView fullNameTV;
    @BindView(R.id.lastname_tv)
    TextView lastNameTV;
    @BindView(R.id.lastname_rl)
    RelativeLayout lastNameRL;
    @BindView(R.id.password_rl)
    RelativeLayout passwordRL;
    @BindView(R.id.logout_card)
    CardView logoutCard;

    //UserDetails details;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(userDetails==null)
            userDetails = MyPreferenceManager.getUserDetail(getActivity());

        //details = userDetails;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this,view);
        fullNameTV.setText(userDetails.getFirstName());
        lastNameTV.setText(userDetails.getLastName());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.back_btn_iv)
    public void onBackPressed()
    {
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.fullname_rl)
    public void onFirstNameClicked()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.change_first_name_dialog);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText fnET = (EditText)dialog.findViewById(R.id.change_firstname_et);
        TextView saveBtn = (TextView)dialog.findViewById(R.id.save_firstname_tv);
        TextView cancelBtn = (TextView)dialog.findViewById(R.id.cancel_tv);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fnET.getText().toString().trim().isEmpty())
                {
                    String fName = fnET.getText().toString().trim();
                    dialog.dismiss();
                    dbRef.child("users").child(userDetails.getParentID()).child("FirstName").setValue(fName);
                    userDetails.setFirstName(fName);
                    MyPreferenceManager.setUserDetail(getActivity(),userDetails);
                    fullNameTV.setText(fName);
                }
                else
                {
                    fnET.setError("First fill the detail");
                }
            }
        });
    }

    @OnClick(R.id.lastname_rl)
    public void onLastNameClicked()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.change_last_name_dialog);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText fnET = (EditText)dialog.findViewById(R.id.change_lastname_et);
        TextView saveBtn = (TextView)dialog.findViewById(R.id.save_lastname_tv);
        TextView cancelBtn = (TextView)dialog.findViewById(R.id.cancel_tv);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fnET.getText().toString().trim().isEmpty())
                {
                    String fName = fnET.getText().toString().trim();
                    dialog.dismiss();
                    dbRef.child("users").child(userDetails.getParentID()).child("LastName").setValue(fName);
                    userDetails.setLastName(fName);
                    MyPreferenceManager.setUserDetail(getActivity(),userDetails);
                    lastNameTV.setText(fName);
                }
                else
                {
                    fnET.setError("First fill the detail");
                }
            }
        });
    }

    @OnClick(R.id.password_rl)
    public void onChangePasswordClicked()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.change_password_dialog);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText currentPasswordET=(EditText)dialog.findViewById(R.id.current_password_et);
        final EditText newPasswordET=(EditText)dialog.findViewById(R.id.new_password_et);
        final EditText confirmNewPasswordET=(EditText)dialog.findViewById(R.id.confirm_new_password_et);
        TextView cancelTV=(TextView)dialog.findViewById(R.id.cancel_tv);
        TextView savePasswordTv=(TextView)dialog.findViewById(R.id.save_password_tv);

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        savePasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentPasswordET.getText().toString().trim().isEmpty()
                        && !newPasswordET.getText().toString().trim().isEmpty()
                        && !confirmNewPasswordET.getText().toString().trim().isEmpty())
                {
                    if(currentPasswordET.getText().toString().trim().equals(userDetails.getPassword()))
                    {
                        if(newPasswordET.getText().toString().trim().equals(confirmNewPasswordET.getText().toString().trim()))
                        {
                            String newPassword=newPasswordET.getText().toString().trim();
                            dialog.dismiss();
                            userDetails.setPassword(newPassword);
                            dbRef.child("users").child(userDetails.getParentID()).child("Password").setValue(newPassword);
                            MyPreferenceManager.setUserDetail(getActivity(),userDetails);
                        }
                    }
                    else
                    {
                        currentPasswordET.setError("This Password is Incorrect!");
                    }
                }
                else
                {
                    if(currentPasswordET.getText().toString().trim().isEmpty())
                        currentPasswordET.setError("Please write appropriate password.");
                    if(newPasswordET.getText().toString().trim().isEmpty())
                        newPasswordET.setError("Please fill the password.");
                    if(confirmNewPasswordET.getText().toString().trim().isEmpty())
                        confirmNewPasswordET.setError("Please fill the password.");
                }
            }
        });
    }

    @OnClick(R.id.logout_card)
    public void onLogoutBtnClicked()
    {
        MyPreferenceManager.deleteAllData(getActivity());
        Intent i = new Intent(getActivity(),LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}
