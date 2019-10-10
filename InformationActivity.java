package com.hadar.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView genderTextView;
    private EditText streetEditText;
    private EditText countryEditText;
    private EditText postcodeEditText;
    private Button updateButton;
    public static final String PERSON_NAME = "person_name";
    public static final String NEW_STREET = "new_street";
    public static final String NEW_COUNTRY = "new_country";
    public static final String NEW_POSTCODE = "new_postcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        nameTextView = findViewById(R.id.nameTextView);
        genderTextView = findViewById(R.id.genderTextView);
        streetEditText = findViewById(R.id.streetEditText);
        countryEditText = findViewById(R.id.countryEditText);
        postcodeEditText = findViewById(R.id.postcodeEditText);
        updateButton = findViewById(R.id.update_button);

        initViews();
        update();
    }

    private void initViews() {
        nameTextView.setText(getName() != null ? getName() : "");
        genderTextView.setText(getGender() != null ? getGender() : "");
        streetEditText.setText(getStreet() != null ? getStreet() : "");
        countryEditText.setText(getCountry() != null ? getCountry() : "");
        postcodeEditText.setText(getPostcode() != null ? getPostcode() : "");
    }

    private String getName() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForName);
        }
        return null;
    }

    private String getGender() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForGender);
        }
        return null;
    }

    private String getStreet() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForStreet);
        }
        return null;
    }

    private String getCountry() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForCountry);
        }
        return null;
    }

    private String getPostcode() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForPostcode);
        }
        return null;
    }

    private void update() {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTextView.getText().toString();
                String gender = genderTextView.getText().toString();
                String street = streetEditText.getText().toString();
                String country = countryEditText.getText().toString();
                String postcode = postcodeEditText.getText().toString();

                MainActivity.updateSQL(name, gender, street, country, postcode);

                Intent i = new Intent();
                i.putExtra(PERSON_NAME, name);
                i.putExtra(NEW_STREET, street);
                i.putExtra(NEW_COUNTRY, country);
                i.putExtra(NEW_POSTCODE, postcode);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}
