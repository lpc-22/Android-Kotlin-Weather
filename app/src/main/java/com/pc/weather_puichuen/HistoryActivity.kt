package com.pc.weather_puichuen

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pc.weather_puichuen.adapter.HistoryAdapter
import com.pc.weather_puichuen.data.History
import com.pc.weather_puichuen.data.HistoryRepository
import com.pc.weather_puichuen.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private val TAG = "HistoryActivity"
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyList: MutableList<History>
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.historyRepository = HistoryRepository(application)

        historyList = mutableListOf()
        historyAdapter = HistoryAdapter(applicationContext, historyList, {pos -> deleteNote(pos)})
        binding.rvHistory.adapter = historyAdapter
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.hasFixedSize()
        Log.d(TAG, "onCreate: historyList - ${historyList}")
    }

    override fun onStart() {
        super.onStart()

        this.historyRepository.allHistory?.observe(this){ receivedNotes ->
            historyList.clear()
            Log.d(TAG, "observe: ${receivedNotes}")
            if(receivedNotes.isNotEmpty()){
                Log.d(TAG, "onStart: ReceivedNotes: $receivedNotes")
                lifecycleScope.launch {
                    receivedNotes.forEach {history ->
                        if(historyList.contains(history)){
                            Log.d(TAG, "onStart: object already present the list")
                        }else{
                            Log.d(TAG, "onStart: adding object to the list")
                            historyList.add(history)
                        }
                    }
                    historyAdapter.notifyDataSetChanged()
                }
            }else{
                Log.d(TAG, "onStart: No data received from observer")
                Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show()
            }
            Log.d(TAG, "observe: historyList - ${historyList}")
        }
    }

    private fun deleteNote(position: Int){
        Log.d(TAG, "deleteNote: Trying to delete note at position $position")

        //ask for confirmation
        val confirmDialog = AlertDialog.Builder(this)
        confirmDialog.setTitle("Delete")
        confirmDialog.setMessage("Are you sure you want to delete this record?")
        confirmDialog.setNegativeButton("Cancel") { dialogInterface, i ->
            // historyAdapter.notifyDataSetChanged()
            dialogInterface.dismiss()
        }
        confirmDialog.setPositiveButton("Yes") { dialogInterface, i ->
            //delete from database
            Log.d(TAG, "deleteNote: trying to remove ${historyList[position]}")
            historyRepository.deleteHistory(historyList[position])
            historyList.removeAt(position)
            historyAdapter.notifyDataSetChanged()
        }
        confirmDialog.show()
    }
}