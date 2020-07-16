package com.rizieq.covid19_chart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Time
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {


        lateinit var adapters:CountryAdapter

        private var ascending = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getCountry()

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapters.filter.filter(newText)
                return false
            }

        })

        sequence.setOnClickListener{
            sequence(ascending)
            ascending = !ascending
        }
    }


    private fun sequence(ascending: Boolean){
        recyclerViewCountry.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            if (ascending){
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true

            } else {
                (layoutManager as LinearLayoutManager).reverseLayout = false
                (layoutManager as LinearLayoutManager).stackFromEnd = false
            }
            adapter = adapters
        }
    }

    private fun getCountry(){
        val okhttp = OkHttpClient().newBuilder()
            .connectTimeout(15,TimeUnit.SECONDS)
            .readTimeout(15,TimeUnit.SECONDS)
            .writeTimeout(15,TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getAllNegara().enqueue(object : Callback<AllNegara>{
            override fun onFailure(call: Call<AllNegara>, t: Throwable) {
                progress_Bar.visibility = View.GONE
                Toast.makeText(this@MainActivity,"Data Not Found",Toast.LENGTH_SHORT).show()
                // Server not found
                // Jaringan jelek
            }

            override fun onResponse(call: Call<AllNegara>, response: Response<AllNegara>) {
                if(response.isSuccessful){
                    progress_Bar.visibility = View.GONE
                    Toast.makeText(this@MainActivity,"Data Success",Toast.LENGTH_LONG).show()
                    val globalListCorona = response.body()?.Global
                    confirmed_globe.text = globalListCorona?.TotalConfirmed
                    recovered_globe.text = globalListCorona?.TotalRecovered
                    deaths_globe.text = globalListCorona?.TotalDeaths
                    recyclerViewCountry.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        adapters = CountryAdapter(response.body()?.Countries as ArrayList<Negara>){
                            negara -> itemClicked(negara)
                        }
                        adapter = adapters
                    }

                }else{
                    progress_Bar.visibility = View.GONE
                    Toast.makeText(this@MainActivity,"Data Not Found",Toast.LENGTH_LONG).show()
                    // End point salah

                }
            }

        })
    }


    private fun itemClicked(negara: Negara){
        val moveWithData = Intent(this@MainActivity, ChartCountryActivity::class.java)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_COUNTRY, negara.Country)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_LATEST_UPDATE, negara.Date)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_NEW_DEATH, negara.NewDeaths)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_NEW_CONFIRMED, negara.NewConfirmed)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_NEW_RECOVERED, negara.NewRecovered)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_TOTAL_DEATH, negara.TotalDeaths)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_TOTAL_CONFIRMED, negara.TotalConfirmed)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_TOTAL_RECOVERED, negara.TotalRecovered)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_COUNTRY_ID, negara.CountryCode)
        startActivity(moveWithData)
    }

}
