package com.example.tanja.insta.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.tanja.insta.R
import com.example.tanja.insta.models.User
import com.example.tanja.insta.utils.FirebaseHelper
import com.example.tanja.insta.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit  var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")

        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        settings_image.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

        add_friends_image.setOnClickListener {
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }

        mFirebase = FirebaseHelper(this)
        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.asUser()!!
            profile_image.loadUserPhoto(mUser.photo)
            username_text.text = mUser.username
        })

        images_recycler.layoutManager = GridLayoutManager (this, 3)
        mFirebase.database.child("images").child(mFirebase.currentUid()!!)
                .addValueEventListener(ValueEventListenerAdapter{
                    val images = it.children.map { it.getValue(String::class.java)!! }
                    images_recycler.adapter = ImagesAdapter(images.reversed())
                })

        mFirebase.database.child("users").child(mFirebase.currentUid()!!).child("follows")
                .addValueEventListener(ValueEventListenerAdapter{
                    following_count_text.text = it.childrenCount.toString()
                })

        mFirebase.database.child("users").child(mFirebase.currentUid()!!).child("followers")
                .addValueEventListener(ValueEventListenerAdapter{
                    followers_count_text.text = it.childrenCount.toString()
                })

        mFirebase.database.child("images").child(mFirebase.currentUid()!!)
                .addValueEventListener(ValueEventListenerAdapter{
                    posts_count_text.text = it.childrenCount.toString()
                })
    }
}

class ImagesAdapter(private val images: List<String>):
        RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){
    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }

    override fun getItemCount(): Int = images.size
}

class SquareImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs){
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
