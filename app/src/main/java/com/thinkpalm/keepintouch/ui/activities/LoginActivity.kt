package com.thinkpalm.keepintouch.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thinkpalm.keepintouch.CoreApplication
import com.thinkpalm.keepintouch.FirebaseDatabaseNodes
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.model.User
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPrefKeys
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPreferenceHandler
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {

    }
    val PLACE_PICKER_REQUEST = 1
    var builder: PlacePicker.IntentBuilder = PlacePicker.IntentBuilder()
    var address: Address? = null
    var location: Location? = null
    @SuppressLint("MissingPermission")

    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        mGoogleApiClient = GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mAuth = FirebaseAuth.getInstance()
        btn_fb.setOnClickListener {
            progress_bar?.visibility=View.VISIBLE
            LoginManager.getInstance().logOut();
            LoginManager.getInstance()
                    .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        }
        mCallbackManager = CallbackManager.Factory.create()

        handleFbLoginCallback()
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient?.connect()
    }

    override fun onStop() {
        mGoogleApiClient?.disconnect()
        super.onStop()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            val user = mAuth?.getCurrentUser()
                            SharedPreferenceHandler.getInstance(this@LoginActivity).saveBoolean(
                                    SharedPrefKeys.IS_LOGIN, true)


                        } else {
                            Toast.makeText(this@LoginActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }

                    }
                })
    }

    private fun handleFbLoginCallback() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        handleGraphAPI(loginResult.accessToken)
                        handleFacebookAccessToken(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        Toast.makeText(this@LoginActivity, "Login Cancel", Toast.LENGTH_LONG).show()
                    }

                    override fun onError(exception: FacebookException) {
                        Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun handleGraphAPI(accessToken: AccessToken?) {
        val params = Bundle()
        params.putString("fields", "id,email,name,first_name,last_name,gender,link,cover,picture" +
                ".type" +
                "(large)")
        GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                GraphRequest.Callback { response ->
                    if (response != null) {
                        try {
                            val data = response.jsonObject
                            if (data.has("picture")) {
                                var email = ""
                                var name = ""
                                var firstname = ""
                                var lastname = ""
                                var gender = ""
                                var link = ""
                                var facebookId = ""

                                Log.e("FACEBOOK", data.toString());
                                if (data.has("link"))
                                    email = data.getString("link")
                                if (data.has("email"))
                                    email = data.getString("email")
                                if (data.has("first_name"))
                                    firstname = data.getString("first_name")
                                if (data.has("gender"))
                                    gender = data.getString("gender")
                                if (data.has("last_name"))
                                    lastname = data.getString("last_name")
                                if (data.has("name"))
                                    name = data.getString("name")
                                if (data.has("id"))
                                    facebookId = data.getString("id")
                                val profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url")

                                val sharedPred = SharedPreferenceHandler.getInstance(this@LoginActivity)

                                sharedPred.saveString(SharedPrefKeys.PROFILE_PICTURE, profilePicUrl)
                                sharedPred.saveString(SharedPrefKeys.EMAIL, email)
                                sharedPred.saveString(SharedPrefKeys.NAME, name)
                                sharedPred.saveString(SharedPrefKeys.FIRSTNAME, firstname)
                                sharedPred.saveString(SharedPrefKeys.LASTNAME, lastname)
                                sharedPred.saveString(SharedPrefKeys.GENDER, gender)
                                sharedPred.saveString(SharedPrefKeys.LINK, link)
                                sharedPred.saveString(SharedPrefKeys.FACEBOOK_ID, facebookId)
                                var user = User(facebookId, name, email, firstname,
                                        lastname,
                                        gender, profilePicUrl, link);
                                user.latitude = ""+location?.latitude
                                user.longitude = ""+location?.longitude
                                user.address = address?.locality
                                writeNewUser(user)

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }).executeAsync()
    }

    private var mUser: User? = null

    private fun writeNewUser(user: User) {
        mUser = user
        locationPicker()
    }

    private fun locationPicker() {
        var builder: PlacePicker.IntentBuilder = PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this@LoginActivity), PLACE_PICKER_REQUEST);
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace();
        } catch (e1: GooglePlayServicesRepairableException) {
            e1.printStackTrace();
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                var place: Place = PlacePicker.getPlace(data, this);
                var stBuilder: StringBuilder? = StringBuilder()
                val placename: String? = String.format("%s", place.getName());
                val latitude: String = "" + place.getLatLng().latitude;
                val longitude: String = "" + place.getLatLng().longitude;
                val address: String? = String.format(" %s", place.getAddress());
                stBuilder?.append("Name: ");
                stBuilder?.append(placename);
                stBuilder?.append("\n");
                stBuilder?.append("Address: ");
                stBuilder?.append(address);
                Log.e("LOCATION", stBuilder?.toString())
                mUser?.latitude = latitude
                mUser?.longitude = longitude
                mUser?.address = stBuilder.toString()
                SharedPreferenceHandler.getInstance(this@LoginActivity).saveString(SharedPrefKeys.LATITUDE,
                        ""+latitude)
                SharedPreferenceHandler.getInstance(this@LoginActivity).saveString(SharedPrefKeys.LONGTITUDE,""+longitude)
                FirebaseDatabase.getInstance().getReference(FirebaseDatabaseNodes.USERS).child(mUser
                        ?.userId).setValue(mUser)
                getUserList()

            }
        } else
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
    }
    private fun getUserList() {
        FirebaseDatabase.getInstance()?.getReference(FirebaseDatabaseNodes.USERS)?.addValueEventListener(object :
                ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val personList = ArrayList<User?>()
                for (postSnapshot in dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    val person = postSnapshot.getValue(User::class.java)
                    person?.uniqueKey = postSnapshot.key;
                    personList.add(person)
                }
                (applicationContext as CoreApplication).mUserList = personList
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        })
    }

}
