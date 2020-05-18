package ru.movista.presentation.placesearch

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_place_search.*
import kotlinx.android.synthetic.main.layout_alert_name.view.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.custom.DefaultAlertDialog
import ru.movista.presentation.utils.*
import ru.movista.presentation.viewmodel.FavoriteModeViewModel
import ru.movista.presentation.viewmodel.SearchPlaceViewModel
import ru.movista.presentation.viewmodel.UserPlacesViewModel
import ru.movista.utils.EMPTY
import java.util.concurrent.TimeUnit

class SearchPlaceFragment : BaseFragment(), SearchPlaceView, OnBackPressedListener {
    companion object {
        private const val KEY_FAVORITE_MODE = "favorite_mode"
        private const val KEY_USER_HISTORY_TAB = "userHistoryTab"

        fun newInstance(
            favoriteModeViewModel: FavoriteModeViewModel?,
            userHistoryTab: UserHistoryTab
        ): SearchPlaceFragment {
            return SearchPlaceFragment().apply {
                arguments = bundleOf(
                    KEY_FAVORITE_MODE to favoriteModeViewModel,
                    KEY_USER_HISTORY_TAB to userHistoryTab
                )
            }
        }
    }

    private lateinit var tabAdapter: TabAdapter
    private lateinit var searchPlaceAdapter: SearchPlaceAdapter

    @InjectPresenter
    lateinit var presenter: SearchPlacePresenter

    @ProvidePresenter
    fun providePresenter(): SearchPlacePresenter {
        val userHistoryTab = arguments?.getSerializable(KEY_USER_HISTORY_TAB) as UserHistoryTab? ?: UserHistoryTab.RECENT
        val favoriteModeViewModel = arguments?.getSerializable(KEY_FAVORITE_MODE) as FavoriteModeViewModel?

        return SearchPlacePresenter(favoriteModeViewModel, userHistoryTab)
    }

    override fun getLayoutRes() = R.layout.fragment_place_search

    override fun onFragmentInject() {
        Injector.init(Screens.PlaceSearch.TAG)
        Injector.searchPlaceComponent?.inject(this)
    }


    override fun initUI() {
        super.initUI()

        observeSearchTextChanges()

        initRecyclerView()

        place_search_back.setOnClickListener { presenter.onBackClicked() }

        place_search_clear.setOnClickListener { place_search_text.setText(String.EMPTY) }

        place_search_refresh_btn.setOnClickListener {
            hideAllContent()
            place_search_loading.setVisible()
            presenter.onRetryClicked()
        }

        place_search_previous.setVisible()

        initViewPager()

        place_search_text.requestFocus()
        place_search_text.showSoftKeyboard()
    }

    override fun hideHistoryViewPager() {
        viewPager.setGone()
    }

    override fun showFavoriteNameAlert(placeHolder: String) {
        val alertContent = LayoutInflater.from(requireContext()).inflate(R.layout.layout_alert_name, null)
        alertContent.name.hint = placeHolder

        DefaultAlertDialog(requireContext())
            .setTitle(R.string.enter_name)
            .setView(alertContent)
            .setPositiveButton(R.string.ok) { presenter.onEnterNameClick(alertContent.name.text.toString().trim()) }
            .setNegativeButton(R.string.cancel) { }
            .build()
            .show()
    }

    override fun showSearchResult(searchPlaceViewModelList: List<SearchPlaceViewModel>) {
        place_search_label.setVisible()
        place_search_label.text = "РЕЗУЛЬТАТЫ ПОИСКА"
        place_search_recycler_view.setVisible()
        searchPlaceAdapter.items = searchPlaceViewModelList
        place_search_recycler_view.scrollToPosition(0)
    }

    override fun hideKeyboard() {
        place_search_text.hideSoftKeyboard()
    }

    override fun showNoResults() {
        place_search_no_results_layout.setVisible()
    }

    override fun showCommonError() {
        place_search_common_error_layout.setVisible()
    }

