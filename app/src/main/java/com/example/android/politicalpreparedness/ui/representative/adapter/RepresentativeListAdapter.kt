package com.example.android.politicalpreparedness.ui.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.RvRepresentativeItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.ui.representative.model.Representative

class RepresentativeListAdapter(val clickListener: RepresentativeListener): ListAdapter<Representative, RepresentativeViewHolder>(representativeDiffCallback){

    //DONE: Create RepresentativeDiffCallback
    companion object {

        private val representativeDiffCallback = object : DiffUtil.ItemCallback<Representative>() {
            override fun areItemsTheSame(
                oldItem: Representative,
                newItem: Representative
            ): Boolean {
                return oldItem.official.name == newItem.official.name
            }

            override fun areContentsTheSame(
                oldItem: Representative,
                newItem: Representative
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

class RepresentativeViewHolder(val binding: RvRepresentativeItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Representative, listener: RepresentativeListener) {
        binding.representative = item
        binding.ivRepresentativePhoto.setImageResource(R.drawable.ic_profile)

        //DONE: Show social links ** Hint: Use provided helper methods

        val channels = item.official.channels
        val urls = item.official.urls

        channels?.let {  channel ->
            showSocialLinks(channel)

            binding.ivFacebook.setOnClickListener {
                val facebookUrl = getFacebookUrl(channel)
                facebookUrl?.let { facebook ->
                    listener.navigateToBrowser(facebook)
                }
            }

            binding.ivTwitter.setOnClickListener {
                val twitterUrl = getTwitterUrl(channel)
                twitterUrl?.let { twitter ->
                    listener.navigateToBrowser(twitter)
                }
            }
        }


        //DONE: Show www link ** Hint: Use provided helper methods

        urls?.let { www ->
            showWWWLinks(www)
            binding.ivWww.setOnClickListener {
                listener.navigateToBrowser(www.first())
            }
        }
        binding.executePendingBindings()
    }

    //DONE: Add companion object to inflate ViewHolder (from)

    companion object {
        fun from(parent: ViewGroup) : RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RvRepresentativeItemBinding
                .inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(binding)
        }
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) { enableLink(binding.ivFacebook, facebookUrl) }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) { enableLink(binding.ivTwitter, twitterUrl) }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.ivTwitter, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}



//DONE: Create RepresentativeListener

interface RepresentativeListener {
    fun navigateToBrowser(url: String)
}