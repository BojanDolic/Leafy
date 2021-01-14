package com.electroniccode.leafy.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.electroniccode.leafy.fragments.EditPostsFragment
import com.electroniccode.leafy.fragments.ForumPostsFragment
import com.electroniccode.leafy.fragments.ProizvodiPostsFragment

class EditPostsPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {


    // 2 jer trenutno može mijenjati samo postove na forumu i objave za proizvode
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment
        when(position) {
            0 -> fragment = ForumPostsFragment()
            1 -> fragment = ProizvodiPostsFragment()
            else -> fragment = ForumPostsFragment()
        }
        return fragment
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}