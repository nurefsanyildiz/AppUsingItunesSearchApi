package com.example.myappusingitunessearchapi

import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.example.myappusingitunessearchapi.models.ItunesResponse
import com.example.myappusingitunessearchapi.models.Results
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_details.*
import kotlinx.android.synthetic.main.items_row.view.*

class ItemAdapter(val repoList: ItunesResponse?, val activity: Activity, val mPrefs: SharedPreferences? )
    : RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_row, parent, false)
        val saveFile: SaveItems = SaveItems();
        saveFile.saveFile(repoList, mPrefs)
        return MyViewHolder(view, activity, repoList)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (repoList != null) {
            holder.bindRepo(repoList?.results[position])
        }
    }

    override fun getItemCount(): Int {
        if (repoList != null)
            return repoList.results.size
        return 0
    }

    class MyViewHolder(view: View, activity: Activity, repoList: ItunesResponse?) :
        RecyclerView.ViewHolder(view) {
        fun bindRepo(repo: Results?) {
            with(repo) {
                if (repo?.trackName != null) {
                    itemView.media_type.text = repo?.kind
                    itemView.country.text = repo?.country
                } else {
                    itemView.media_type.text = repo?.kind.orEmpty()
                    itemView.country.text = repo?.country.orEmpty()
                }
                itemView.genre.text = repo?.primaryGenreName.orEmpty()
                Picasso.get().load(repo?.artworkUrl100).error(R.drawable.ic_movie_filter_24dp)
                    .into(itemView.civ_icon)
            }
        }
        init {
            view.setOnClickListener() {
                val adapterPosition = getAdapterPosition()
                if (adapterPosition >= 0) {
                    if (repoList != null)
                        showDialog(activity, repoList?.results[adapterPosition])
                }
            }
        }

        fun showDialog(activity: Activity, item: Results) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.content_details)
            dialog.tv_artist.setText(item.artistName)
            dialog.tv_primaryGenreName.setText(item.primaryGenreName)
            if(item.trackName!=null) {
                dialog.tv_trackname.setText("Track: " + item.trackName)
                dialog.tv_trackPrice.setText("Price: " + item.trackPrice + "$")
            }
            else{
                dialog.tv_trackname.visibility =View.GONE
                dialog.tv_trackPrice.visibility =View.GONE
            }
            dialog.tv_collection_name.setText("Collection: " + item.collectionName)
            dialog.tv_collection_price.setText("Collection price: " + item.collectionPrice + "$")
            dialog.tv_country.setText("Country: " + item.country)
            dialog.tv_description.setText(item.longDescription)
            Picasso.get().load(item.artworkUrl100).error(R.drawable.ic_movie_filter_24dp).into(dialog.iv_icon)
            dialog.setCancelable(true)
            dialog.show()
        }
    }
    }





