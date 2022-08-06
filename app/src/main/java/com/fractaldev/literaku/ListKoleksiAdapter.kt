package com.fractaldev.literaku

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ListKoleksiAdapter(private val listBuku: ArrayList<Buku>) : RecyclerView.Adapter<ListKoleksiAdapter.ListViewHolder>()  {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var row_title: TextView = itemView.findViewById(R.id.koleksiTitleView)
        var row_author: TextView = itemView.findViewById(R.id.koleksiAuthorView)
        val row_cover: ImageView = itemView.findViewById(R.id.koleksiImgView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_koleksi, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val book = listBuku[position]

        holder.row_title.text = book.title
        holder.row_author.text = book.author
        Glide.with(holder.itemView.context)
            .load(book.coverURL)
            .into(holder.row_cover)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listBuku[holder.adapterPosition]) }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Buku)
    }

    override fun getItemCount(): Int = listBuku.size
}