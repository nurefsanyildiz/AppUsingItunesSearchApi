package com.example.myappusingitunessearchapi

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappusingitunessearchapi.models.ItunesResponse
import com.example.myappusingitunessearchapi.network.ItunesService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit.*

lateinit var drawableAnimation: AnimationDrawable
class MainActivity : AppCompatActivity() {
    val LIST_VIEW = "LIST_VIEW"
    val GRID_VIEW = "GRID_VIEW"
    var currentVisibleView: String = GRID_VIEW
    private var mProgressDialog: Dialog? = null
    private lateinit var mSharedPreferences: SharedPreferences
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getDataFromAPI(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        drawableAnimation = mainView.background as AnimationDrawable
        drawableAnimation.setEnterFadeDuration(1000)
        drawableAnimation.setExitFadeDuration(2000)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN
        fabSwitch.setOnClickListener { view ->
            if (currentVisibleView == LIST_VIEW) {
                fabSwitch.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_list))
                gridView()
            } else {
                fabSwitch.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_grid))
                listView()
            }
        }
    }

    private fun getDataFromAPI(query:String){
        if (Constants.isNetworkAvailable(this@MainActivity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: ItunesService = retrofit.create<ItunesService>(ItunesService::class.java)
            val listCall: Call<ItunesResponse> = service.getItunes(query)
            showCustomProgressDialog()
            listCall.enqueue(object : Callback<ItunesResponse> {
                @SuppressLint("SetTextI18n")

                override fun onResponse(response: Response<ItunesResponse>, retrofit: Retrofit) {
                    if (response.isSuccess) {
                        hideProgressDialog()
                        val itunesList: ItunesResponse = response.body()
                        val itunesResponseJsonString = Gson().toJson(itunesList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.ITUNES_RESPONSE_DATA, itunesResponseJsonString)
                        editor.apply()
                        setupUI()
                        rvItemsList.adapter = ItemAdapter(response.body(), this@MainActivity, mSharedPreferences)
                    } else {
                        val rc = response.code()
                        when (rc) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }
                override fun onFailure(t: Throwable) {
                    Log.e("Error", t.message.toString())
                    hideProgressDialog()
                }
            })
        }else {
            Toast.makeText(this@MainActivity, "No internet connection available.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listView() {
        currentVisibleView = LIST_VIEW
        rvItemsList.layoutManager = LinearLayoutManager(this)
    }

    private fun gridView() {
        currentVisibleView = GRID_VIEW
        rvItemsList.layoutManager = GridLayoutManager(this, 3)
    }

    override fun onStart() {
        super.onStart()
        drawableAnimation.start()
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    private fun setupUI(){
        val itunesResponseJsonString = mSharedPreferences.getString(Constants.ITUNES_RESPONSE_DATA, "")
        if (!itunesResponseJsonString.isNullOrEmpty()) {
            gridView()
        }
    }
}



