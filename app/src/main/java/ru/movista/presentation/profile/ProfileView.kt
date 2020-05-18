package ru.movista.presentation.profile

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ProfileView : MvpView {
    fun setLoaderVisibility(visible: Boolean)
    fun setRoutesContentVisibility(visible: Boolean)
    fun setProfileContent()
    fun setUnAuthContent()
    fun setInfoContentVisibility(visible: Boolean)
    fun setUserName(userName: String)
    fun setCity(userCity: String)
    fun setEditProfileBtnVisibility(visible: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun hideSnackBar()

    @StateStrategyType(SkipStrategy::class)
    fun showMsg(msg: String)

    @StateStrategyType(SkipStrategy::class)
    fun navigateToFeedBackMailApplication(
        whom: String,
        versionName: String,
        versionCode: Int,
        brand: String,
        model: String,
        osVersion: String
    )
}