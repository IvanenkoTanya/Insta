package com.example.tanja.insta.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.tanja.insta.R
import com.example.tanja.insta.models.User
import com.example.tanja.insta.utils.CameraHelper
import com.example.tanja.insta.utils.FirebaseHelper
import com.example.tanja.insta.utils.ValueEventListenerAdapter
import com.example.tanja.insta.views.PasswordDialog
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mCamera: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = CameraHelper(this)
        close_image.setOnClickListener { finish() }
        save_image.setOnClickListener { updateProfile() }
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }

        mFirebase = FirebaseHelper(this)
        mFirebase.currentUserReference()
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    mUser = it.asUser()!!
                    name_input.setText(mUser.name, TextView.BufferType.EDITABLE)
                    username_input.setText(mUser.username, TextView.BufferType.EDITABLE)
                    website_input.setText(mUser.website, TextView.BufferType.EDITABLE)
                    bio_input.setText(mUser.bio, TextView.BufferType.EDITABLE)
                    email_input.setText(mUser.email, TextView.BufferType.EDITABLE)
                    phone_input.setText(mUser.phone, TextView.BufferType.EDITABLE)
                    profile_image.loadUserPhoto(mUser.photo)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE && resultCode == RESULT_OK) {
            val ref = mFirebase.storage.child("users/${mFirebase.currentUid()!!}/photo")
            mFirebase.uploadUserPhoto(mCamera.imageUri!!) {
                ref.downloadUrl.addOnCompleteListener {
                    val photoUrl = it.result.toString()
                    mFirebase.updateUserPhoto(photoUrl) {
                        mUser = mUser.copy(photo = photoUrl)
                        profile_image.loadUserPhoto(mUser.photo)
                    }
                }
            }
        }
    }

    private fun updateProfile() {
        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if(error == null){
            if(mPendingUser.email == mUser.email){
                updateUser(mPendingUser)
            } else{
                PasswordDialog().show(supportFragmentManager, "password_dilog")
            }
        } else{
            showToast(error)
        }
    }

    private fun readInputs(): User {
        return User(
                name = name_input.text.toString(),
                username = username_input.text.toString(),
                email = email_input.text.toString(),
                website = website_input.text.toStringOrNull(),
                bio = bio_input.text.toStringOrNull(),
                phone = phone_input.text.toStringOrNull()
        )
    }

    override fun onPasswordConfirm(password: String) {
        if(password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebase.reauthenticate(credential) {
                mFirebase.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else{
            showToast("You should enter your password")
        }
    }

    private fun updateUser(user: User){
        val updatesMap = mutableMapOf<String, Any?>()
        if(user.name != mUser.name) updatesMap["name"] = user.name
        if(user.username != mUser.username) updatesMap["username"] = user.username
        if(user.website != mUser.website) updatesMap["website"] = user.website
        if(user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if(user.phone != mUser.phone) updatesMap["phone"] = user.phone
        if(user.email != mUser.email) updatesMap["email"] = user.email

        mFirebase.updateUser(updatesMap) {
            showToast("Profile saved")
            finish()
        }
    }
    private fun validate(user: User): String? =
        when{
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.email.isEmpty() -> "Please enter email"
            else -> null
        }
}