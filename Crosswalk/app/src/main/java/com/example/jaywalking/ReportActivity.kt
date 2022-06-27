package com.example.jaywalking

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jaywalking.MyApplication.Companion.db
import com.example.jaywalking.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity() {
    lateinit var binding : ActivityReportBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_report)

        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myCheckPermission(this)
        binding.addFab.setOnClickListener {
            if (MyApplication.checkAuth() || MyApplication.email != null) {
                startActivity(Intent(this, AddActivity::class.java))
            }
            else {
                Toast.makeText(this, "인증 필요", Toast.LENGTH_SHORT).show()
            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.reportLayout.setBackgroundColor(Color.parseColor(bgColor))

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.reportLayout.setBackgroundColor(Color.parseColor(bgColor))
    }

    override fun onStart() {
        super.onStart()
        makeRecyclerView()
    }

    private fun makeRecyclerView() {
        MyApplication.db.collection("news")
            .get()
            .addOnSuccessListener {result ->
                val itemList = mutableListOf<ItemData>()
                for(document in result) {
                    val item = document.toObject(ItemData::class.java)
                    item.docId = document.id
                    itemList.add(item)

                }
                binding.reportRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.reportRecyclerView.adapter = AddAdapter(this, itemList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }
}