package com.vibe_viroma.cropnutrient.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.content.MainActivity;
import com.vibe_viroma.cropnutrient.objets.User;
import com.vibe_viroma.cropnutrient.tools.Cte;
import com.vibe_viroma.cropnutrient.tools.PrefManager;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class login extends AppCompatActivity {

    private TextInputEditText email, password;
    private TextView tv_login, tv_signin;
    private SweetAlertDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initClick();
    }

    private void initViews(){
        email=(TextInputEditText)findViewById(R.id.regist_email);
        password=(TextInputEditText)findViewById(R.id.regis_code);
        tv_login=(TextView)findViewById(R.id.login);
        tv_signin=(TextView)findViewById(R.id.signin);
        loading= new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

    }

    private void initClick(){
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _email= email.getText().toString().trim();
                String _password= password.getText().toString().trim();
                if(_email.isEmpty() || _password.isEmpty()){
                     new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE).setCancelText("OK")
                             .setTitleText("Erreur")
                             .showContentText(true)
                             .setContentText("Tous les champs sont requis")
                             .showCancelButton(false)
                             .show();
                }else{
                    make_login(_email, _password);
                }
            }
        });

        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, signin.class));
                finish();
            }
        });
    }

    private void make_login(String email, String password) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        loading.setContentText("Veuillez patienter...");
        loading.show();
        loading.setCanceledOnTouchOutside(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final String user_id= mAuth.getCurrentUser().getUid();
                    FirebaseFirestore.getInstance().collection("Users").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            loading.dismissWithAnimation();
                            User user= documentSnapshot.toObject(User.class);

                            PrefManager.setUserId(user_id, login.this);
                            PrefManager.setUserInfo(Cte.object2json(user), login.this);
                            startActivity(new Intent(login.this, MainActivity.class));
                            finish();
                        }
                    });
                }else{
                    loading.dismissWithAnimation();
                }
            }
        });
    }
}
