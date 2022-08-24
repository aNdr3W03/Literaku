package com.fractaldev.literaku

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ListPenjelajahAdapter(private val list: ArrayList<Penjelajah>) : RecyclerView.Adapter<ListPenjelajahAdapter.ListViewHolder>()  {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var row_title: TextView = itemView.findViewById(R.id.penjelajahTitleView)
        var row_description: TextView = itemView.findViewById(R.id.penjelajahDescriptionView)
        val row_image: ImageView = itemView.findViewById(R.id.penjelajahImgView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_penjelajah, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = list[position]

        holder.row_title.text = item.title
        holder.row_description.text = item.description
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.row_image)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[holder.adapterPosition]) }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Penjelajah)
    }

    override fun getItemCount(): Int = list.size
}