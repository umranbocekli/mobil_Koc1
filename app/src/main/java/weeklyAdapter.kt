package com.example.yazlmdev.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yazlmdev.R

class WeeklyAdapter(private val days: List<String>, private val onDayClick: (String) -> Unit) :
    RecyclerView.Adapter<WeeklyAdapter.WeeklyViewHolder>() {

    class WeeklyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.textViewDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return WeeklyViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeeklyViewHolder, position: Int) {
        val day = days[position]
        holder.dayText.text = day
        holder.view.setOnClickListener {
            onDayClick(day)
        }
    }

    override fun getItemCount(): Int = days.size
}
