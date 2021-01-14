package com.electroniccode.leafy.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.BuyProizvodDetailsBinding
import com.electroniccode.leafy.util.UtilFunctions


class BuyProizvodDetailsFragment : Fragment() {

    private var _binding: BuyProizvodDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: BuyProizvodDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BuyProizvodDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.proizvod?.let { proizvod ->

            binding.apply {

                Glide.with(requireContext())
                    .load(proizvod.slikaProizvoda)
                    .into(buyDetailsImage)

                buyDetailsNaslovText.text = proizvod.naslovProizvoda
                buyDetailsOpisText.text = proizvod.opisProizvoda

                val ikona = UtilFunctions.getDrawableBasedOnName(proizvod.tipProizvoda, resources)

                Glide.with(requireContext())
                    .load(ikona)
                    .into(binding.buyDetailsVrstaIcon)

                buyDetailsCijenaText.text = String.format(
                    resources.getString(
                        R.string.evro_placeholder_text),
                    proizvod.cijenaProizvoda)

                buyDetailsVrstaText.text = proizvod.tipProizvoda

                buyDetailsTelefonText.text = proizvod.brojTelefona

                buyDetailsPozoviBtn.setOnClickListener {
                    openTelefonApp(proizvod.brojTelefona)
                }

            }

        }
    }

    fun openTelefonApp(brojTelefona: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:${brojTelefona}"))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}