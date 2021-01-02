package com.electroniccode.leafy.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.electroniccode.leafy.fragments.EditPostsFragment
import com.electroniccode.leafy.fragments.ForumPostsFragment

class EditPostsPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {


    // 2 jer trenutno može mijenjati samo postove na forumu i objave za žitarice
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment
        when(position) {
            1 -> fragment = ForumPostsFragment()
            2 -> fragment = ForumPostsFragment()
            else -> fragment = ForumPostsFragment()
        }
        return fragment
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}