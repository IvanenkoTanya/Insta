package com.example.tanja.insta.views

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.tanja.insta.R
import kotlinx.android.synthetic.main.dialog_password.view.*

class PasswordDialog: android.support.v4.app.DialogFragment(){
    private lateinit var mListener: Listener
    interface Listener{
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mListener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog.Builder(context!!)
                .setView(view)
                .setPositiveButton(android.R.string.ok, {_, _ ->
                    mListener.onPasswordConfirm(view.password_input.text.toString())
                })
                .setNegativeButton(android.R.string.cancel, {_, _ ->
                    //do nothing
                })
                .setTitle(R.string.please_enter_password)
                .create()
    }
}