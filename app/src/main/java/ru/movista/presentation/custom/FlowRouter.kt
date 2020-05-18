package ru.movista.presentation.custom

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppScreen

class FlowRouter(private val router: Router) : Router() {

    fun replaceFlow(screen: SupportAppScreen) {
        router.replaceScreen(screen)
    }

    fun finishFlow() {
        router.exit()
    }
}