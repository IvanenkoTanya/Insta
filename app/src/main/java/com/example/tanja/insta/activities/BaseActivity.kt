package com.example.tanja.insta.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.tanja.insta.R
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity (val navNumber: Int): AppCompatActivity(){
    private val TAG = "BaseActivity"

    fun setupBottomNavigation(){
        bottom_navigaton_view.setIconSize(29f, 29f)
        bottom_navigaton_view.setTextVisibility(false)
        bottom_navigaton_view.enableItemShiftingMode(false)
        bottom_navigaton_view.enableShiftingMode(false)
        bottom_navigaton_view.enableAnimation(false)
        for(i in 0 until bottom_navigaton_view.menu.size()){
            bottom_navigaton_view.setIconTintList(i, null)
        }
        bottom_navigaton_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when(it.itemId) {
                        R.id.nav_item_home -> HomeActivity::class.java
                        R.id.nav_item_search -> AddFriendsActivity::class.java
                        R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> LikesActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(TAG, "unknown nav item clicked $it")
                            null
                        }
                    }
            if(nextActivity != null){
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(bottom_navigaton_view != null) {
            bottom_navigaton_view.menu.getItem(navNumber).isChecked = true
        }
    }
}