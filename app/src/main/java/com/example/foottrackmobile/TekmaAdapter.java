package com.example.foottrackmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TekmaAdapter extends RecyclerView.Adapter<TekmaAdapter.ViewHolder> {

    private final ArrayList<Tekma> tekme;

    public TekmaAdapter(ArrayList<Tekma> tekme) {
        this.tekme = tekme;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teams, score, date, stadium;

        public ViewHolder(View itemView) {
            super(itemView);
            teams = itemView.findViewById(R.id.teamsText);
            score = itemView.findViewById(R.id.scoreText);
            date = itemView.findViewById(R.id.dateText);
            stadium = itemView.findViewById(R.id.stadiumText);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tekma, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tekma t = tekme.get(position);

        holder.teams.setText(t.domacaIme + " vs " + t.gostIme);
        holder.score.setText(t.domacaGol + " : " + t.gostujocaGol);
        holder.date.setText("Datum: " + t.datum);
        holder.stadium.setText("Stadion: " + t.stadionIme);
    }

    @Override
    public int getItemCount() {
        return tekme.size();
    }
}
