package com.example.odh_project_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class JoinPage : AppCompatActivity() {
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabaseReference: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_page)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        val sv_root: ScrollView = findViewById(R.id.sv_root)

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                sv_root.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance()
        val backlogin: TextView  = findViewById(R.id.backlogin)
        val idEditText: EditText = findViewById(R.id.id)
        val passwordEditText: EditText = findViewById(R.id.password)
        val password2EditText: EditText = findViewById(R.id.password2)
        val nameEditText: EditText = findViewById(R.id.name)
        val name2EditText: EditText = findViewById(R.id.name2)
        val yearEditText: EditText = findViewById(R.id.year)
        val emailEditText: EditText = findViewById(R.id.email)

        val joinButton: Button = findViewById(R.id.joinbutton)

        backlogin.setOnClickListener {
            val intent=Intent(this@JoinPage,LoginPage1::class.java)
            startActivity(intent)
        }
        joinButton.setOnClickListener {
            val id = idEditText.text.toString()
            val password = passwordEditText.text.toString()
            val password2 = password2EditText.text.toString()
            val name = nameEditText.text.toString()
            val name2 = name2EditText.text.toString()
            val year = yearEditText.text.toString()
            val email = emailEditText.text.toString()

            if (validateInput(id, password, password2, name,name2, year, email)) {
                registerUser(email, password, password2, id, name, name2, year)
            }
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        password2: String,
        id: String,
        name: String,
        name2: String,
        year: String
    ) {
        if (password == password2) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = mFirebaseAuth.currentUser
                        val userId = user?.uid ?: ""

                        val userRef = mDatabaseReference.reference.child("users").child(userId)
                        val userData = hashMapOf<String, Any>(
                            "id" to id,
                            "name" to name,
                            "name2" to name2,
                            "year" to year,
                            "email" to email,
                            "password" to password
                        )
                        userRef.setValue(userData).addOnCompleteListener { saveTask ->
                            if (saveTask.isSuccessful) {
                                Toast.makeText(this, "회원 가입에 성공했습니다.", Toast.LENGTH_SHORT).apply {
                                    show()
                                    // Toast.LENGTH_SHORT의 기본 시간은 2초(2000ms)입니다.
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        mFirebaseAuth.signOut()
                                        val intent2 = Intent(this@JoinPage, LoginPage1::class.java)
                                        startActivity(intent2)
                                        finish()
                                    }, 2000)
                                }
                            } else {
                                Toast.makeText(this, "회원 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "회원 가입에 실패했습니다: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(
        id: String,
        password: String,
        password2: String,
        name: String,
        name2: String,
        year: String,
        email: String
    ): Boolean {
        if (id.isBlank() || password.isBlank() || password2.isBlank() || name.isBlank() ||name2.isBlank() || year.isBlank() || email.isBlank()) {
            Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "올바른 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "비밀번호는 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != password2) {
            Toast.makeText(this, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        // 기타 입력 값 검증 로직을 추가할 수 있습니다.

        return true
    }
    override fun onDestroy() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }
}