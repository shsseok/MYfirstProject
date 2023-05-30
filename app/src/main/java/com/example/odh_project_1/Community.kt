package com.example.odh_project_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.odh_project_1.Adapters.PostAdapter
import com.example.odh_project_1.DatabaseConnect.FirebaseRepository

class Community : AppCompatActivity() {

    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        val writeButton: Button = findViewById(R.id.write_button)
        writeButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val adapter = PostAdapter(mutableListOf())

        // RecyclerView에 어댑터 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        firebaseRepository = FirebaseRepository()


        firebaseRepository.fetchPosts({ posts ->
            // 성공적으로 게시물을 가져올 경우 어댑터에 데이터를 설정
            adapter.setData(posts)
        }, { exception ->
            Toast.makeText(this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        })
    }
}