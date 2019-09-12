package com.example.tanja.insta.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.tanja.insta.R
import com.example.tanja.insta.models.FeedPost
import com.example.tanja.insta.models.User
import com.example.tanja.insta.utils.CameraHelper
import com.example.tanja.insta.utils.FirebaseHelper
import com.example.tanja.insta.utils.GlideApp
import com.example.tanja.insta.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")

        mFirebase = FirebaseHelper(this)

        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

        back_image.setOnClickListener { finish() }
        share_text.setOnClickListener { share() }

        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.asUser()!!
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
            } else{
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        if(imageUri != null){
            val uid = mFirebase.currentUid()!!
            mFirebase.storage.child("users").child(uid).child("images")
                    .child(imageUri.lastPathSegment).putFile(imageUri).addOnCompleteListener()
                    {
                        if (it.isSuccessful) {
                            mFirebase.database.child("images").child(uid).push()
                                    .setValue(imageUri.toString())
                                    .addOnCompleteListener {
                                        if(it.isSuccessful){
                                            mFirebase.database.child("feed-posts").child(uid)
                                                    .push()
                                                    .setValue(mkFeedPost(uid, imageUri.toString())).addOnCompleteListener {
                                                        if(it.isSuccessful) {
                                                            startActivity(Intent(this, ProfileActivity::class.java))
                                                            finish()
                                                        }
                                                    }
                                        } else {
                                            showToast(it.exception!!.message!!)
                                        }
                                    }
                        } else {
                            showToast(it.exception!!.message!!)
                        }
                    }
        }
    }

    private fun mkFeedPost(uid: String, imageDownloadUri: String): FeedPost {
        return FeedPost(
                uid = uid,
                username = mUser.username,
                image = imageDownloadUri,
                caption = caption_input.text.toString(),
                photo = mUser.photo
        )
    }
}


