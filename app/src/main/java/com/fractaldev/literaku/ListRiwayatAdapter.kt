package com.fractaldev.literaku

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListRiwayatAdapter(private val listBuku: ArrayList<Buku>) : RecyclerView.Adapter<ListRiwayatAdapter.ListViewHolder>()  {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var row_title: TextView = itemView.findViewById(R.id.row_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_riwayat, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, description) = listBuku[position]
        holder.row_title.text = title
    }

    override fun getItemCount(): Int = listBuku.size
}