package com.vibe_viroma.cropnutrient.content.champs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.content.community.ZoomPic;
import com.vibe_viroma.cropnutrient.objets.Photo;
import com.vibe_viroma.cropnutrient.tools.Cte;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailsFerme extends AppCompatActivity {

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference consRef= db.collection("Conseillers");
    private CollectionReference photosRef;
    private String user_id="", ferme_id="", champName="";
    private Button btn_analysis;
    private GridView photoGrid;
    private TextView tv_size;
    private int max_photos=3;
    private List<Photo> paths= new ArrayList<>();
    private SweetAlertDialog loading;
    /**LAT LONG*/
    private double lat=0, lon=0;

    private String lieu="";

    /**GOOGLE MAP INIT*/
    private LocationManager locationManager;
    private long lenght=0;

    public DetailsFerme() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_ferme);
        initViews();

        user_id= FirebaseAuth.getInstance().getUid();
        ferme_id= getIntent().getExtras().getString("ferme_id");
        champName= getIntent().getExtras().getString("champ_name");

        getSupportActionBar().setTitle("Ferme "+champName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photosRef= consRef.document(user_id).collection("Fermes").document(ferme_id).collection("Photos");

        loadPhotos();
        getLocation();
    }

    private void initViews() {
        loading= new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        btn_analysis=(Button)findViewById(R.id.show_analysis);
        photoGrid=(GridView) findViewById(R.id.gridView);
        tv_size=(TextView) findViewById(R.id.size);
        locationManager= (LocationManager)getSystemService(LOCATION_SERVICE);

        Photo photo = new Photo();
        photo.setLien("add");
        paths.add(photo);
        setAdapter();
    }

    private void setAdapter(){
        //photoGrid.setLayoutManager(new GridLayoutManager((this, 3)))
        CustomAdapterGallery customAdapterGallery = new CustomAdapterGallery(this, paths);

        photoGrid.setAdapter(customAdapterGallery);
    }

    private void  loadPhotos(){
        photosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                lenght= queryDocumentSnapshots.size();
                paths= new ArrayList<>();

                tv_size.setText(lenght+" / "+max_photos+" photos");
                Log.d(Cte.TAG_+": Lenght", lenght+"");
                for (DocumentSnapshot d:queryDocumentSnapshots.getDocuments()
                     ) {
                    Photo photo= d.toObject(Photo.class);
                    photo.setKey(d.getId());
                    paths.add(photo);
                }
                Photo photo = new Photo();
                photo.setLien("add");
                paths.add(photo);

                if(lenght>= max_photos){
                    btn_analysis.setEnabled(true);
                    btn_analysis.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(DetailsFerme.this, Analyse.class));
                        }
                    });
                }
                setAdapter();
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

    public class CustomAdapterGallery extends BaseAdapter{
        private Context ctx;
        private List<Photo> imgs;

        CustomAdapterGallery(Context ctx, List<Photo> imgs ) {
            this.ctx = ctx;
            this.imgs = imgs;
        }

        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view= LayoutInflater.from(ctx).inflate(R.layout.item_image, parent, false);
            ImageView add= (ImageView)view.findViewById(R.id.add);
            final ImageView img= (ImageView)view.findViewById(R.id.img);
            Log.d(Cte.TAG_, imgs.get(position).getLien());
            img.setVisibility(View.VISIBLE);
            if(imgs.get(position).getLien().equals("add")){
                img.setVisibility(View.GONE);
            }else{
                add.setVisibility(View.GONE);
                img.setImageURI(Uri.fromFile(new File(imgs.get(position).getLien())));
            }

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence charSequence[]= new CharSequence[]{"Aperçu de la photo", "Supprimer la photo", "Info de la photo"};
                    AlertDialog sweetAlertDialog= new AlertDialog.Builder(ctx)
                            .setItems(charSequence, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        Intent intent = new Intent(ctx, ZoomPic.class);
                                        intent.putExtra("PATH", imgs.get(position).getLien());
                                        startActivity(intent);
                                    }else if(which==1){
                                        loading.setContentText("Suppression de la photo en cours...");
                                        loading.show();
                                        photosRef.document(imgs.get(position).getKey()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loading.dismiss();
                                                loadPhotos();
                                            }
                                        });
                                    }else{
                                        Date date= new Date(Long.parseLong(imgs.get(position).getTime()));
                                        String text="Photo prise:\n" +
                                                "- le "+(new SimpleDateFormat("dd MMM YYYY à hh:mm").format(date))+"\n" +
                                                "- à "+imgs.get(position).getLieu()+".\n" +
                                                "- Coordonnées: ("+imgs.get(position).getLat()+", "+imgs.get(position).getLon()+")";
                                        AlertDialog alertDialog= new AlertDialog.Builder(ctx)
                                                .setMessage(text)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .create();
                                        alertDialog.show();
                                    }
                                }
                            })
                            .create();
                    sweetAlertDialog.show();
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
                    Pix.start(DetailsFerme.this, options);
                }
            });
            return view;
        }

        private void addPhoto(){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode == 100){
            ArrayList<String> returnValue=data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            final String path = returnValue.get(0);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    loading.setContentText("Vérification de la photo en cours...\nEst-ce vraiment une feuille de coton??");
                    loading.show();
                }
            };
            Runnable runnable2 = new Runnable() {
                @Override
                public void run() {
                    loading.dismiss();
                    int rand = new Random().nextInt(50);
                    if (rand % 2 == 0) {
                        new SweetAlertDialog(DetailsFerme.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Erreur")
                                .showContentText(true)
                                .setContentText("Ceci n'est pas une feuille de coton! Reprenez svp")
                                .showCancelButton(false)
                                .show();
                    } else {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DetailsFerme.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Oui, il s'agit d'une feuille de coton !")
                                .showContentText(true)
                                .setTitleText("Image de coton")
                                .setConfirmText("Enregistrer")
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
                                View vw = LayoutInflater.from(DetailsFerme.this).inflate(R.layout.item_id_photo, null, false);
                                final AlertDialog alertDialog = new AlertDialog.Builder(DetailsFerme.this)
                                        .setView(vw)
                                        .show();
                                sweetAlertDialog.dismiss();
                                alertDialog.setCanceledOnTouchOutside(false);
                                final EditText idPhoto = (EditText) vw.findViewById(R.id.idPhoto);
                                TextView sauver = (TextView) vw.findViewById(R.id.save);
                                sauver.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String id = idPhoto.getText().toString().trim() + "-";
                                        alertDialog.dismiss();
                                        Photo photo = new Photo(id, path, new Date().getTime() + "", lieu, lat, lon);
                                        loading.setContentText("Ajout de la photo en cours...");
                                        loading.show();
                                        photosRef.add(photo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                loading.dismiss();
                                                loadPhotos();
                                            }
                                        });
                                    }
                                });
                                //alertDialog.show();
                                String id = ferme_id+""+(lenght++);
                                alertDialog.dismiss();
                                Photo photo = new Photo(id, path, new Date().getTime() + "", lieu, lat, lon);
                                loading.setContentText("Ajout de la photo en cours...");
                                loading.show();
                                photosRef.add(photo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        loading.dismiss();
                                        loadPhotos();
                                    }
                                });
                            }
                        });
                    }
                }
            };
            Handler handler = new Handler();
            handler.post(runnable);
            handler.postDelayed(runnable2, 6000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermUtil
                .REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Log.d(Cte.TAG_, "Permissions");
        }else if(requestCode==251){
            getLocation();
        }
    }


    private void getLocation(){
        if(checkPermissionGeoLocale()) {
            Toast.makeText(this, "Autorisez Cotton Nutrient à accéder à votre position", Toast.LENGTH_SHORT).show();
            requestPermissionGeoLocale();
        }else{
            if(isLocationEnabled())
                getCurrentLocation();
            else{
                androidx.appcompat.app.AlertDialog al= new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setMessage("Veuillez activer votre localisation")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).create();
                al.setCanceledOnTouchOutside(false);
                al.setCancelable(false);
                al.show();

            }
        }
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    lat=location.getLatitude();
                    lon=location.getLongitude();
                    changeLocatioText();
                }
            }
        });
    }

    private void changeLocatioText() {
        Geocoder geocoder = new Geocoder(DetailsFerme.this);
        List<Address> adress = null;
        try {
            adress = geocoder.getFromLocation(lat, lon, 2);
            String str = adress.get(0).getLocality();
            String st = adress.get(0).getCountryName();
            lieu=str+", "+st;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissionGeoLocale() {
        int fine_result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse_result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fine_result == PackageManager.PERMISSION_DENIED || coarse_result == PackageManager.PERMISSION_DENIED;
    }

    private boolean isLocationEnabled()  {
        return ((locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                ||(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))));
    }


    private void requestPermissionGeoLocale() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 251);
    }

}
