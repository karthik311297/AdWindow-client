package com.example.adwindow.adwindow_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adwindow.adwindow_client.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        Button signUpButton = findViewById(R.id.register);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText email = findViewById(R.id.newemail);
                final EditText phoneNumber = findViewById(R.id.PhoneNumber);
                final EditText companyName = findViewById(R.id.companyName);
                EditText password = findViewById(R.id.newpassword);
                EditText confirmPassword = findViewById(R.id.newconfirmpassword);
                if(email.getText().toString().equals("") || phoneNumber.getText().toString().equals("") || companyName.getText().toString().equals("")
                        || password.getText().toString().equals("") || confirmPassword.getText().toString().equals(""))
                {
                    Toast.makeText(SignUpActivity.this, "Fields Cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else if (!password.getText().toString().equals(confirmPassword.getText().toString()))
                {
                    Toast.makeText(SignUpActivity.this, "Confirm correct Password",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setTitle("Please Wait Registering..");
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        String uid = firebaseAuth.getCurrentUser().getUid();
                                        final User user = new User(uid, email.getText().toString(), companyName.getText().toString(), phoneNumber.getText().toString());
                                        DatabaseReference userRef = databaseReference.child("Users").child(uid);
                                        userRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SignUpActivity.this, "Registration Successful",Toast.LENGTH_SHORT).show();
                                                firebaseAuth.signOut();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                if(firebaseAuth.getCurrentUser()!=null) {
                                                    firebaseAuth.getCurrentUser().delete();
                                                }
                                                Toast.makeText(SignUpActivity.this, "Registration Failed",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Registration Failed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
