package ru.movista.analytics.performers

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import ru.movista.App
import ru.movista.analytics.ActionAnalyticEvent
import ru.movista.analytics.ActionWithDataAnalyticEvent
import ru.movista.analytics.AnalyticEvent
import ru.movista.analytics.AnalyticEventPerformer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirabaseAnalyticEventPerformer @Inject constructor(app: App) : AnalyticEventPerformer {

    var firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(app)

    override fun perform(event: AnalyticEvent) {
        when (event) {
            is ActionAnalyticEvent -> {
                firebaseAnalytics.logEvent(event.name, null)
            }
            is ActionWithDataAnalyticEvent -> {
                val bundle = Bundle()
                event.data.forEach { (key, value) ->
                    when (value) {
                        is String -> bundle.putString(key, value)
                        is Int -> bundle.putInt(key, value)
                        else -> bundle.putString(key, value.toString())
                    }

                }
                firebaseAnalytics.logEvent(event.name, bundle)
            }
        }
    }
}