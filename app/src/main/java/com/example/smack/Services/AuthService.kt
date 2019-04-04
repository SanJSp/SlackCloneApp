package com.example.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.Controller.App
import com.example.smack.Utilities.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

//    var isLoggedIn = false
//    var userEmail = ""
//    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        //val url = URL_REGISTER

        //JSON Body
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        // convert to string because of bytearray conversion later
         val requestBody = jsonBody.toString()

        //web request, POST is method type
        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { _ ->
            complete(true)
        }, Response.ErrorListener { error ->                                    //error response
            Log.d("ERROR", "Could not register user: $error")
            complete(false)
        })
        {   //bodycontent type
            override fun getBodyContentType(): String{
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        //add to queue to be requested
        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser (context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        //JSON Body
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        // convert to string because of bytearray conversion later
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener { response ->

            try {
                App.prefs.userEmail = response.getString("user")
                App.prefs.authToken = response.getString("token")
                App.prefs.isLoggedIn = true
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }


        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
            complete(false)

        }) {   //bodycontent type
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

        }
        App.prefs.requestQueue.add(loginRequest)

    }

    fun createUser( context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()


        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->

            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }




        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Could not add user: $error")
            complete(false)

        })  {
            //bodycontent type
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()


            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }


        }


        App.prefs.requestQueue.add(createRequest)

    }


    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest =
            object : JsonObjectRequest(Method.GET, "$URL_GET_USER${App.prefs.userEmail}", null, Response.Listener { response ->

                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString(("avatarName"))
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.id = response.getString("_id")

                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                    complete(true)

                } catch (e: JSONException) {
                    Log.d("JSON", "EXC:" + e.localizedMessage)
                }


            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not add user: $error")
                complete(false)

            }) {
                //bodycontent type
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                    return headers
                }
            }
        App.prefs.requestQueue.add(findUserRequest)

    }

}
