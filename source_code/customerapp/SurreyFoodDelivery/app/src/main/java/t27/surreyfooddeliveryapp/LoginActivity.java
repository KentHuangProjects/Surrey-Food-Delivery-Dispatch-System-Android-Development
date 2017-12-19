package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import t27.surreyfooddeliveryapp.objectstodb.Customer;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    SharedPreferences userInfo_Prefs;
    private DatabaseReference mDatabaseRef;
    EditText email_et;
    EditText password_et;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        email_et = (EditText) findViewById(R.id.email_input);
        password_et = (EditText) findViewById(R.id.password_input);
    }

    public void login_registered_click(View view) {

        email = email_et.getText().toString();
        password = password_et.getText().toString();

        if (!InputValidation.isValidEmail(email)) {
            ((EditText) findViewById(R.id.email_input)).setError("incorrect email");
            Toast.makeText(LoginActivity.this, "Incorrect email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(InputValidation.isWeakPassword(password)){
            ((EditText) findViewById(R.id.password_input)).setError("incorrect password");
            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        signIn();
    }

    private void signIn() {
        // [START sign_in_with_email]

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            readAndSignIn_Customer_Once();

                            //-------update token--------------
                            SharedPreferences tokenPre = getApplicationContext().getSharedPreferences("notifToken",Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = tokenPre.edit();
                            prefsEditor.putString("token", FirebaseInstanceId.getInstance().getToken());
                            prefsEditor.apply();

                            //---------------------
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Log.d(TAG, "email :" + email);
                                Toast.makeText(LoginActivity.this, "Invalid email",
                                        Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG, "email :" + email);
                                Toast.makeText(LoginActivity.this, "Invalid password",
                                        Toast.LENGTH_SHORT).show();
                            } catch (FirebaseNetworkException e) {
                                Toast.makeText(LoginActivity.this, "No network",
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        hideProgressDialog();
                    }
                });
        // [END sign_in_with_email]
    }

    private void readAndSignIn_Customer_Once() {

        FirebaseUser user = mAuth.getCurrentUser();
        final String userUid = user.getUid();

        //get user information from db and store to sharedPreference
        Query userInfo_query = mDatabaseRef.child("users").child(userUid);
        //get the query info(user) once
        userInfo_query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the Customer info in db
                Customer loginUser = (Customer) dataSnapshot.getValue(Customer.class);
                if(loginUser == null) {
                    Toast.makeText(LoginActivity.this, "Invalid email or password.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String curEmail = loginUser.getEmail();

                //store object to sharedPreference
                userInfo_Prefs = getApplicationContext().getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = userInfo_Prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(loginUser);
                prefsEditor.putString("userUID", userUid);
                prefsEditor.putString("userObject", json);
                prefsEditor.putString("curEmail",curEmail);
                prefsEditor.apply();

                //move to home page if succeeds
                Intent fromloginToHome = new Intent(LoginActivity.this, HomeActivity.class);
                fromloginToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(fromloginToHome);

                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(LoginActivity.this, "Failed to sign in.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
