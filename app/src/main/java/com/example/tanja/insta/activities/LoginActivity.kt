package com.example.tanja.insta.activities

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tanja.insta.R
import com.example.tanja.insta.models.User
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.json.JSONException
import com.facebook.GraphRequest
import com.facebook.AccessToken
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var mDatabase: DatabaseReference
    val RC_SIGN_IN: Int = 1
    var mGoogleApiClient: GoogleApiClient? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        KeyboardVisibilityEvent.setEventListener(this, this)
        coordinateBtnAndInputs(login_button, email_input, password_input)
        login_button.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()

        login_fb_button.setReadPermissions("email", "public_profile")

        login_fb_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
               lateinit var mEmail : String
               lateinit var mName : String
                val accessToken = loginResult.accessToken
                val profile = Profile.getCurrentProfile()
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { `object`, response ->
                    try {
                        mName = `object`.getString("name")
                        mEmail = `object`.getString("email")
                        Log.d("Email = ", " $mEmail" + " $mName")
                        handleFacebookAccessToken(loginResult.accessToken, mEmail, mName)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender, birthday")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onError(error: FacebookException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        login_google_button.setOnClickListener {
            var signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signIntent, RC_SIGN_IN)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "Connection failed")
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun DatabaseReference.createUser(uid: String, user: User, onSuccess: () -> Unit){
        val reference = mDatabase.child("users").child(uid)
        reference.setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {

            }
        }
    }
    private fun mkUser(fullname: String, email: String): User {
        val username = mkUsername(fullname)
        return User(name = fullname, username = username, email = email)
    }

    private fun mkUsername(fullname: String): String =
            fullname.toLowerCase().replace(" ", ".")


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken, mEmail:String, mName: String) {
        mAuth = FirebaseAuth.getInstance()
        val credential = FacebookAuthProvider.getCredential(token.token)
        var firstAuth = false

        mAuth.fetchSignInMethodsForEmail(mEmail) { signInMethods ->
            if (signInMethods.isEmpty()) { firstAuth = true }
        }

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        if (firstAuth) {
                            val user = mkUser(mName, mEmail)
                            mAuth = FirebaseAuth.getInstance()
                            mDatabase = FirebaseDatabase.getInstance().reference
                            mDatabase.createUser(mAuth.currentUser!!.uid, user) {
                                val uid = mAuth.currentUser!!.uid
                            }
                        }
                        startHomeActivity()
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", it.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                    }
                }
    }

    private  fun FirebaseAuth.fetchSignInMethodsForEmail(email: String, onSuccess: (List<String>) -> Unit){
        fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                onSuccess(it.result.signInMethods ?: emptyList<String>())
            } else{
                showToast((it.exception!!.message!!))
            }
        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.login_button -> {
                val email = email_input.text.toString()
                val password = password_input.text.toString()
                if(validate(email, password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    }
                } else{
                    showToast("Please enter email and password")
                }
            }
            R.id.create_account_text -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean){
        if(isKeyboardOpen){
            create_account_text.visibility = View.GONE
        } else{
            create_account_text.visibility = View.VISIBLE
        }
    }

    private fun validate (email: String, password: String) =
        email.isNotEmpty() && password.isNotEmpty()

}
