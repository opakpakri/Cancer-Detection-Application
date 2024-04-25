package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.DataHistory

class DataHistoryAdapter(private val dataHistoryLists: List<DataHistory>) :
    RecyclerView.Adapter<DataHistoryAdapter.ViewHolder>() {

    private var onDeleteClickListener: OnDeleteClickListener? = null

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataHistoryLists[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = dataHistoryLists.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.Img)
        private val resultTextView: TextView = itemView.findViewById(R.id.tvCategory)
        private val deleteImageView: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(dataHistory: DataHistory) {
            Glide.with(itemView.context)
                .load(dataHistory.imagePath)
                .into(imageView)

            resultTextView.text = dataHistory.result

            deleteImageView.setOnClickListener {
                onDeleteClickListener?.onDeleteClick(adapterPosition)
            }
        }
    }
}
