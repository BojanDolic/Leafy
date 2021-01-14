package com.electroniccode.leafy.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.EditPostsPagerAdapter
import com.electroniccode.leafy.databinding.EditPostsFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class EditPostsFragment : Fragment() {

    private lateinit var viewModel: EditPostsViewModel

    private var _binding: EditPostsFragmentBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = arrayOf("Pitanja", "Proizvodi")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditPostsFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EditPostsViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EditPostsPagerAdapter(this)
        val viewpager = binding.editPostsViewpager
        viewpager.adapter = adapter


        TabLayoutMediator(binding.editPostsTablayout, viewpager) { tablayout, position ->
            tablayout.text = tabTitles[position]
            viewpager.setCurrentItem(tablayout.position, true)
        }.attach()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}