package t27.surreyfooddeliverycompany;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import objectstodb.Account;

public class ProfileActivity extends AppCompatActivity {
    private Intent intent;
    private SharedPreferences userPreference;
    private TextView name;
    private TextView address;
    private TextView email;
    private TextView phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.edit_address);
        email = (TextView) findViewById(R.id.email);
        phoneNumber = (TextView) findViewById(R.id.phone);
        userPreference = getApplicationContext().getSharedPreferences(
                getString(R.string.user_preference), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreference.getString("userObject", null);
        if (json != null) {
            Account account = gson.fromJson(json, Account.class);
            if(account!=null) {
                String accountName = account.getName();
                String accountAddress = account.getAddress();
                String accountEmail = account.getEmail();
                String accountPhone = account.getNumber();

                name.setText(accountName);
                address.setText(accountAddress);
                email.setText(accountEmail);
                phoneNumber.setText(accountPhone);
            }

        }
    }

    public void signOut (View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getSharedPreferences(getString(
                R.string.user_preference), Context.MODE_PRIVATE);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        final String accountUID = user.getUid();

        String type = preferences.getString("loginType","");
        Log.d("profileAct", "signOut: type" + type);;


        if(type.equals("driver")) {
            database.child("driver").child(accountUID).child("status").setValue("offline");
        }

        SharedPreferences.Editor editor = preferences.edit();

        mAuth.signOut();
        editor.clear();
        editor.apply();

        intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
