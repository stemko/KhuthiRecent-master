package ru.solandme.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private String[] arraySpinner;
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;

    private Button registerBtn;
    private TextView loginLink;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    private ProgressDialog progress;
    private String selected;

    Spinner s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*
        Spinner
         */
        this.arraySpinner = new String[]{
                "Customer", "Company"
        };
        s = (Spinner) findViewById(R.id.Spinner01);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);


        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected= (String) s.getAdapter().getItem(position);

//                Toast.makeText(RegisterActivity.this,"Selected " + selected,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        progress = new ProgressDialog(this);
        loginLink = (TextView)findViewById(R.id.linkLoginSignUp);

        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
         final String spinner =arraySpinner.toString().trim();
        final String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();


        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)&&(!TextUtils.isEmpty(spinner))) {

            progress.setMessage("Registering ...");
            progress.show();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String userId = auth.getCurrentUser().getUid();
                        DatabaseReference currentUserDB = databaseRef.child(userId);

                        currentUserDB.child("name").setValue(name);
                        currentUserDB.child("image").setValue("default");
                        currentUserDB.child("type").setValue(selected);
                        progress.dismiss();
                        if (selected.equalsIgnoreCase("company")) {
                            Intent mainIntent = new Intent(RegisterActivity.this, PostActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                        } else {
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                        }


                    }

                }
            });
        }
    }
}
