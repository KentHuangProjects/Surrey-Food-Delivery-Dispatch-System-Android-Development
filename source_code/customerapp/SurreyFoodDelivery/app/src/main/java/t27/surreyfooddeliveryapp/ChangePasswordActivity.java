package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import t27.surreyfooddeliveryapp.LocalOrders.CheckConnection;
import t27.surreyfooddeliveryapp.objectstodb.Customer;

public class ChangePasswordActivity extends AppCompatActivity {
    String TAG = "ChangePasswordActivity";
    EditText oldpass_et;
    EditText newpass_et;
    EditText newpass_et2;
    String oldpass;
    String newpass;
    String newpass2;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldpass_et = (EditText) findViewById(R.id.edit_pass);
        newpass_et = (EditText) findViewById(R.id.edit_new_pass);
        newpass_et2 = (EditText) findViewById(R.id.edit_confirm_pass);
    }

    public void change_pass_click(View view) {
        if(!CheckConnection.isOnline(ChangePasswordActivity.this)) {
            Toast.makeText(ChangePasswordActivity.this, "No Network",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        oldpass = oldpass_et.getText().toString();
        newpass = newpass_et.getText().toString();
        newpass2 = newpass_et2.getText().toString();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null&& !oldpass.isEmpty()) {
            email = user.getEmail();
            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, oldpass);
            //new passwords are not null and they are the same
            if(!newpass.isEmpty()&&!newpass2.isEmpty()&&newpass.equals(newpass2)){
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Password updated");
                                                Toast.makeText(ChangePasswordActivity.this, "Password updated",
                                                        Toast.LENGTH_SHORT).show();

                                                //----modify the object in sharedPreference
                                                SharedPreferences shar_pre = getApplicationContext()
                                                        .getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
                                                Gson gson = new Gson();
                                                String json = shar_pre.getString("userObject", null);

                                                //log in as customer or restaurant
                                                //or logged in before
                                                if (json != null) {
                                                    Customer cur_customer = gson.fromJson(json, Customer.class);
                                                    cur_customer.setPassword(newpass);

                                                    //store back to sharedpre
                                                    SharedPreferences.Editor prefsEditor = shar_pre.edit();
                                                    gson = new Gson();
                                                    json = gson.toJson(cur_customer);
                                                    prefsEditor.putString("userObject", json);
                                                    prefsEditor.apply();
                                                    //end----store back
                                                }
                                                //end ------modify the object in sharedPreference
                                                finish();
                                            } else {
                                                try {
                                                    throw task.getException();
                                                } catch (Exception e) {
                                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                Log.d(TAG, "Error password not updated");
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Error invalid credential",
                                            Toast.LENGTH_SHORT).show();

                                    Log.d(TAG, "Error auth failed");
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Please enter identical new passwords",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
