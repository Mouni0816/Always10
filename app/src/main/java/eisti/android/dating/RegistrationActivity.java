package eisti.android.dating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private RadioGroup mRadiogroup;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        firebaseAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent=new Intent (RegistrationActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister=findViewById(R.id.register1);
        mEmail=findViewById(R.id.email);
        mPassword=findViewById(R.id.password);

        mRadiogroup=findViewById(R.id.radioGroup);
        mName=findViewById(R.id.name);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId=mRadiogroup.getCheckedRadioButtonId();

                final RadioButton radioButton=findViewById(selectId);

                if(radioButton.getText()==null){
                    return;
                }



                final String email=mEmail.getText().toString();
                final String password=mPassword.getText().toString();
                final String name=mName.getText().toString();
                final String sex1=radioButton.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            String s = "Sign up Failed" + task.getException();
                            Toast.makeText(RegistrationActivity.this,s, Toast.LENGTH_SHORT).show();
                        }else{
                            String userId=mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map userInfo= new HashMap<>();
                            userInfo.put("name",name);
                            userInfo.put("sex",sex1);
                            userInfo.put("profileImageUrl","default");

                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}