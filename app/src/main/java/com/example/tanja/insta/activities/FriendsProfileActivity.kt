package com.example.tanja.insta.activities


import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tanja.insta.R
import com.example.tanja.insta.models.User
import com.example.tanja.insta.utils.FirebaseHelper
import com.example.tanja.insta.utils.ValueEventListenerAdapter
import com.google.android.gms.tasks.Tasks

class FriendsProfileActivity(val userUid1: String = ""): BaseActivity(4){
   /* private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User
    private lateinit var mUsers: List<User>


    private var mPosition = mapOf<String, Int>()

    private var mFollows = mapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)
        setupBottomNavigation()

        val intent = intent
        val userUid = intent.getStringExtra("uid")

        mFirebase = FirebaseHelper(this)

        back_image.setOnClickListener {
            finish()
        }

        sign_out_image.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

        add_friends_image.setOnClickListener {
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }

        mFirebase.database.child("users").child(userUid)
                .addValueEventListener(ValueEventListenerAdapter {
                    mUser = it.asUser()!!
                    profile_image.loadUserPhoto(mUser.photo)
                    username_text.text = mUser.username
                })

        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mFirebase.database.child("images").child(userUid)
                .addValueEventListener(ValueEventListenerAdapter {
                    val images = it.children.map { it.getValue(String::class.java)!! }
                    images_recycler.adapter = ImagesAdapter(images.reversed())
                })

        mFirebase.database.child("users").child(userUid).child("followers")
                .addValueEventListener(ValueEventListenerAdapter {
                    followers_count_text.text = it.childrenCount.toString()
                })

        mFirebase.database.child("users").child(userUid).child("follows")
                .addValueEventListener(ValueEventListenerAdapter {
                    following_count_text.text = it.childrenCount.toString()
                })

        mFirebase.database.child("images").child(userUid)
                .addValueEventListener(ValueEventListenerAdapter {
                    posts_count_text.text = it.childrenCount.toString()
                })

        mFirebase.database.child("users").addValueEventListener(ValueEventListenerAdapter {
            val allUsers = it.children.map { it.asUser()!! }
            val (userList, otherUsersList) = allUsers.partition { it.uid == mFirebase.currentUid()!! }
            //mUser = userList.first()
            mUsers = otherUsersList

            //mPosition = mUsers.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
            mFollows = mUser.follows
        })

        val follows = mFollows[userUid] ?: false
        if (follows) {
           // follow_friends_btn.visibility = View.VISIBLE
            unfollow_friends_btn.visibility = View.GONE
        } else {
            //follow_friends_btn.visibility = View.GONE
            unfollow_friends_btn.visibility = View.VISIBLE
        }

        follow_friends_btn.setOnClickListener{
            follow(userUid)
            val follows = mFollows[userUid] ?: false
            if(follows) {
              //  follow_friends_btn.visibility = View.VISIBLE
                //unfollow_friends_btn.visibility = View.GONE


            } else{
                //follow_friends_btn.visibility = View.GONE
                unfollow_friends_btn.visibility = View.VISIBLE
            }
        }

        unfollow_friends_btn.setOnClickListener {
            unfollow(userUid)
            val follows = mFollows[userUid] ?: false
            if(follows) {
                follow_friends_btn.visibility = View.VISIBLE
                unfollow_friends_btn.visibility = View.GONE
            } else{
                follow_friends_btn.visibility = View.GONE
                unfollow_friends_btn.visibility = View.VISIBLE
            }
        }
    }

    fun follow(uid: String) {
        setFollow(uid, true) {
            mFollows += uid to true
        }
    }

    fun unfollow(uid: String) {
        setFollow(uid, false) {
            mFollows -= uid
        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        val followsTask = mFirebase.database.child("users").child(mFirebase.currentUid()!!).child("follows")
                .child(mUser.uid).setValueTrueOrRemove(follow)
        val followersTask = mFirebase.database.child("users").child(mUser.uid).child("followers")
                .child(mFirebase.currentUid()!!).setValueTrueOrRemove(follow)


        val feedPostsTask = task<Void> { taskSource ->
            mFirebase.database.child("feed-posts").child(uid)
                    .addValueEventListener(ValueEventListenerAdapter {
                        val postsMap = if (follow) {
                            it.children.map { it.key to it.value }.toMap()
                        } else {
                            it.children.map { it.key to null }.toMap()
                        }
                        mFirebase.database.child("feed-posts").child(mUser.uid).updateChildren(postsMap)
                                .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                    })
        }
        Tasks.whenAll(followsTask, followersTask, feedPostsTask).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }*/
}

