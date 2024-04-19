package com.pc.weather_puichuen.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.pc.weather_puichuen.R
import com.pc.weather_puichuen.data.History
import com.pc.weather_puichuen.databinding.RowHistoryItemBinding

class HistoryAdapter(
    private val context: Context,
    var historyList: MutableList<History>,
    private val deleteButtonClicked: (Int) -> Unit
)
    : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    private val TAG = "HistoryAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder: ${historyList}")
        return ViewHolder(RowHistoryItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHistory: History = historyList[position]
        holder.bind(currentHistory)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    inner class ViewHolder(b: RowHistoryItemBinding):RecyclerView.ViewHolder(b.root){
        var binding: RowHistoryItemBinding

        init {
            binding = b

            val btnDeleteFromShortList = itemView.findViewById<Button>(R.id.btn_remove_from_list)
            btnDeleteFromShortList.setOnClickListener {
                deleteButtonClicked(adapterPosition)
            }
        }

        fun bind(currentHistory: History){
            if(currentHistory != null){
                binding.tvLocation.text = currentHistory.location
                binding.tvDateTime.text = currentHistory.time
                binding.tvTemperature.text = currentHistory.temperature.toString()+"℃"
                binding.tvHudmidity.text = currentHistory.hudmidity.toString()+"%"
                binding.tvCondition.text = currentHistory.condition
            }
        }
    }
}