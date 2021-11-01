package com.vibe_viroma.cropnutrient.content.champs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.content.MainActivity;
import com.vibe_viroma.cropnutrient.login.login;
import com.vibe_viroma.cropnutrient.login.signin;
import com.vibe_viroma.cropnutrient.objets.Ferme;
import com.vibe_viroma.cropnutrient.objets.User;
import com.vibe_viroma.cropnutrient.tools.Cte;
import com.vibe_viroma.cropnutrient.tools.PrefManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddChamp extends AppCompatActivity {

    private Spinner tv_prod;
    private EditText ed_champ,ed_superficie;
    private Spinner ed_zone, ed_region, unite;
    private TextView tv_back, tv_save;
    private String[] str_zones = {"Zone Nord", "Zone centre", "Zone sud"};
    private String[] unites = {"Unité", "ha", "m2"};
    private String[][] str_region = {{"Gogounou", "N'dali", "Banicoara"}, {"Djougou", "Savè"}, {"Cana", "Djidja"}};
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference consRef= db.collection("Conseillers");
    private String user_id, selected_zone, selected_region;
    private List<String> prods_list= new ArrayList<>();
    private SweetAlertDialog loading;
    private String prodName="", str_unit="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_champ);
        Objects.requireNonNull(getSupportActionBar()).show();
        getSupportActionBar().setTitle("Nouveau champ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user_id= FirebaseAuth.getInstance().getUid();
        initView();
        initClick();
        setupSpinners();
        setupAutoCompleteTextView();
    }

    private void initView(){
        tv_back=(TextView)findViewById(R.id.back);
        tv_save=(TextView)findViewById(R.id.save);
        ed_champ=(EditText)findViewById(R.id.champ);
        ed_superficie=(EditText)findViewById(R.id.superficie);
        tv_prod=(Spinner)findViewById(R.id.prod);

        ed_zone=(Spinner)findViewById(R.id.zone);
        unite=(Spinner)findViewById(R.id.unite);
        ed_region=(Spinner)findViewById(R.id.region);
        loading= new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    }

    private void initClick(){
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_prod= prodName.trim();//tv_prod.getText().toString().trim();
                String champ= ed_champ.getText().toString().trim();
                String superf= ed_superficie.getText().toString().trim();
                if(str_prod.isEmpty() || champ.isEmpty() || superf.isEmpty() || selected_region.isEmpty() || selected_zone.isEmpty()){
                    new SweetAlertDialog(AddChamp.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Erreur")
                            .showContentText(true)
                            .setContentText("Tous les champs sont requis")
                            .showCancelButton(false)
                            .show();
                } else if( str_unit.isEmpty() ){
                    new SweetAlertDialog(AddChamp.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Erreur")
                            .showContentText(true)
                            .setContentText("Vous n'avez pas choisi l'unité de mesure de la superficie..")
                            .showCancelButton(false)
                            .show();
                }else{
                    saveNewChamp(str_prod, champ, superf, selected_zone, selected_region, str_unit);
                }
            }
        });
    }

    private void saveNewChamp(String str_prod, final String champ, String superf, String selected_zone, String selected_region, String unite) {
        loading.setContentText("Création du nouveau champ en cours...");
        loading.show();
        loading.setCanceledOnTouchOutside(false);


        final Ferme f= new Ferme(str_prod, superf, champ, selected_zone, selected_region, unite);
        CollectionReference df=consRef.document(user_id).collection("Fermes");
        final String ferme_id= df.document().getId();
        df.document(ferme_id).set(f).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loading.dismissWithAnimation();
                if(task.isSuccessful()){
                    loading.dismissWithAnimation();
                    final SweetAlertDialog sw= new SweetAlertDialog(AddChamp.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Champ "+champ+ " créé !")
                            .showContentText(true)
                            .setTitleText("Succès")
                            .setConfirmText("Continuer")
                            .setCancelText("Nouveau")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    startActivity(new Intent(AddChamp.this, DetailsFerme.class)
                                            .putExtra("ferme_id",ferme_id )
                                            .putExtra("champ_name", f.getFerme_name()));
                                    finish();
                                }
                            }) ;
                    sw.setCanceledOnTouchOutside(false);
                    sw.show();
                    sw.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            ed_champ.setText("");
                            ed_superficie.setText("");
                            sw.dismiss();
                        }
                    });
                    sw.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ed_champ.setText("");
                            ed_superficie.setText("");
                            sw.dismiss();
                        }
                    });
                }  else{
                    Toast.makeText(AddChamp.this, "Erreur: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSpinners(){
        ArrayAdapter<String> aa= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, str_zones);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ed_zone.setAdapter(aa);

        ed_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                ArrayAdapter<String> aa2= new ArrayAdapter<>(AddChamp.this, android.R.layout.simple_spinner_item, str_region[position]);
                aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ed_region.setAdapter(aa2);
                selected_zone= str_zones[position];
                ed_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                        selected_region= str_region[position][i];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selected_region="";
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aa= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unites);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        unite.setAdapter(aa);
        unite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    str_unit= unites[position];
                }else{
                    str_unit="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                str_unit="";
            }
        });
    }

    private void setupAutoCompleteTextView(){
        consRef.document(user_id).collection("Producteurs").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String[] prods = new String[queryDocumentSnapshots.size()+1];
                int i=0;
                prods_list= new ArrayList<>();
                for (DocumentSnapshot d:queryDocumentSnapshots.getDocuments()) {
                    prods[i]=d.getString("name");
                    prods_list.add(prods[i]);
                    i++;
                }
                prods[i]="Autre";
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddChamp.this,
                        android.R.layout.simple_spinner_item, prods);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tv_prod.setAdapter(adapter);
                tv_prod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position>prods_list.size()-1){
                            View vw = LayoutInflater.from(AddChamp.this).inflate(R.layout.item_id_photo, null, false);
                            final AlertDialog alertDialog = new AlertDialog.Builder(AddChamp.this)
                                    .setView(vw)
                                    .show();
                            alertDialog.setCanceledOnTouchOutside(false);
                            final EditText idPhoto = (EditText) vw.findViewById(R.id.idPhoto);
                            TextView sauver = (TextView) vw.findViewById(R.id.save);
                            sauver.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String id = idPhoto.getText().toString().trim() + "-";
                                    alertDialog.dismiss();

                                    Map<String, Object> map= new HashMap<String, Object>();
                                    map.put("name", id);
                                    map.put("time", new Date().getTime());
                                    loading.setContentText("Ajout du producteur en cours...");
                                    loading.show();
                                    consRef.document(user_id).collection("Producteurs").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            loading.dismiss();
                                            setupAutoCompleteTextView();
                                        }
                                    });
                                }
                            });
                            alertDialog.show();
                        }else{
                            prodName= prods_list.get(position);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        prodName="";
                    }
                });
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }
}
