package tipcalcapp.com.tipcalculator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    EditText bill_amount;
    EditText tax_percent;
    EditText tax_amount;
    EditText tip_percent;
    EditText tip_amount;
    TextView total_bill;
    SwitchCompat include_tax;
    Button split_minus;
    Button split_plus;
    EditText split_amount;
    TextView split_bill;
    LinearLayout empty_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load saved settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String default_tip = sharedPref.getString(SettingsActivity.default_tip, "15");
        boolean default_tip_tax = sharedPref.getBoolean(SettingsActivity.default_tip_tax, false);
        String default_tax_percent = sharedPref.getString(SettingsActivity.default_tax_percent, "8.5");

        // Set findViews
        bill_amount = findViewById(R.id.bill_amount);
        tax_percent = findViewById(R.id.tax_percent);
        tax_amount = findViewById(R.id.tax_amount);
        tip_percent = findViewById(R.id.tip_percent);
        tip_amount = findViewById(R.id.tip_amount);
        total_bill = findViewById(R.id.total_bill);
        include_tax = findViewById(R.id.tip_include_switch);
        split_minus = findViewById(R.id.split_minus);
        split_plus = findViewById(R.id.split_plus);
        split_amount = findViewById(R.id.split_amount);
        split_bill = findViewById(R.id.split_bill);
        empty_box = findViewById(R.id.empty_box);

        // Set default values
        tax_percent.setText(default_tax_percent);
        tip_percent.setText(default_tip);
        include_tax.setChecked(default_tip_tax);

        // Set focus listeners
        bill_amount.setOnFocusChangeListener(this);
        tax_percent.setOnFocusChangeListener(this);
        tax_amount.setOnFocusChangeListener(this);
        tip_percent.setOnFocusChangeListener(this);
        tip_amount.setOnFocusChangeListener(this);
        split_amount.setOnFocusChangeListener(this);

        // Set switch listener
        include_tax.setOnCheckedChangeListener(this);

        // Set button listeners
        split_minus.setOnClickListener(this);
        split_plus.setOnClickListener(this);

        // Focus total_bill so doesn't show keyboard at launch
        bill_amount.clearFocus();
        empty_box.requestFocus();

        // Text field press enter listeners
        bill_amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                    updateBillAmount();
                    bill_amount.clearFocus();
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        tax_percent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                    updateBillAmount();
                    tax_percent.clearFocus();
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        tax_amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                    updateTaxAmount();
                    tax_amount.clearFocus();
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        tip_percent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                    updateBillAmount();
                    tip_percent.clearFocus();
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        tip_amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                    updateTipAmount();
                    tip_amount.clearFocus();
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        split_amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                    updateSplitAmount();
                    split_amount.clearFocus();
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

    }

    // Update field functions
    @SuppressLint("SetTextI18n")
    public void updateBillAmount() {

        if (bill_amount.getText().toString().trim().length() > 0) {

            float tax = Float.parseFloat(bill_amount.getText().toString()) - (Float.parseFloat(bill_amount.getText().toString()) / (1 + Float.parseFloat(tax_percent.getText().toString()) * 0.01f));
            BigDecimal bdtax = new BigDecimal(Float.toString(tax));
            bdtax = bdtax.setScale(2, RoundingMode.HALF_UP);
            tax_amount.setText(bdtax.toString());

            // Include tax or not
            if (include_tax.isChecked()) {

                float tip = Float.parseFloat(bill_amount.getText().toString()) * Float.parseFloat(tip_percent.getText().toString()) * 0.01f;
                BigDecimal bdtip = new BigDecimal(Float.toString(tip));
                bdtip = bdtip.setScale(2, RoundingMode.HALF_UP);
                tip_amount.setText(bdtip.toString());

            } else {

                float tip = (Float.parseFloat(bill_amount.getText().toString()) - Float.parseFloat(tax_amount.getText().toString())) * Float.parseFloat(tip_percent.getText().toString()) * 0.01f;
                BigDecimal bdtip = new BigDecimal(Float.toString(tip));
                bdtip = bdtip.setScale(2, RoundingMode.HALF_UP);
                tip_amount.setText(bdtip.toString());

            }

            // Add tip
            float bill = (Float.parseFloat(bill_amount.getText().toString()) + Float.parseFloat(tip_amount.getText().toString()));
            BigDecimal bdbill = new BigDecimal(Float.toString(bill));
            bdbill = bdbill.setScale(2, RoundingMode.HALF_UP);
            total_bill.setText(bdbill.toString());

            updateSplitAmount();

        }
    }

    @SuppressLint("SetTextI18n")
    public void updateTaxAmount() {

        if (tax_amount.getText().length() > 0) {

            if (bill_amount.getText().length() > 0) {

                if (Float.parseFloat(bill_amount.getText().toString()) > Float.parseFloat(tax_amount.getText().toString())) {

                    // Float tax = Float.valueOf(bill_amount.getText().toString()) / (1 + Float.valueOf(tax_percent.getText().toString()) * 0.01f);
                    float tax = ((Float.parseFloat(bill_amount.getText().toString()) - (Float.parseFloat(bill_amount.getText().toString()) - Float.parseFloat(tax_amount.getText().toString()))) / (Float.parseFloat(bill_amount.getText().toString()) - Float.valueOf(tax_amount.getText().toString()))) * 100f;
                    BigDecimal bd = new BigDecimal(Float.toString(tax));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    tax_percent.setText(bd.toString());
                } else {

                    Toast.makeText(this, "Tax amount cannot be more than the total bill!",
                            Toast.LENGTH_LONG).show();

                }

            } else {

                float newbill = (Float.parseFloat(tax_amount.getText().toString()) / (Float.parseFloat(tax_percent.getText().toString()) / 100f)) + Float.parseFloat(tax_amount.getText().toString());
                BigDecimal bd = new BigDecimal(Float.toString(newbill));
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                bill_amount.setText(bd.toString());

            }

            updateBillAmount();

        }
    }

    @SuppressLint("SetTextI18n")
    public void updateTipAmount() {

        if (tip_amount.getText().length() > 0) {

            if (bill_amount.getText().length() > 0) {

                if (include_tax.isChecked()) {

                    float tip = Float.parseFloat(tip_amount.getText().toString()) / Float.parseFloat(bill_amount.getText().toString()) * 100f;
                    BigDecimal bd = new BigDecimal(Float.toString(tip));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    tip_percent.setText(bd.toString());

                } else {

                    float newbill = Float.parseFloat(bill_amount.getText().toString()) - Float.parseFloat(tax_amount.getText().toString());
                    float tip = Float.parseFloat(tip_amount.getText().toString()) / newbill * 100f;
                    BigDecimal bd = new BigDecimal(Float.toString(tip));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    tip_percent.setText(bd.toString());

                }

            } else {

                if (include_tax.isChecked()) {

                    float newbill = (Float.parseFloat(tip_amount.getText().toString()) / (Float.parseFloat(tip_percent.getText().toString()) / 100f));
                    BigDecimal bd = new BigDecimal(Float.toString(newbill));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    bill_amount.setText(bd.toString());

                } else {

                    float newbill = (Float.parseFloat(tip_amount.getText().toString()) * (1f + Float.parseFloat(tax_percent.getText().toString()) / 100f)) / (Float.parseFloat(tip_percent.getText().toString()) / 100f);
                    BigDecimal bd = new BigDecimal(Float.toString(newbill));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    bill_amount.setText(bd.toString());

                }

            }

            updateBillAmount();
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateSplitAmount() {

        if (bill_amount.getText().toString().trim().length() > 0) {

            float split = Float.parseFloat(total_bill.getText().toString()) / Float.parseFloat(split_amount.getText().toString());
            BigDecimal bd = new BigDecimal(Float.toString(split));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            split_bill.setText(bd.toString());

        }
    }

    // Hide keyboard
    public void hideKeyboard(View view) {

        empty_box.requestFocus();
        empty_box.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (v == bill_amount || v == tax_percent || v == tip_percent) {

                hideKeyboard(v);
                updateBillAmount();

            } else if (v == tax_amount) {

                hideKeyboard(v);
                updateTaxAmount();

            } else if (v == tip_amount) {

                hideKeyboard(v);
                updateTipAmount();

            } else {

                hideKeyboard(v);
                updateSplitAmount();

            }

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        updateBillAmount();

    }

    // Split button press
    @SuppressLint("SetTextI18n")
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.split_minus) {
            if (Integer.parseInt(split_amount.getText().toString()) > 1) {

                split_amount.setText(Integer.toString((Integer.parseInt(split_amount.getText().toString()) - 1)));
                updateSplitAmount();

            } else {

                split_amount.setText("1");

            }
        } else if (id == R.id.split_plus) {
            split_amount.setText(Integer.toString((Integer.parseInt(split_amount.getText().toString()) + 1)));
            updateSplitAmount();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.reset) {
            bill_amount.setText("");
            tax_amount.setText("");
            tip_amount.setText("");
            split_amount.setText("1");
            total_bill.setText("");
            split_bill.setText("");

            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String default_tip = sharedPref.getString(SettingsActivity.default_tip, "15");
            boolean default_tip_tax = sharedPref.getBoolean(SettingsActivity.default_tip_tax, false);
            String default_tax_percent = sharedPref.getString(SettingsActivity.default_tax_percent, "8.5");
            tax_percent.setText(default_tax_percent);
            tip_percent.setText(default_tip);
            include_tax.setChecked(default_tip_tax);
        }


        return super.onOptionsItemSelected(item);
    }

}
