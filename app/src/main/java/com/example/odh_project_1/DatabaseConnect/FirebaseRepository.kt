package com.example.odh_project_1.DatabaseConnect

import com.example.odh_project_1.DataClass.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    fun savePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val myRef = firebaseDatabase.getReference("posts")
        val postId = myRef.push().key

        postId?.let {
            myRef.child(it).setValue(post)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess.invoke()
                    } else {
                        task.exception?.let { ex -> onFailure.invoke(ex) }
                    }
                }
        }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val myRef = firebaseDatabase.getReference("posts").child(postId)

        myRef.removeValue()
            .addOnSuccessListener { onSuccess.invoke() }
            .addOnFailureListener { ex -> onFailure.invoke(ex) }
    }

    fun fetchPosts(
        onDataChange: (List<Post>) -> Unit,
        onCancelled: (Exception) -> Unit
    ): ValueEventListener {
        val myRef = firebaseDatabase.getReference("posts")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { posts.add(it) }
                }
                onDataChange.invoke(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                onCancelled.invoke(error.toException())
            }
        }

        myRef.addValueEventListener(valueEventListener)

        return valueEventListener
    }

}

