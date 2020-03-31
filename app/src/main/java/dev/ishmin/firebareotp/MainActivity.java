package dev.ishmin.firebareotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText phone1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone1 = findViewById(R.id.phone1);
        findViewById(R.id.contBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = phone1.getText().toString().trim();
                if (mobile.isEmpty() || mobile.length() < 10) {
                    phone1.setError("Enter a valid mobile");
                    phone1.requestFocus();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
