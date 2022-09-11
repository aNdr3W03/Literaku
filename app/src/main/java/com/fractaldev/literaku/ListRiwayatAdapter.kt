package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ListRiwayatAdapter(private val listBuku: ArrayList<Buku> = arrayListOf()) : RecyclerView.Adapter<ListRiwayatAdapter.ListViewHolder>()  {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var row_title: TextView = itemView.findViewById(R.id.row_title)
        var row_time: TextView = itemView.findViewById(R.id.row_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_riwayat, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val book = listBuku[position]

        holder.row_title.text = book.title
        holder.row_time.text = book.lastRead

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listBuku[holder.adapterPosition]) }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: ArrayList<Buku>) {
        listBuku.clear()
        listBuku.addAll(newData)
        this.notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Buku)
    }

    override fun getItemCount(): Int = listBuku.size
}