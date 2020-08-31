package com.deepquotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.ViewHolder> {

    private List quotesList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView quoteText;
        public ViewHolder(View itemView) {
            super(itemView);
            quoteText = itemView.findViewById(R.id.quote_history);
        }
    }

    public QuotesAdapter(List quotesList) {
        this.quotesList = quotesList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotes_history_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    public void onBindViewHolder(ViewHolder holder, int position) {
//        quotesList.get(position);
        holder.quoteText.setText(quotesList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return quotesList.size();
    }
}