    override fun hideLoading() {
        place_search_loading.setGone()
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun showPreviousResultsLoading() {
        place_search_previous_loading.setVisible()
    }

    override fun hidePreviousResultsLoading() {
        place_search_previous_loading.setGone()
    }

    override fun showContentLoading() {
        content_loader.setVisible()
    }

    override fun hideContentLoading() {
        content_loader.setGone()
    }

    override fun showPreviousResults() {
        place_search_previous.setVisible()
    }

    override fun hidePreviousResults() {
        place_search_previous.setGone()
    }

    override fun setSearchPlaceResultsVisibility(visible: Boolean) {
        place_search_recycler_view.setVisibility(visible)
        place_search_label.setVisibility(visible)
    }

    override fun showUserFavourites(results: List<UserPlacesViewModel>) {
        val favouritePlacesFragment = tabAdapter.getItem(1) as? FavouritePlacesFragment
        favouritePlacesFragment ?: return

        favouritePlacesFragment.showUserHistory(results) {
            presenter.onHistoryPlaceClicked(UserHistoryTab.FAVORITES, it)
        }
    }

    override fun showUserRecent(results: List<UserPlacesViewModel>) {
        val recentPlacesFragment = tabAdapter.getItem(0) as? RecentPlacesFragment
        recentPlacesFragment ?: return

        recentPlacesFragment.showUserHistory(results) {
            presenter.onHistoryPlaceClicked(UserHistoryTab.RECENT, it)
        }
    }

    override fun showMsg(msg: String) {
        container.longSnackbar(msg)
    }

    override fun clearUI() {
        super.clearUI()
        place_search_recycler_view.adapter = null
    }

    private fun observeSearchTextChanges() {
        addDisposable(
            place_search_text.textChanges()
                .doOnEach {
                    // делаем это без задержки
                    hideAllContent()
                    if (it.value.isNullOrEmpty()) {
                        place_search_loading.setGone()
                        place_search_previous.setVisible()
                    } else {
                        place_search_loading.setVisible()
                    }
                }
                .debounce(350, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { presenter.onSearchTextChanged(it.toString()) }
        )
    }

    private fun hideAllContent() {
        place_search_label.setGone()
        place_search_recycler_view.setGone()
        place_search_no_results_layout.setGone()
        place_search_common_error_layout.setGone()
        place_search_previous.setGone()
    }

    private fun initRecyclerView() {
        searchPlaceAdapter = SearchPlaceAdapter { presenter.onPlaceClicked(it) }

        place_search_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        place_search_recycler_view.adapter = searchPlaceAdapter
    }

    private fun initViewPager() {
        tabAdapter = TabAdapter(childFragmentManager)

        val savedFragments = childFragmentManager.fragments

        val favouritePlacesFragment = savedFragments
            .filterIsInstance<FavouritePlacesFragment>()
            .firstOrNull()

        val recentPlacesFragment = savedFragments
            .filterIsInstance<RecentPlacesFragment>()
            .firstOrNull()

        // после смерти процесса фрагменты сохранены в childFragmentManager. Берем их, если существуют, иначе создаем.
        recentPlacesFragment?.let { tabAdapter.addFragment(it, resources.getString(R.string.history)) }
            ?: tabAdapter.addFragment(RecentPlacesFragment(), resources.getString(R.string.history))

        favouritePlacesFragment?.let { tabAdapter.addFragment(it, resources.getString(R.string.favourite)) }
            ?: tabAdapter.addFragment(FavouritePlacesFragment(), resources.getString(R.string.favourite))

        viewPager.adapter = tabAdapter
        tab_layout.setupWithViewPager(viewPager)
    }

    override fun setCurrentPage(userHistoryTab: UserHistoryTab) {
        when (userHistoryTab) {
            UserHistoryTab.RECENT -> viewPager.currentItem = 0
            UserHistoryTab.FAVORITES -> viewPager.currentItem = 1
        }
    }

    class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val fragments = ArrayList<Fragment>()
        private val fragmentTitles = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentTitles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitles[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }
}