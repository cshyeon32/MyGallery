package com.example.mygallery

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    var uris = mutableListOf<Uri>()
    override fun getItemCount(): Int {
        TODO("Not yet implemented")
        return uris.size
    }

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
        return PhotoFragment.newInstance(uris[position])
    }
}