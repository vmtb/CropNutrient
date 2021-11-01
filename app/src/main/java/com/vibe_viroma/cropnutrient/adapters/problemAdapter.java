package com.vibe_viroma.cropnutrient.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.content.community.AddQuest;
import com.vibe_viroma.cropnutrient.objets.Problem;
import com.vibe_viroma.cropnutrient.objets.User;
import com.vibe_viroma.cropnutrient.tools.Cte;

import java.util.List;

public class problemAdapter extends RecyclerView.Adapter<champListAdapter.champItem> {
    private Context ctx;
    private List<problemPoJo> problemPoJoList;

    public problemAdapter(Context ctx, List<problemPoJo> problemPoJoList) {
        this.ctx = ctx;
        this.problemPoJoList = problemPoJoList;
    }

    @NonNull
    @Override
    public champListAdapter.champItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.item_champ, parent,false);
        return new champListAdapter.champItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull champListAdapter.champItem holder, int position) {
        final Problem problem= problemPoJoList.get(position).getProblem();
        final User user= problemPoJoList.get(position).getUser();
        final String key= problemPoJoList.get(position).getKey();
        problem.setKey(key);

        holder.tv_name.setText(problem.getTitle());
        holder.tv_superficie.setText("0 réponse(s)");
        holder.tv_photos.setText("");
        holder.tv_producteur.setText(user.getFirstname()+" "+user.getLastname());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctx.startActivity(new Intent(ctx, AddQuest.class)
                .putExtra("PROBLEM", Cte.object2json(problem))
                .putExtra("USER", Cte.object2json(user)) );
            }
        });

    }

    @Override
    public int getItemCount() {
        return problemPoJoList.size();
    }

}
