package com.st10397576.sanewshub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val newsList: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.textCategory)
        val title: TextView = view.findViewById(R.id.textTitle)
        val body: TextView = view.findViewById(R.id.textBody)
        val source: TextView = view.findViewById(R.id.textSource)
        val time: TextView = view.findViewById(R.id.textTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = newsList[position]
        holder.category.text = item.category.uppercase()
        holder.title.text = item.title
        holder.body.text = item.body
        holder.source.text = "via ${item.source}"

        // Format timestamp (e.g., "2 hours ago")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(item.timestamp)
        val now = Date()
        val diff = now.time - date!!.time
        val hours = diff / (1000 * 60 * 60)
        holder.time.text = if (hours > 0) "$hours hours ago" else "Just now"
    }

    override fun getItemCount() = newsList.size
}