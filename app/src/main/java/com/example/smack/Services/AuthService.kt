package com.example.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.Utilities.URL_LOGIN
import com.example.smack.Utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

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
        Volley.newRequestQueue(context).add(registerRequest)
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
                userEmail = response.getString("user")
                authToken = response.getString("token")
                isLoggedIn = true
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }


        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not register user: $error")
            complete(false)

        }) {   //bodycontent type
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

        }
        Volley.newRequestQueue(context).add(loginRequest)

    }


}
