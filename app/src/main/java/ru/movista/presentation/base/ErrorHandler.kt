package ru.movista.presentation.base

import retrofit2.HttpException
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.utils.EMPTY
import java.io.IOException

class ErrorHandler {

    private val resources = Injector.mainComponent?.getResources()

    fun getErrorDescription(apiError: Throwable): String {
        val resources = resources ?: return String.EMPTY
        return when (apiError) {
            is HttpException -> {
                resources.getString(R.string.error_server_common)
            }
            is IOException -> {
                resources.getString(R.string.error_no_connection)
            }
            else -> resources.getString(R.string.error_unexpected_error)
        }
    }
}