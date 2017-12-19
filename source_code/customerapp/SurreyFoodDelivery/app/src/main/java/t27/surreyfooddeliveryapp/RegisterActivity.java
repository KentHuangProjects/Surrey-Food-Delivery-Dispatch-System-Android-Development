package t27.surreyfooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import t27.surreyfooddeliveryapp.LocalOrders.CheckConnection;
import t27.surreyfooddeliveryapp.objectstodb.Customer;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    //xml input fields
    private EditText email_EditText;
    private EditText password_EditText;
    private EditText password2_EditText;
    private EditText name_EditText;
    private EditText phone_EditText;
    private EditText address_EditText;
    private EditText addressDetail_EditText;
    private RadioGroup type_RadioGroup;
    private RadioButton accountType_RadioButton;

    //inputs
    private String accountUID;
    private String email;
    private String password;
    private String password2;
    private String name;
    private String phone;
    private String address;
    private String addressDetail;
    private String accountType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get input fields in xml
        email_EditText = (EditText) findViewById(R.id.email_et);
        password_EditText = (EditText) findViewById(R.id.password_et);
        password2_EditText = (EditText) findViewById(R.id.password2_et);
        name_EditText = (EditText) findViewById(R.id.name_et);
        phone_EditText = (EditText) findViewById(R.id.phone_et);
        address_EditText = (EditText) findViewById(R.id.address_et);
        addressDetail_EditText = (EditText) findViewById(R.id.addressDetail_et);
        type_RadioGroup = (RadioGroup) findViewById(R.id.types_radioGroup);

        //get fireBase db reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //get fireBase Auth reference
        mAuth = FirebaseAuth.getInstance();
    }

    public void register_submit(View view) {
        //get inputs for validation and registration
        accountType_RadioButton = (RadioButton) findViewById(type_RadioGroup.getCheckedRadioButtonId());

        accountType = accountType_RadioButton.getText().toString();
        email = email_EditText.getText().toString();
        password = password_EditText.getText().toString();
        password2 = password2_EditText.getText().toString();
        name = name_EditText.getText().toString();
        phone = phone_EditText.getText().toString();
        addressDetail = addressDetail_EditText.getText().toString();
        address = address_EditText.getText().toString();

        //TODO change true to add validation function

        /* email validation */
        if (!InputValidation.isValidEmail(email)) {
            email_EditText.setError("Invalid email address");
            errorMessage();
            return;
        }

        /* password validation */
        if (InputValidation.isWeakPassword(password)) {
            password_EditText.setError("Password must contain at least 6 characters");
            errorMessage();
            return;
        }

        /* check whether two passowrds match */
        if (!InputValidation.isSamePassword(password, password2)) {
            password2_EditText.setError("Password does not match");
            errorMessage();
            return;
        }

        /* Name validation */
        if (!InputValidation.isValidName(name)) {
            name_EditText.setError("Enter a name");
            errorMessage();
            return;
        }

        /* Phone number validation */
        if (!InputValidation.isValidPhoneNumber(phone)) {
            phone_EditText.setError("Invalid phone number");
            errorMessage();
            return;
        }

        /* address validation */
        if(!InputValidation.isValidAddress(address)){
            address_EditText.setError("Enter an address");
            return;
        }

        /* address detail validation */
        if(!InputValidation.isValidAddress(addressDetail)){
            addressDetail = "";
        }

        if(!CheckConnection.isOnline(RegisterActivity.this)) {
            Toast.makeText(RegisterActivity.this, "No Network",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        register_account();
    }

    public void errorMessage() {
        Toast.makeText(RegisterActivity.this, R.string.register_failed,
                Toast.LENGTH_SHORT).show();
    }

    //registration function to authentication and database
    private void register_account() {
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            if(!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_weak_password),
                                            Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_invalid_email),
                                            Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_user_exists),
                                            Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.register_failed),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            //store to the database
                            FirebaseUser user = task.getResult().getUser();
                            String accountUID = user.getUid();
                            Log.d(TAG, "onComplete: uid=" + accountUID);

                            if (user != null) {
                                Customer newCustomer = new Customer(accountUID,
                                        accountType,
                                        email,
                                        password,
                                        name,
                                        phone,
                                        address,
                                        addressDetail);

                                mDatabaseRef.child("users").child(accountUID).setValue(newCustomer);

                                Toast.makeText(RegisterActivity.this, R.string.register_succeed,
                                        Toast.LENGTH_SHORT).show();

                                Intent registerSucceed = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(registerSucceed);
                                finish();
                            }
                        }

                        hideProgressDialog();
                    }
                });
    }
}
