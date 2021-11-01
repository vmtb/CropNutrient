package com.vibe_viroma.cropnutrient.content.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.adapters.champListAdapter;
import com.vibe_viroma.cropnutrient.adapters.champs;
import com.vibe_viroma.cropnutrient.adapters.problemAdapter;
import com.vibe_viroma.cropnutrient.adapters.problemPoJo;
import com.vibe_viroma.cropnutrient.content.champs.AddChamp;
import com.vibe_viroma.cropnutrient.content.community.AddQuest;
import com.vibe_viroma.cropnutrient.objets.Ferme;
import com.vibe_viroma.cropnutrient.objets.Problem;
import com.vibe_viroma.cropnutrient.objets.User;
import com.vibe_viroma.cropnutrient.tools.Cte;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * A placeholder fragment containing a simple view.
 */

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Context ctx;
    private ImageView centerImage;
    private RecyclerView rv_champsList, rv_discuss;
    private SearchView searchbar;
    private FloatingActionButton fab_add;


    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference consRef= db.collection("Conseillers");
    private CollectionReference communityRef= db.collection("Communities");
    private CollectionReference userRef= db.collection("Users");
    private String user_id;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        user_id= FirebaseAuth.getInstance().getUid();
        ctx= container.getContext();
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        return initViews(index, root, inflater, container);
    }

    private View initViews(int index, View root,LayoutInflater inflater, ViewGroup container){

        if(index==3){
            root = inflater.inflate(R.layout.fragment_discuss, container, false);
            searchbar= root.findViewById(R.id.search_bar);
            rv_discuss= root.findViewById(R.id.recyclerView);
            fab_add= root.findViewById(R.id.fab);
            loadDiscuss();
            initClickFrag3();
        }else if(index==2) {
            root = inflater.inflate(R.layout.fragment_ferme, container, false);
            rv_champsList=(RecyclerView)root.findViewById(R.id.rv_champsList);
            loadFermes();
        }else{
            PulsatorLayout pulsator = (PulsatorLayout) root.findViewById(R.id.pulsator);
            pulsator.start();
            centerImage=(ImageView)root.findViewById(R.id.centerImage);
            initClickFrag0();
        }

        return root;
    }

    private void loadDiscuss() {
        rv_discuss.setLayoutManager(new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false));
        communityRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                final List<problemPoJo> c = new ArrayList<>();
                final problemAdapter adapter= new problemAdapter( ctx, c);
                rv_discuss.setAdapter(adapter);
                assert queryDocumentSnapshots != null;
                for (final DocumentSnapshot d:queryDocumentSnapshots.getDocuments()) {
                    final Problem pb= d.toObject(Problem.class);
                    Log.d(Cte.TAG_, pb.getFrom());
                    if(pb.getFrom()=="")
                        continue;
                    userRef.document(pb.getFrom()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            User user= task.getResult().toObject(User.class);
                            problemPoJo poJo= new problemPoJo(pb, user);
                            poJo.setKey(d.getId());
                            c.add(poJo);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                rv_discuss.setNestedScrollingEnabled(false);
                rv_discuss.scrollToPosition(0);
            }
        });
    }

    private void loadFermes() {
        rv_champsList.setLayoutManager(new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false));
        consRef.document(user_id).collection("Fermes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<champs> c = new ArrayList<>();
                champListAdapter champListAdapter= new champListAdapter(c, ctx);
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot d:queryDocumentSnapshots.getDocuments()) {
                    champs champs= new champs((Ferme)d.toObject(Ferme.class), d.getId(), user_id);
                    c.add(champs);
                }
                rv_champsList.setNestedScrollingEnabled(false);
                rv_champsList.setAdapter(champListAdapter);
                rv_champsList.scrollToPosition(0);
            }
        });
    }

    private void initClickFrag0() {
        centerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, AddChamp.class));
            }
        });
    }

    private void initClickFrag1() {}
    private void initClickFrag3() {
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, AddQuest.class));
            }
        });
    }

}