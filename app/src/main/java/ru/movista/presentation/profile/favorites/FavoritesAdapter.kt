package ru.movista.presentation.profile.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.item_home_work_places.view.*
import kotlinx.android.synthetic.main.item_search_place.view.*
import ru.movista.R
import ru.movista.domain.model.FavoritePlace
import ru.movista.presentation.utils.inject
import ru.movista.presentation.utils.setInVisible
import ru.movista.presentation.utils.setVisible

class FavoritesAdapter(
    private val homeEditClickListener: (Long?) -> Unit,
    private val workEditClickListener: (Long?) -> Unit,
    private val deleteClickListener: (FavoritePlace) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val HOME_WORK_VIEW_TYPE = 0
        private const val TITLE_VIEW_TYPE = 1
        private const val FAVORITES_VIEW_TYPE = 2
        private const val BOTTOM_SPACE_VIEW_TYPE = 3
    }

    private val favorites: MutableList<FavoritePlace> = mutableListOf()
    private var homePlace: FavoritePlace? = null
    private var workPlace: FavoritePlace? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HOME_WORK_VIEW_TYPE -> HomeWorkHolder(inflater.inflate(R.layout.item_home_work_places, parent, false))
            TITLE_VIEW_TYPE -> HeaderHolder(inflater.inflate(R.layout.item_favorites_title, parent, false))
            BOTTOM_SPACE_VIEW_TYPE -> BottomSpaceHolder(
                inflater.inflate(
                    R.layout.item_favorites_bottom_space,
                    parent,
                    false
                )
            )
            else -> FavoritesHolder(inflater.inflate(R.layout.item_search_place, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (favorites.isEmpty()) 1 else favorites.size + 3 // 3 = homeWork + title + bottomSpace
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HOME_WORK_VIEW_TYPE
            1 -> TITLE_VIEW_TYPE
            itemCount - 1 -> BOTTOM_SPACE_VIEW_TYPE
            else -> FAVORITES_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HOME_WORK_VIEW_TYPE -> (viewHolder as HomeWorkHolder).bind()
            FAVORITES_VIEW_TYPE -> {
                val favoritePlaces = getFavoriteByPosition(position)
                favoritePlaces?.let { (viewHolder as FavoritesHolder).bind(it) }
            }
        }
    }

    fun updateItems(homePlace: FavoritePlace?, workPlace: FavoritePlace?, favorites: List<FavoritePlace>) {
        this.favorites.clear()
        this.favorites.addAll(favorites)
        this.workPlace = workPlace
        this.homePlace = homePlace
        notifyDataSetChanged()
    }

    fun addOtherFavorite(favorites: FavoritePlace) {
        this.favorites.add(0, favorites)
        notifyItemInserted(2)
    }

    fun updateHome(homePlace: FavoritePlace?) {
        this.homePlace = homePlace
        notifyItemChanged(0)
    }

    fun updateWork(workPlace: FavoritePlace?) {
        this.workPlace = workPlace
        notifyItemChanged(0)
    }

    fun removeOtherFavorite(favoritePlace: FavoritePlace) {
        val position = this.favorites.indexOf(favoritePlace)
        favorites.removeAt(position)

        if (favorites.isEmpty()) {
            notifyItemRangeRemoved(1, 3) // [title, title + lastFavoriteItem + bottomSpace]
        } else {
            notifyItemRemoved(position + 2) // 2 = homeWork + title
        }
    }

    private fun getFavoriteByPosition(position: Int): FavoritePlace? {
        return when (getItemViewType(position)) {
            FAVORITES_VIEW_TYPE -> favorites[position - 2] // 2 = homeWork + title
            else -> null
        }
    }

    private fun getFormattedAddress(context: Context, title: String?, description: String?): String? {
        var finalDescription = title

        if (description?.isNotBlank() == true) {
            finalDescription = context.getString(R.string.comma_splitted_text, finalDescription, description)
        }

        return finalDescription
    }

    class BottomSpaceHolder(view: View) : RecyclerView.ViewHolder(view)

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class HomeWorkHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            with(itemView) {
                if (homePlace != null) {
                    home_place_text.text = getFormattedAddress(
                        context,
                        homePlace?.placeAddress?.placeName,
                        homePlace?.placeAddress?.placeDescription
                    )
                } else {
                    home_place_text.setText(R.string.add_address)
                }

                if (workPlace != null) {
                    work_place_text.text = getFormattedAddress(
                        context,
                        workPlace?.placeAddress?.placeName,
                        workPlace?.placeAddress?.placeDescription
                    )
                } else {
                    work_place_text.setText(R.string.add_address)
                }

                home_edit.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        homeEditClickListener.invoke(homePlace?.id)
                    }
                }

                work_edit.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        workEditClickListener.invoke(workPlace?.id)
                    }
                }
            }
        }
    }

    inner class FavoritesHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(favoritePlace: FavoritePlace) {
            with(itemView) {
                if (adapterPosition == itemCount - 2) {
                    divider.setInVisible()
                } else {
                    divider.setVisible()
                }

                var placeTitle = favoritePlace.name

                if (placeTitle.isBlank()) {
                    placeTitle = favoritePlace.placeAddress.placeName
                }

                search_place_icon.inject(favoritePlace.icon, favoritePlace.iosIcon)
                search_place_title.text = placeTitle
                search_place_text.text = getFormattedAddress(
                    context,
                    favoritePlace.placeAddress.placeName,
                    favoritePlace.placeAddress.placeDescription
                )

                action_icon.setVisible()
                action_icon.setImageResource(R.drawable.ic_trash)
                action_icon.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        deleteClickListener.invoke(favoritePlace)
                    }
                }
            }
        }
    }
}