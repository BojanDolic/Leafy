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

    /**
     * Kreira kartice koje daju informacije kako tretirati bolesti
     *
     * @param usjev Usjev kojeg treba tretirati (informacije o bolesti i načinu tretiranja)
     */
    fun createBolestiCards(usjev: Usjev) {
        // Prolazimo kroz sva vremena primjene jer bolesti i broj primjena mogu biti prazne
        // (jedan preparat može se koristiti za skupinu bolesti koje su grupisane u opšti card)
        for(i in usjev.vrijemePrimjene.indices) {

            val bolestCard = DetaljiTretiranjaCardBinding.inflate(layoutInflater, binding.tretiranjePreparatContainer, false)
            val bolestCardView = bolestCard.root

            if(usjev.kolicinaPrimjene[i].isNotEmpty())
                bolestCard.tretiranjeKolicinaDesc.text = usjev.kolicinaPrimjene[i]
            else bolestCard.tretiranjeKolicinaContainer.visibility = View.GONE

            if(usjev.bolesti[i].isNotEmpty())
                bolestCard.tretiranjeBolestTitle.text = usjev.bolesti[i]
            else bolestCard.tretiranjeBolestTitle.text = usjev.imeUsjeva


            bolestCard.tretiranjeVrijemeDesc.text = usjev.vrijemePrimjene[i]

            // Ako postoji broj primjena prikazuje tekst
            // U protivnom skriva taj podatak
            if(usjev.brojPrimjena[i].isNotEmpty())
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