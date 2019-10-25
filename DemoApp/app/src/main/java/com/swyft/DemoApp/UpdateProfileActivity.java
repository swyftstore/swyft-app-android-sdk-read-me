package com.swyft.DemoApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.swyftstore.sdk.networking.model.SwyftUser;
import com.swyftstore.sdk.singletons.SwyftSdk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateProfileActivity extends AppCompatActivity {

    static public final String FIRST_NAME = "firstName";
    static public final String LAST_NAME = "lastName";
    static public final String EMAIL = "email";
    static public final String PHONE_NUMBER = "phoneNumber";

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @BindView(R2.id.first_name)
    protected EditText firstNameField;
    @BindView(R2.id.last_name)
    protected EditText lastNameField;
    @BindView(R2.id.email)
    protected EditText emailField;
    @BindView(R2.id.phone_number)
    protected EditText phoneNumberField;
    @BindView(R2.id.progressBarContainer)
    protected RelativeLayout containerProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        final Intent intent = getIntent();
        firstName = intent.getStringExtra(FIRST_NAME);
        lastName = intent.getStringExtra(LAST_NAME);
        email = intent.getStringExtra(EMAIL);
        phoneNumber = intent.getStringExtra(PHONE_NUMBER);

    }

    @Override
    protected void onResume() {
        super.onResume();
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        emailField.setText(email);
        phoneNumberField.setText(phoneNumber);
    }

    @OnClick(R2.id.submit)
    protected void onSubmit() {
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        email = emailField.getText().toString();
        phoneNumber = phoneNumberField.getText().toString();
        final SwyftUser user = new SwyftUser(firstName, lastName, email, phoneNumber);
        SwyftSdk.getInstance().updateUser(user, new SwyftSdk.UpdateUserCallBack() {
            @Override
            public void onSuccess(String s) {
                containerProgress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "User Profile Updated!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String s) {
                containerProgress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "User Profile Update failed: " + s, Toast.LENGTH_LONG).show();

                Log.e("Demo App", "onFailure " + s);
            }
        });
    }
}
