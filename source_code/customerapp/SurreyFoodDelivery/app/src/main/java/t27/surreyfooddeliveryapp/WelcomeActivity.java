package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        login_redirect();
    }

    public void login_click(View view) {
        Intent fromWelcomeToLogin = new Intent(this,LoginActivity.class);
        startActivity(fromWelcomeToLogin);
    }

    public void register_click(View view) {
        Intent fromWelcomeToRegister = new Intent(this,RegisterActivity.class);
        startActivity(fromWelcomeToRegister);
    }

    public void guest_click(View view) {
        Intent fromWelcomeToHome_guest = new Intent(this,HomeActivity.class);
        startActivity(fromWelcomeToHome_guest);
    }

    private void login_redirect() {
        SharedPreferences userInfo_Prefs =  getApplicationContext().getSharedPreferences(
                getString(R.string.User_info), Context.MODE_PRIVATE);
        if(userInfo_Prefs.getString("userUID",null)!=null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
