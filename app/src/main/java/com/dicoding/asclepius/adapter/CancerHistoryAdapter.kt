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
import com.dicoding.asclepius.database.CancerHistory

class CancerHistoryAdapter(private val cancerHistoryList: List<CancerHistory>) :
    RecyclerView.Adapter<CancerHistoryAdapter.ViewHolder>() {

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
        val currentItem = cancerHistoryList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = cancerHistoryList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.Img)
        private val resultTextView: TextView = itemView.findViewById(R.id.tvCategory)
        private val deleteImageView: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(cancerHistory: CancerHistory) {
            Glide.with(itemView.context)
                .load(cancerHistory.imagePath)
                .into(imageView)

            resultTextView.text = cancerHistory.result

            deleteImageView.setOnClickListener {
                onDeleteClickListener?.onDeleteClick(adapterPosition)
            }
        }
    }
}
