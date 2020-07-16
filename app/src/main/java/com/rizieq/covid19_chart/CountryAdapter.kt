package com.rizieq.covid19_chart

import android.service.autofill.OnClickAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_chart_country.view.*
import kotlinx.android.synthetic.main.list_cotry.view.*
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CountryAdapter(
    private var negara: ArrayList<Negara>,
    private var clickListener: (Negara) -> Unit
) : RecyclerView.Adapter<CountryAdapter.ViewHolder>(), Filterable {


    var countryFilterList = ArrayList<Negara>()

    init {
        countryFilterList = negara
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_cotry, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countryFilterList.size
    }

    override fun onBindViewHolder(holder: CountryAdapter.ViewHolder, position: Int) {
        holder.bind(countryFilterList[position], clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(negara: Negara, clickListener: (Negara) -> Unit) {
            // Membuat widget pada layout
            val country: TextView = itemView.countryName
            val cTotalCase: TextView = itemView.country_total_case
            val cTotalRecovered: TextView = itemView.country_total_recovered
            val cTotalDeaths: TextView = itemView.country_total_deaths
            val flag: ImageView = itemView.img_flag_circle

            // Membuat format separator seribu
            val formatter: NumberFormat = DecimalFormat("#,####")

            // Meninjeksi data ke variable widget
            country.text = negara.Country
            cTotalCase.text = formatter.format(negara.TotalConfirmed?.toDouble())
            cTotalRecovered.text = formatter.format(negara.TotalRecovered?.toDouble())
            cTotalDeaths.text = formatter.format(negara.TotalDeaths?.toDouble())
            Glide.with(itemView)
                .load("https://www.countryflags.io/" + negara.CountryCode + "/flat/16.png")
                .into(flag)

            country.setOnClickListener { clickListener(negara) }
            cTotalCase.setOnClickListener { clickListener(negara) }
            cTotalRecovered.setOnClickListener { clickListener(negara) }
            cTotalDeaths.setOnClickListener { clickListener(negara) }


        }

    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                countryFilterList = if (charSearch.isEmpty()){
                    negara
                }else{
                    val resultList = ArrayList<Negara>()
                    for (row in negara) {
                        if (row.Country.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row) // Menyimpan data found
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?. values as ArrayList<Negara>
                notifyDataSetChanged()

            }

        }
    }
}