package ru.movista.presentation.profile.favorites

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_profile.base_container
import kotlinx.android.synthetic.main.fragment_profile.loader
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.movista.R
import ru.movista.domain.model.FavoritePlace
import ru.movista.domain.model.UserFavoritesPlaces
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.utils.longSnackbar
import ru.movista.presentation.utils.setVisibility

class FavoritesFragment : BaseFragment(), FavoritesView, OnBackPressedListener {
    companion object {
        private const val KEY_TYPES = "user_favorite_places"

        fun newInstance(userFavoritesPlaces: UserFavoritesPlaces): FavoritesFragment {
            return FavoritesFragment().apply {
                arguments = bundleOf(KEY_TYPES to userFavoritesPlaces)
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: FavoritesPresenter

    @ProvidePresenter
    fun providePresenter(): FavoritesPresenter {
        return FavoritesPresenter(arguments?.getSerializable(KEY_TYPES) as UserFavoritesPlaces)
    }

    private lateinit var adapter: FavoritesAdapter

    override fun getLayoutRes() = R.layout.fragment_favorites

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = FavoritesAdapter(
            homeEditClickListener = { homePlaceId -> presenter.onHomeEditClick(homePlaceId) },
            workEditClickListener = { workPlaceId -> presenter.onWorkEditClick(workPlaceId) },
            deleteClickListener = { presenter.onFavoriteDeleteClick(it) }
        )
    }

    override fun initUI() {
        super.initUI()

        setupToolbarBackButton(favorites_toolbar)

        add_btn.setOnClickListener { presenter.onAddFavoritesClick() }
        favorites_recycler_view.adapter = adapter
        favorites_recycler_view.layoutManager = LinearLayoutManager(context)
    }

    override fun setLoaderVisibility(visible: Boolean) {
        loader.setVisibility(visible)
    }

    override fun setFavoritesContentVisibility(visible: Boolean) {
        favorites_recycler_view.setVisibility(visible)
        add_btn.setVisibility(visible)
    }

    override fun updateItems(homePlace: FavoritePlace?, workPlace: FavoritePlace?, favorites: List<FavoritePlace>) {
        adapter.updateItems(homePlace, workPlace, favorites)
    }

    override fun addOtherFavorite(favorites: FavoritePlace) {
        adapter.addOtherFavorite(favorites)
    }

    override fun updateHome(homePlace: FavoritePlace?) {
        adapter.updateHome(homePlace)
    }

    override fun updateWork(workPlace: FavoritePlace?) {
        adapter.updateWork(workPlace)
    }

    override fun removeOtherFavorite(favoritePlace: FavoritePlace) {
        adapter.removeOtherFavorite(favoritePlace)
    }

    override fun showMsg(msg: String) {
        base_container.longSnackbar(msg)
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }

    override fun clearUI() {
        super.clearUI()
        favorites_recycler_view.adapter = null
    }
}