package com.example.odh_project_1.Adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.odh_project_1.Community
import com.example.odh_project_1.DataClass.Post
import com.example.odh_project_1.DatabaseConnect.FirebaseRepository
import com.example.odh_project_1.R
import com.google.firebase.auth.FirebaseAuth

class PostAdapter(private var postList: MutableList<Post> = mutableListOf()) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    fun setData(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged()
        // 데이터가 변경됐음을 알려줍니다.
    }

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.text_view_title)
        val body = view.findViewById<TextView>(R.id.text_view_body)
        val author = view.findViewById<TextView>(R.id.text_view_author)
        val date = view.findViewById<TextView>(R.id.text_date)
        val deleteButton =view.findViewById<TextView>(R.id.deleteButton)
        fun bind(post: Post,onDeleteSuccess: () -> Unit) {
            // ...
            val repository = FirebaseRepository()
            // 본인의 게시물인지 확인
            val currentUser = FirebaseAuth.getInstance().currentUser
            Log.d("PostAdapter11", "Current user UID: ${currentUser?.uid}, Post UID: ${post.uid}")
            if (currentUser != null && currentUser.uid == post.uid) {
                // 본인의 게시물이면 삭제 버튼을 표시합니다.
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener {
                    repository.deletePost(post.postId, {
                        onDeleteSuccess() // 데이터 삭제에 성공했을 때 호출됩니다.
                    }, { exception -> })
                }


            } else {
                // 본인의 게시물이 아니면 삭제 버튼을 숨깁니다.
                deleteButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)

        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.title.text = post.title
        holder.body.text = post.body
        holder.author.text = post.author
        holder.date.text = post.date
        holder.bind(post) {
            val index = postList.indexOfFirst { it.postId == post.postId }
            if (index != -1) {
                postList.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }
}