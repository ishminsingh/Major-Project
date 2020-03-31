package dev.ishmin.firebareotp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    private EditText otp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        otp1 = findViewById(R.id.otp1);

        Intent intent = getIntent();
        String mobile = intent.getStringExtra("mobile");
        sendVerificationCode(mobile);

        findViewById(R.id.login1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp1.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    otp1.setError("Enter valid code");
                    otp1.requestFocus();
                    return;
                }
                verifyVerificationCode(code);
            }
        });
    }
    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            //verification unsuccessful.. display an error message
                            Toast.makeText(VerifyPhoneActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp1.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            //mResendToken = forceResendingToken;
        }
    };
}
