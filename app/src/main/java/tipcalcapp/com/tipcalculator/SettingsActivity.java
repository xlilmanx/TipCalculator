package tipcalcapp.com.tipcalculator;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }

    public static final String default_tip = "default_tip";
    public static final String default_tip_tax = "default_tip_tax";
    public static final String default_tax_percent = "default_tax_percent";


}

