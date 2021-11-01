package com.vibe_viroma.cropnutrient.adapters;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.content.community.AddQuest;
import com.vibe_viroma.cropnutrient.objets.Problem;
import com.vibe_viroma.cropnutrient.objets.Response;
import com.vibe_viroma.cropnutrient.objets.User;
import com.vibe_viroma.cropnutrient.tools.Cte;

import java.util.Date;
import java.util.List;

public class responseAdapter extends RecyclerView.Adapter<responseAdapter.reponseItem> {
    private Context ctx;
    private List<reponsePoJo> problemPoJoList;

    public responseAdapter(Context ctx, List<reponsePoJo> problemPoJoList) {
        this.ctx = ctx;
        this.problemPoJoList = problemPoJoList;
    }

    @NonNull
    @Override
    public reponseItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.item_response, parent,false);
        return new reponseItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reponseItem holder, int position) {
        final Response response= problemPoJoList.get(position).getResponse();
        final User user= problemPoJoList.get(position).getUser();
        final String key= problemPoJoList.get(position).getKey();

        response.setReponse_key(key);
        holder.tv_name.setText(response.getReponse());

        long time= Long.parseLong(response.getTime());
        holder.tv_superficie.setText("");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dsf= new SimpleDateFormat("dd MMM yyyy, HH:mm");
            holder.tv_superficie.setText(dsf.format(new Date(time)));
        }
        holder.tv_photos.setText("");
        holder.tv_producteur.setText(user.getFirstname()+" "+user.getLastname());

    }

    @Override
    public int getItemCount() {
        return problemPoJoList.size();
    }

    static class reponseItem  extends RecyclerView.ViewHolder {
        TextView tv_name, tv_superficie, tv_photos, tv_producteur;
        reponseItem(@NonNull View itemView) {
            super(itemView);
            tv_name=(TextView)itemView.findViewById(R.id.name);
            tv_superficie=(TextView)itemView.findViewById(R.id.superficie);
            tv_photos=(TextView)itemView.findViewById(R.id.middle);
            tv_producteur=(TextView)itemView.findViewById(R.id.prod);
        }
    }
}
