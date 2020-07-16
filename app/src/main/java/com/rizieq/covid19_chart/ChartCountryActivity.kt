package com.rizieq.covid19_chart

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.activity_chart_country.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChartCountryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COUNTRY = "EXTRA_COUNTRY"
        const val EXTRA_LATEST_UPDATE = "EXTRA_LATEST_UPDATE"
        const val EXTRA_NEW_DEATH = "EXTRA_NEW_DEATH"
        const val EXTRA_NEW_CONFIRMED = "EXTRA_NEW_CONFIRMED"
        const val EXTRA_NEW_RECOVERED = "EXTRA_NEW_RECOVERED"
        const val EXTRA_TOTAL_DEATH = "EXTRA_TOTAL_DEATH"
        const val EXTRA_TOTAL_CONFIRMED = "EXTRA_TOTAL_CONFIRMED"
        const val EXTRA_TOTAL_RECOVERED = "EXTRA_TOTAL_RECOVERED"
        const val EXTRA_COUNTRY_ID = "EXTRA_COUNTRY_ID"
    }


    private lateinit var tNegara: String
    private var dayCases = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_country)

        tNegara = intent.getStringExtra(EXTRA_COUNTRY)
        val tLatestUpdate = intent.getStringExtra(EXTRA_LATEST_UPDATE)
        val tNewDeath = intent.getStringExtra(EXTRA_NEW_DEATH)
        val tNewConfirmed = intent.getStringExtra(EXTRA_NEW_CONFIRMED)
        val tNewRecovered = intent.getStringExtra(EXTRA_NEW_RECOVERED)
        val tTotalDeath = intent.getStringExtra(EXTRA_TOTAL_DEATH)
        val tTotalConfirmed = intent.getStringExtra(EXTRA_TOTAL_CONFIRMED)
        val tTotalRecovered = intent.getStringExtra(EXTRA_TOTAL_RECOVERED)
        val tCountryId = intent.getStringExtra(EXTRA_COUNTRY_ID)

        latest_update.text = tNegara
        hasil_total_death_currently.text = tTotalDeath
        hasil_new_death_currently.text = tNewDeath
        hasil_total_recovery_currently.text = tTotalRecovered
        hasil_new_recovery_currently.text = tNewRecovered
        hasil_total_confirmed_currently.text = tTotalConfirmed
        hasil_new_confirmed_currently.text = tNewConfirmed
        Glide.with(this).load("https://www.countryflags.io/$tCountryId/shiny/64.png")
            .into(img_flag_country)


        chartDataView()
    }

    private fun chartDataView() {

        val okhttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/dayone/country/")
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getInfoService(tNegara).enqueue(object : Callback<List<InfoNegara>> {
            override fun onFailure(call: Call<List<InfoNegara>>, t: Throwable) {
                Toast.makeText(this@ChartCountryActivity,"Gagal menampilkan chart",Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<List<InfoNegara>>,
                response: Response<List<InfoNegara>>
            ) {
                if (response.isSuccessful) {

                    var getListDataCorona : List<InfoNegara> = response.body()!!

                    val barEntriesConfirmed : ArrayList<BarEntry> = ArrayList()
                    val barEntriesDeaths : ArrayList<BarEntry> = ArrayList()
                    val barEntriesRecovered : ArrayList<BarEntry> = ArrayList()
                    val barEntriesActive : ArrayList<BarEntry> = ArrayList()

                    var i = 0
                    while (i < getListDataCorona.size) {
                        for (a in getListDataCorona){

                            val barEntryConfirmed = BarEntry(i.toFloat(), a.Confirmed.toFloat())
                            val barEntryDeaths = BarEntry(i.toFloat(), a.Deaths.toFloat())
                            val barEntryRecovered = BarEntry(i.toFloat(), a.Recovered.toFloat())
                            val barEntryActive = BarEntry(i.toFloat(), a.Active.toFloat())

                            barEntriesConfirmed.add(barEntryConfirmed)
                            barEntriesDeaths.add(barEntryDeaths)
                            barEntriesRecovered.add(barEntryRecovered)
                            barEntriesActive.add(barEntryActive)


                            dayCases.add(a.Date)
                            i++
                        }

                    }

                    val barDataRecovered = BarDataSet(barEntriesRecovered,"Recovered")
                    val barDataDeaths = BarDataSet(barEntriesDeaths,"Deaths")
                    val barDataConfirmed = BarDataSet(barEntriesConfirmed,"Confirmed")
                    val barDataActive = BarDataSet(barEntriesActive,"Active")
                    barDataRecovered.setColor(Color.BLUE)
                    barDataDeaths.setColor(Color.RED)
                    barDataConfirmed.setColor(Color.GREEN)
                    barDataActive.setColor(Color.BLACK)

                    // Untuk menampilkan barchart X atau Horizontal
                    val x: XAxis = barChartView.xAxis
                    x.valueFormatter = IndexAxisValueFormatter(dayCases)
                    barChartView.axisLeft.axisMinimum = 0f
                    x.position = XAxis.XAxisPosition.BOTTOM
                    x.granularity = 1f
                    x.setCenterAxisLabels(true)
                    x.isGranularityEnabled = true

                    // Untuk menampilkan barchart Y atau Vertical
                    val data = BarData(barDataRecovered,barDataDeaths,barDataConfirmed,barDataActive)
                    barChartView.data = data

                    //Untuk mengatur keseluruhan view barchart
                    data.barWidth = 0.15f
                    barChartView.invalidate()
                    barChartView.setNoDataTextColor(Color.BLACK)
                    barChartView.setTouchEnabled(true)
                    barChartView.description.isEnabled = true
                    barChartView.xAxis.axisMinimum = 0f
                    barChartView.groupBars(0f,0.03f,0.02f)
                    barChartView.setVisibleXRangeMaximum(0f + barChartView.barData.getGroupWidth(0.3f,0.02f)*4f)


                }
            }

        })
    }
}
