package com.vibe_viroma.cropnutrient.content.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.adapters.reponsePoJo;
import com.vibe_viroma.cropnutrient.adapters.responseAdapter;
import com.vibe_viroma.cropnutrient.content.champs.DetailsFerme;
import com.vibe_viroma.cropnutrient.objets.Photo;
import com.vibe_viroma.cropnutrient.objets.Problem;
import com.vibe_viroma.cropnutrient.objets.Response;
import com.vibe_viroma.cropnutrient.objets.User;
import com.vibe_viroma.cropnutrient.tools.Cte;
import com.vibe_viroma.cropnutrient.tools.PrefManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddQuest extends AppCompatActivity {

    private TextInputEditText ed_title, ed_desc;
    private TextInputLayout descLayout;
    private TextView tv_delete, tv_play, tv_desc;
    private ImageButton record_btn;
    private RecordButton rec_btn;
    private RecordView rec_view;
    private TextView tv_see_response;
    private RecyclerView recyclerView;
    private Button validate;
    private EditText ed_response;
    private FloatingActionButton btn_send;
    private ImageView img, add;
    private Chronometer recordTime;
    private LinearLayout bottomResponse;
    
    private boolean isSubmitRecording=false;
    private boolean isSubmitPlaying=false;
    private String recordFile="", str_img_path="";

    private MediaRecorder submitMediaRecorder;
    private SweetAlertDialog loading;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference problemRef= db.collection("Communities");

    private Problem problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quest);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Préoccupations");

        initViews();

        check_edit_or_view();

        initClick();
    }

    private void initClick() {
        tv_see_response.setText("Masquer les réponses");

        tv_see_response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerView.getVisibility()==View.VISIBLE){
                    recyclerView.setVisibility(View.GONE);
                    tv_see_response.setText("Masquer les réponses");
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_see_response.setText("Afficher les réponses");
                }
            }
        });

        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(problem==null) {
                    if (isSubmitRecording) {
                        //Stop Recording
                        stopRecording();

                        // Change button image and set Recording state to false
                        record_btn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped, null));
                        isSubmitRecording = false;
                    } else {
                        //Check permission to record audio
                        if (!checkPermissionRecording()) {
                            //Start Recording
                            startRecording();

                            // Change button image and set Recording state to false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                record_btn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
                            }
                            isSubmitRecording = true;
                        } else
                            requestPermissionRecording();
                    }
                }else{
                    setupUpForPlaying();
                }
            }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_play.setVisibility(View.GONE);
                tv_delete.setVisibility(View.GONE);
                recordFile=null;
                recordTime.setText("00:00");
            }
        });

        tv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupUpForPlaying();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options= Options.init()
                        .setRequestCode(100)
                        .setCount(1)
                        .setFrontfacing(false)
                        .setPreSelectedUrls(new ArrayList<String>())
                        .setExcludeVideos(true)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                        .setPath("Cotton Nutrient/images");
                Pix.start(AddQuest.this, options);
            }
        });

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_title=ed_title.getText().toString().trim();
                String str_desc=ed_desc.getText().toString().trim();
                if(str_title.isEmpty()) {
                    new SweetAlertDialog(AddQuest.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Erreur")
                            .setContentText("Vous n'avez pas renseigné le titre de votre préoccupation")
                            .showCancelButton(false)
                            .show();
                }else if(str_desc.isEmpty()&&str_img_path.trim().isEmpty()&&recordFile.isEmpty()){

                    new SweetAlertDialog(AddQuest.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Erreur")
                            .setContentText("Vous devez donner au moins un élément pour la compréhension de votre problème..!")
                            .showCancelButton(false)
                            .show();
                }else
                    submitProblem(str_img_path, recordFile, str_title, str_desc);
            }
        });
        ed_response.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    btn_send.setVisibility(View.GONE);
                }else{
                    btn_send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse(ed_response.getText().toString());
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(str_img_path.isEmpty()){
                    Toast.makeText(AddQuest.this, "Aucun média trouvé..!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(AddQuest.this, ZoomPic.class);
                intent.putExtra("PATH", str_img_path);
                startActivity(intent);
            }
        });
    }

    private void sendResponse(String response) {
        CollectionReference reponseRef= problemRef.document(problem.getKey()).collection("Reponses");
        Response resp= new Response(response, problem.getKey(), "", FirebaseAuth.getInstance().getCurrentUser().getUid(), new Date().getTime()+"", "");
        ed_response.setText("");
        reponseRef.add(resp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        });
    }

    private void loadResponse(){
        CollectionReference reponseRef= problemRef.document(problem.getKey()).collection("Reponses");
        final CollectionReference userRef= db.collection("Users");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        reponseRef.orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                final List<reponsePoJo> c = new ArrayList<>();
                final responseAdapter adapter= new responseAdapter( AddQuest.this, c);
                recyclerView.setAdapter(adapter);
                assert queryDocumentSnapshots != null;
                for (final DocumentSnapshot d:queryDocumentSnapshots.getDocuments()) {
                    final Response pb= d.toObject(Response.class);
                    Log.d(Cte.TAG_, pb.getFrom());
                    if(pb.getFrom()=="")
                        continue;
                    userRef.document(pb.getFrom()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            User user= task.getResult().toObject(User.class);
                            reponsePoJo poJo= new reponsePoJo(pb, user);
                            poJo.setKey(d.getId());
                            c.add(poJo);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.scrollToPosition(0);
            }
        });
    }

    private void setupUpForPlaying() {

        if(problem !=null && problem.getLink_audio().isEmpty()){
            Toast.makeText(AddQuest.this, "Ce problème n'a pas d'audio !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(recordFile.isEmpty()){
            Toast.makeText(AddQuest.this, "Veuillez patienter pendant le téléchargement...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isSubmitPlaying)
            stopAudio();
        else
            playAudio(new File(recordFile));
    }

    private void submitProblem(final String str_img_path, final String recordFile, final String str_title, final String str_desc) {
        loading.setContentText("Veuillez patienter...");
        loading.show();
        if (!recordFile.trim().isEmpty()) {
            Upload upload = new Upload(recordFile, "amr");
            upload.setListener(new Upload.uploadingListener() {
                @Override
                public void onProgressListener(int percentage) {
                    loading.setContentText("Téléchargement de l'audio: " + percentage + "% effectué(s)");
                }

                @Override
                public void onSuccess(final String link) {
                    if (!str_img_path.trim().isEmpty()) {
                        Upload upload1 = new Upload(str_img_path, "jpg");
                        upload1.setListener(new Upload.uploadingListener() {
                            @Override
                            public void onProgressListener(int percentage) {
                                loading.setContentText("Téléchargement de l'image: " + percentage + "% effectué(s)");
                            }

                            @Override
                            public void onSuccess(String link2) {
                                saveProblem(str_title, str_desc, link, link2);
                            }
                        });
                        upload1.makeUplaod();
                    } else {
                        saveProblem(str_title, str_desc, link, str_img_path);
                    }
                }
            });
            upload.makeUplaod();
        } else if (!str_img_path.trim().isEmpty()) {
            Upload upload1 = new Upload(str_img_path, "jpg");
            upload1.setListener(new Upload.uploadingListener() {
                @Override
                public void onProgressListener(int percentage) {
                    loading.setContentText("Téléchargement de l'image: " + percentage + "% effectué(s)");
                }

                @Override
                public void onSuccess(String link2) {
                    saveProblem(str_title, str_desc, recordFile, link2);
                }
            });
            upload1.makeUplaod();
        } else {
            saveProblem(str_title, str_desc, recordFile, str_img_path);
        }

    }

    private void saveProblem(String str_title, String str_desc, String link, String str_img_path) {
        loading.setContentText("Publication du problème en cours...");
        Problem problem= new Problem(str_title, str_desc, str_img_path , link, FirebaseAuth.getInstance().getCurrentUser().getUid(), "", new Date().getTime()+"");

        problemRef.add(problem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                loading.dismiss();
                if(task.isSuccessful()){
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(AddQuest.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Votre problème a bien été posé à la communauté Cotton Nutrient !\nBientôt, une solution sera à votre portée.")
                            .showContentText(true)
                            .setTitleText("Publication réussie !")
                            .setConfirmText("Retour")
                            .showCancelButton(false)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                }
                            });
                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            onBackPressed();
                        }
                    });
                }else{
                    Log.d(Cte.TAG_, task.getException().getMessage()+"");
                }
            }
        });
    }


    private void stopRecording() {
        //Stop Timer, very obvious
        recordTime.stop();

        //Change text on page to file saved
        tv_play.setVisibility(View.VISIBLE);
        tv_delete.setVisibility(View.VISIBLE);

        //Stop media recorder and set it to null for further use to record new audio
        submitMediaRecorder.stop();
        submitMediaRecorder.release();
        submitMediaRecorder = null;
    }
    private void startRecording() {
        //Start timer from 0
        recordTime.setBase(SystemClock.elapsedRealtime());
        recordTime.start();

        //Get app external directory path
        String recordPath = getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.FRENCH);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".amr";

        recordFile= recordPath + "/" + recordFile;

        //Setup Media Recorder for recording
        submitMediaRecorder = new MediaRecorder();
        submitMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        submitMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        submitMediaRecorder.setOutputFile(recordFile);
        submitMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            submitMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        submitMediaRecorder.start();
    }
    private boolean checkPermissionRecording() {
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return record_audio_result == PackageManager.PERMISSION_DENIED;
    }
    private void requestPermissionRecording() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO
        }, 252);
    }
    public void playAudio(File fileToPlay) {

        MediaPlayer submitMediaPlayer = new MediaPlayer();

        try {
            submitMediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            submitMediaPlayer.prepare();
            submitMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv_play.setText("Arrêter la lecture");

        //Play the audio
        isSubmitPlaying = true;

        submitMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        recordTime.setBase(SystemClock.elapsedRealtime());
        recordTime.start();
        if(problem !=null){
            record_btn.setImageDrawable(getResources().getDrawable(R.drawable.list_pause_btn));
        }
    }
    private void stopAudio() {
        //Stop The Audio
        tv_play.setText("Lire");
        recordTime.stop();
        isSubmitPlaying = false;
        if(problem !=null){
            record_btn.setImageDrawable(getResources().getDrawable(R.drawable.list_play_btn));
        }
    }



    private void check_edit_or_view()  {
        if(getIntent()==null || getIntent().getExtras()==null){
            bottomResponse.setVisibility(View.GONE);
        }else{
            User user = (User) Cte.string2class(getIntent().getExtras().get("USER").toString(), User.class);
            problem=(Problem) Cte.string2class(getIntent().getExtras().get("PROBLEM").toString(), Problem.class);

            bottomResponse.setVisibility(View.VISIBLE);
            tv_delete.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
            descLayout.setVisibility(View.GONE);
            validate.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            ed_title.setEnabled(false);


            getSupportActionBar().setTitle("Préoccupation de "+ user.getFirstname());

            setElement();

        }
    }

    private void setElement(){
        tv_desc.setText(problem.getDesc());
        ed_title.setText(problem.getTitle());
        record_btn.setImageDrawable(getResources().getDrawable(R.drawable.list_play_btn));
        recordFile= PrefManager.getLinkAudio(this, problem.getKey());

        if(! new File(recordFile).exists()){
            Download download= new Download(problem.getKey(), problem.getLink_audio(), "amr", this);
            download.setListener(new Download.downloadingListener() {
                @Override
                public void onProgressListener(int percentage) {

                }

                @Override
                public void onSuccess(String link) {
                    recordFile= link;
                }
            });
            try {
                download.makeUplaod();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        str_img_path= PrefManager.getLinkMedia(this, problem.getKey());
        if(! new File(str_img_path).exists()){
            Glide.with(this).load(problem.getLink_media()).into(img);
            Download download= new Download(problem.getKey(), problem.getLink_media(), "jpg", this);
            download.setListener(new Download.downloadingListener() {
                @Override
                public void onProgressListener(int percentage) {

                }

                @Override
                public void onSuccess(String link) {
                    str_img_path= link;
                }
            });
            try {
                download.makeUplaod();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            img.setImageURI(Uri.fromFile(new File(str_img_path)));
        }

        loadResponse();
        str_img_path= new File(str_img_path).exists()?str_img_path:problem.getLink_media();

    }
    

    private void initViews(){
        ed_title= findViewById(R.id.title);
        ed_desc= findViewById(R.id.desc);
        tv_desc= findViewById(R.id.tv_desc);
        tv_delete= findViewById(R.id.delete);
        tv_play= findViewById(R.id.play);
        record_btn= findViewById(R.id.record_btn);
        rec_btn= findViewById(R.id.record_button);
        rec_view= findViewById(R.id.recyclerView);
        ed_title= findViewById(R.id.title);
        ed_title= findViewById(R.id.title);
        tv_see_response= findViewById(R.id.tv_see_response);
        recyclerView= findViewById(R.id.recycle_response);
        validate= findViewById(R.id.validate);
        ed_response= findViewById(R.id.ed_message);
        img= findViewById(R.id.img);
        add= findViewById(R.id.add);
        recordTime= findViewById(R.id.record_timer);
        bottomResponse= findViewById(R.id.bottomResponse);
        btn_send= findViewById(R.id.btn_send);
        descLayout= findViewById(R.id.descLayout);

        loading= new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loading.setCanceledOnTouchOutside(false);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode == 100){
            ArrayList<String> returnValue=data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            str_img_path = returnValue.get(0);
            img.setImageURI(Uri.fromFile(new File(str_img_path)));
            add.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermUtil
                .REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Log.d(Cte.TAG_, "Permissions");
        }
    }
}
