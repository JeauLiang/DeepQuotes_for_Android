package com.deepquotes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class QuotesAdapter(private val quotesList: List<*>) : RecyclerView.Adapter<QuotesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var quoteText: TextView

        init {
            quoteText = itemView.findViewById(R.id.quote_history)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quotes_history_item, parent, false)
        val viewHolder = ViewHolder(view)
        val clipboardManager = parent.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //        点击监听
//        viewHolder.quoteText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = viewHolder.getAdapterPosition();
//                String quote = (String) quotesList.get(position);
//                Toast.makeText(view.getContext(),"u click "+ quote,Toast.LENGTH_SHORT).show();
//            }
//        });

//        长按监听
        viewHolder.quoteText.setOnLongClickListener { view ->
            val position = viewHolder.adapterPosition
            val quote = quotesList[position] as String
            clipboardManager.setPrimaryClip(ClipData.newPlainText("quote", "$quote——来自「相顾无言」"))
            Toast.makeText(view.context, "文字已复制", Toast.LENGTH_SHORT).show()
            false
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        quotesList.get(position);
        holder.quoteText.text = quotesList[position].toString()
    }

    override fun getItemCount(): Int {
        return quotesList.size
    }

}