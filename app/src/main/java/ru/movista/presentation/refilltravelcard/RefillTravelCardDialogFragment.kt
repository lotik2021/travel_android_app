package ru.movista.presentation.refilltravelcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_refill_travel_card.*
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.utils.postOnMainThread
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RefillTravelCardDialogFragment : BaseDialogFragment() {

    companion object {

        private const val TAG = Screens.RefillTravelCardDialog.TAG

        private const val KEY_TRAVEL_CARDS = "travel_cards"

        fun newInstance(travelCardViewModel: ArrayList<TravelCardViewModel>): RefillTravelCardDialogFragment {
            val instance = RefillTravelCardDialogFragment()

            val bundle = Bundle()
            bundle.putSerializable(KEY_TRAVEL_CARDS, travelCardViewModel)
            instance.arguments = bundle

            return instance
        }
    }

    private lateinit var travelCards: ArrayList<TravelCardViewModel>

    @Inject
    lateinit var router: Router

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.init(TAG)
        Injector.refillTravelCardComponent?.inject(this)

        super.onCreate(savedInstanceState)

        val bundle = arguments ?: return
        travelCards = bundle.getSerializable(KEY_TRAVEL_CARDS) as ArrayList<TravelCardViewModel>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_refill_travel_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onDestroyView() {
        refill_travel_card_recycler_view.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Injector.destroy(TAG)
        super.onDestroy()
    }

    private fun initUI() {
        refill_travel_card_recycler_view.adapter = TravelCardsAdapter(travelCards) { position ->
            onTravelCardClicked(position)
        }

        refill_travel_card_recycler_view.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        val dividerItemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
            requireContext(),
            androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
        )

        val dividerImage = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        dividerImage?.let { dividerItemDecoration.setDrawable(it) }

        refill_travel_card_recycler_view.addItemDecoration(dividerItemDecoration)
    }

    private fun onTravelCardClicked(index: Int) {

        val chosenCard = travelCards[index]

        this.dismiss()

        postOnMainThread {
            when (chosenCard) {
                is TroikaViewModel -> {
                    router.navigateTo(Screens.RefillTroika(chosenCard))
                }
                is StrelkaViewModel -> {
                    router.navigateTo(Screens.RefillStrelka(chosenCard))
                }
            }.also {
                analyticsManager?.reportTravelCardChoice(chosenCard.cardId, chosenCard.cardTitle)
            }
        }
    }
}

