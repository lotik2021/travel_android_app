package ru.movista.presentation.tickets.placeselect

import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_place_select.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.domain.model.tickets.Place
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.utils.*
import ru.movista.utils.DIVIDER
import ru.movista.utils.EMPTY
import java.util.concurrent.TimeUnit

class PlaceSelectFragment : BaseFragment(), PlaceSelectView, OnBackPressedListener {

    companion object {
        private const val KEY_TYPE = "type"

        fun newInstance(citySelectTypeName: String): PlaceSelectFragment {
            return PlaceSelectFragment().apply {
                arguments = bundleOf(KEY_TYPE to citySelectTypeName)
            }
        }
    }

    private lateinit var placeSelectAdapter: PlaceSelectAdapter
    private var snackBar: Snackbar? = null

    @InjectPresenter
    lateinit var presenter: PlaceSelectPresenter

    @ProvidePresenter
    fun providePresenter(): PlaceSelectPresenter {
        return PlaceSelectPresenter(
            arguments?.getString(KEY_TYPE)
                ?: throw IllegalStateException("CitySelectPresenter require non-null citySelectTypeName")
        )
    }

    override fun getLayoutRes() = R.layout.fragment_place_select

    override fun initUI() {
        super.initUI()

        observeSearchTextChanges()
        setupRecyclerView()

        postOnMainThread {
            place_select_text.requestFocus()
            place_select_text.showSoftKeyboard()
        }

        back_button.setOnClickListener { presenter.onBackPressed() }
    }

    override fun showLoading() {
        place_select_loading.setVisible()
    }

    override fun hideLoading() {
        place_select_loading.setGone()
    }

    override fun showError(error: String) {
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun setToolbarText(fromText: String, toText: String) {
        val fromPlaceColor = getProperToolbarTextColor(fromText, resources.getString(R.string.from))
        val toPlaceColor = getProperToolbarTextColor(toText, resources.getString(R.string.to))

        from_place.setTextColor(getColor(requireContext(), fromPlaceColor))
        to_place.setTextColor(getColor(requireContext(), toPlaceColor))
        divider.setTextColor(getColor(requireContext(), toPlaceColor))

        from_place.text = fromText
        to_place.text = toText
        divider.text = String.DIVIDER
    }

    override fun showPlacesFound(places: List<Place>) {
        placeSelectAdapter.items = places
    }

    override fun showRecyclerView() {
        place_select_recycler_view.setVisible()
    }

    override fun hideRecyclerView() {
        place_select_recycler_view.setGone()
    }

    override fun clearEditText() {
        place_select_text.setText(String.EMPTY)
        place_select_text.requestFocus()
        place_select_text.showSoftKeyboard()
    }

    override fun hideKeyboard() {
        place_select_text.hideSoftKeyboard()
    }

    override fun showErrorMsg(message: String, action: View.OnClickListener) {
        postOnMainThreadWithDelay(200) {
            snackBar = place_select_content.infiniteSnackBar(
                message,
                getString(R.string.update),
                action
            )
        }
    }

    override fun hideErrorMsg() {
        snackBar?.dismiss()
    }

    override fun clearUI() {
        super.clearUI()
        place_select_recycler_view.adapter = null
        snackBar?.dismiss()
        snackBar = null
    }

    private fun observeSearchTextChanges() {
        addDisposable(
            place_select_text.textChanges()
                .debounce(350, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { presenter.onSearchTextChanged(it.toString()) }
        )
    }

    private fun setupRecyclerView() {
        placeSelectAdapter = PlaceSelectAdapter { presenter.onPlaceSelected(it) }
        place_select_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        place_select_recycler_view.adapter = placeSelectAdapter
    }

    private fun getProperToolbarTextColor(fullText: String, textToBeInactive: String): Int {
        return if (fullText.contains(textToBeInactive, true)) {
            R.color.inactive_text
        } else {
            R.color.blue_primary
        }
    }
}
