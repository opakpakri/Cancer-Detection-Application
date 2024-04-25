package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.DataHistoryAdapter
import com.dicoding.asclepius.database.DataHistory
import com.dicoding.asclepius.database.LocalDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CancerHistoryActivity : AppCompatActivity(), DataHistoryAdapter.OnDeleteClickListener {
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: DataHistoryAdapter
    private var historyList: MutableList<DataHistory> = mutableListOf()
    private lateinit var tvNotFound: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancer_history)

        historyRecyclerView = findViewById(R.id.rvHistory)
        tvNotFound = findViewById(R.id.tvNotFound)

        historyAdapter = DataHistoryAdapter(historyList)
        historyAdapter.setOnDeleteClickListener(this)
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        loadHistoryFromDatabase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_HISTORY_UPDATE && resultCode == RESULT_OK) {
            loadHistoryFromDatabase()
        }
    }

    private fun loadHistoryFromDatabase() {
        GlobalScope.launch(Dispatchers.Main) {
            val history = LocalDatabase.getDatabase(this@CancerHistoryActivity)
                .cancerHistoryDao().getAllHistory()
            historyList.clear()
            historyList.addAll(history)
            historyAdapter.notifyDataSetChanged()
            showOrHideNoHistoryText()
        }
    }

    private fun showOrHideNoHistoryText() {
        if (historyList.isEmpty()) {
            tvNotFound.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.GONE
        } else {
            tvNotFound.visibility = View.GONE
            historyRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDeleteClick(position: Int) {
        val history = historyList[position]
        GlobalScope.launch(Dispatchers.IO) {
            LocalDatabase.getDatabase(this@CancerHistoryActivity)
                .cancerHistoryDao().deleteHistory(history)
        }
        historyList.removeAt(position)
        historyAdapter.notifyDataSetChanged()
        showOrHideNoHistoryText()
    }

    companion object {
        const val REQUEST_HISTORY_UPDATE = 1001
        private const val TAG = "CancerHistoryActivity"
    }

}