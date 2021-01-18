package com.electroniccode.leafy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.DetaljiTretiranjaCardBinding
import com.electroniccode.leafy.databinding.TretiranjeUsjevaDetailsFragmentBinding
import com.electroniccode.leafy.models.Usjev

class TretiranjeUsjevaDetails : Fragment() {

    private var _binding: TretiranjeUsjevaDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: TretiranjeUsjevaDetailsArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TretiranjeUsjevaDetailsFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.usjev.let {
            createBolestiCards(it)
        }
    }

    fun createBolestiCards(usjev: Usjev) {
        // Prolazimo kroz sve kolicine primjena jer bolesti mogu biti prazne
        // (jedan preparat može se koristiti za skupinu bolesti koje su grupisane u opšti card)
        for(i in usjev.kolicinaPrimjene.indices) {

            val bolestCard = DetaljiTretiranjaCardBinding.inflate(layoutInflater, binding.tretiranjePreparatContainer, false)
            val bolestCardView = bolestCard.root

            if(usjev.bolesti.isNotEmpty())
                bolestCard.tretiranjeBolestTitle.text = usjev.bolesti[i]
            else bolestCard.tretiranjeBolestTitle.text = usjev.imeUsjeva

            bolestCard.tretiranjeKolicinaDesc.text = usjev.kolicinaPrimjene[i]
            bolestCard.tretiranjeVrijemeDesc.text = usjev.vrijemePrimjene[i]

            // Ako postoji broj primjena prikazuje tekst
            // U protivnom skriva taj podatak
            if(usjev.brojPrimjena.isNotEmpty())
                bolestCard.tretiranjeBrojprimjeneDesc.text = usjev.brojPrimjena[i]
            else bolestCard.tretiranjeBrojprimjeneContainer.visibility = View.GONE

            binding.tretiranjePreparatContainer.addView(bolestCardView)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}