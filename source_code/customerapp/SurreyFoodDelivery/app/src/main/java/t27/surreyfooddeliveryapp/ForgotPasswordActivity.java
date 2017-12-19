package t27.surreyfooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText text;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        auth = FirebaseAuth.getInstance();
        text = (EditText) findViewById(R.id.email_input);
    }

    public void sendEmail(View view) {
        final String emailAddress = text.getText().toString();
        intent = new Intent(this, LoginActivity.class);

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent
                                    .FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Invalid email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
