package ru.movista.presentation.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView
import ru.movista.di.Injector
import timber.log.Timber

abstract class BasePresenter<V : MvpView> : MvpPresenter<V>() {

    private val compositeDisposable = CompositeDisposable()

    private val errorHandler: ErrorHandler by lazy { ErrorHandler() }

    abstract val screenTag: String

    init {
        this.onPresenterInject()
    }

    override fun onFirstViewAttach() {
        Timber.tag("Lifecycle callback:").d("${this.javaClass.simpleName} onFirstViewAttach")
        super.onFirstViewAttach()
    }

    override fun attachView(view: V) {
        Timber.tag("Lifecycle callback:").d("${this.javaClass.simpleName} attachView")
        super.attachView(view)
    }

    override fun onDestroy() {
        Timber.tag("Lifecycle callback:").d("${this.javaClass.simpleName} onDestroy")
        clearDisposable()
        Injector.destroy(screenTag)
        super.onDestroy()
    }

    open fun onPresenterInject() {}

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun getErrorDescription(apiError: Throwable) = errorHandler.getErrorDescription(apiError)

    private fun clearDisposable() = compositeDisposable.clear()
}