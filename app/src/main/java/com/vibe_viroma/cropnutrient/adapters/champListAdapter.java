package com.vibe_viroma.cropnutrient.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.content.champs.AddChamp;
import com.vibe_viroma.cropnutrient.content.champs.DetailsFerme;
import com.vibe_viroma.cropnutrient.objets.Ferme;

import java.util.List;
import java.util.Objects;

public class champListAdapter extends RecyclerView.Adapter<champListAdapter.champItem> {
    private List<champs> champsList;
    private Context ctx;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference consRef= db.collection("Conseillers");

    public champListAdapter(List<champs> champsList, Context ctx) {
        this.champsList = champsList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public champListAdapter.champItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.item_champ, parent,false);

        return new champItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final champListAdapter.champItem holder, int position) {
        champs c= champsList.get(position);
        final Ferme ferme=c.getFerme();
        holder.tv_name.setText("Champ "+ferme.getFerme_name());
        holder.tv_producteur.setText(ferme.getProducteur());
        holder.tv_superficie.setText("Superficie: "+ferme.getSuperficie()+ ""+ferme.getUnite());

        String user_id= c.getUser_id();
        final String ferme_id= c.getFerme_id();
        consRef.document(user_id).collection("Fermes").document(ferme_id).collection("Photos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                long size= Objects.requireNonNull(task.getResult()).size();
                holder.tv_photos.setText(size+" Prise(s)");
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctx.startActivity(new Intent(ctx, DetailsFerme.class)
                        .putExtra("ferme_id",ferme_id )
                        .putExtra("champ_name", ferme.getFerme_name()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return champsList.size();
    }

    static class champItem  extends RecyclerView.ViewHolder {
        TextView tv_name, tv_superficie, tv_photos, tv_producteur;
        champItem(@NonNull View itemView) {
            super(itemView);
            tv_name=(TextView)itemView.findViewById(R.id.name);
            tv_superficie=(TextView)itemView.findViewById(R.id.superficie);
            tv_photos=(TextView)itemView.findViewById(R.id.middle);
            tv_producteur=(TextView)itemView.findViewById(R.id.prod);
        }
    }
}
