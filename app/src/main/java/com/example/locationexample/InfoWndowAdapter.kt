package com.example.locationexample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.gir_forest.view.*

class InfoWndowAdapter(val context : Context, val list : ArrayList<String>) : GoogleMap.InfoWindowAdapter{

    lateinit var contextApp : Context

    init{
        this.contextApp=context
    }


    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker?): View {
        var inflator=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var v =inflator.inflate(R.layout.gir_forest,null)

        var a = p0?.snippet


        v.tvgir.text=list[a!!.toInt()]

        return v
    }

}