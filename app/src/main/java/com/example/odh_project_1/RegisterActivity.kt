package com.example.odh_project_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.odh_project_1.DataClass.Post
import com.example.odh_project_1.DatabaseConnect.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var registerButton: Button

    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        titleEditText = findViewById(R.id.title_et)
        contentEditText = findViewById(R.id.content_et)
        registerButton = findViewById(R.id.reg_button)

        registerButton.setOnClickListener {
            registerPost()
        }
    }

    private fun registerPost() {
        val title = titleEditText.text.toString()
        val body = contentEditText.text.toString()

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val author = FirebaseAuth.getInstance().currentUser?.email ?: return
        val dateTime = Date(System.currentTimeMillis())
        val formattedDate = SimpleDateFormat("yyyy-MM-dd kk:mm:ss E", Locale("ko", "KR")).format(dateTime)
        val POSTuid =FirebaseAuth.getInstance().currentUser?.uid
        Log.d("RegisterActivity111", "Current user UID: $POSTuid")
        val post = Post(POSTuid!!,"", title, body, author, formattedDate)

        repository.savePost(post, {
            // 게시물 등록 성공
            val intent = Intent(this, Community::class.java)
            startActivity(intent)
            finish()
        }, { exception ->
            // 게시물 등록 실패
            // 사용자에게 메시지 표시하거나 다른 동작 수행
        })

    }
}
