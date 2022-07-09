package com.fractaldev.literaku

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListKoleksiAdapter(private val listBuku: ArrayList<Buku>) : RecyclerView.Adapter<ListKoleksiAdapter.ListViewHolder>()  {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var row_title: TextView = itemView.findViewById(R.id.row_title)
        var row_author: TextView = itemView.findViewById(R.id.row_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_koleksi, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, description, author) = listBuku[position]
        holder.row_title.text = title
        holder.row_author.text = author
    }

    override fun getItemCount(): Int = listBuku.size
}