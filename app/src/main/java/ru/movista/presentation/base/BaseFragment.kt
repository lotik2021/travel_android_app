package ru.movista.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatFragment
import moxy.MvpView
import ru.movista.di.Injector
import ru.movista.presentation.common.OnBackPressedListener
import ru.movista.presentation.utils.HandlersHolder
import ru.movista.presentation.utils.longSnackbar
import timber.log.Timber

abstract class BaseFragment : MvpAppCompatFragment() {

    val analyticsManager = Injector.mainComponent?.getAnalyticsManager()

    private val viewCompositeDisposable = CompositeDisposable()

    private val systemBarsDecorator: SystemBarsDecorator by lazy { SystemBarsDecorator(activity) }

    override fun onAttach(context: Context) {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onCreate")
        onFragmentInject()

        super.onCreate(savedInstanceState)

        analyticsManager?.reportChangeCurrentScreenFromFragment(activity, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onCreateView")

        beforeCreateView()

        systemBarsDecorator.onFragmentCreateView(this)

        return inflater.inflate(getLayoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        afterCreate()
    }

    override fun onStart() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onDestroyView")
        HandlersHolder.removeAllCallbacks()
        clearUI()
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} onDetach")
        super.onDetach()
    }

    fun addDisposable(disposable: Disposable) {
        viewCompositeDisposable.add(disposable)
    }

    abstract fun getLayoutRes(): Int

    open fun beforeCreateView() {}

    open fun onFragmentInject() {}

    open fun afterCreate() {}

    open fun initUI() {}

    open fun clearUI() {
        clearDisposable()
    }

    // можем расшарить презентер, например, для доступа к текущему презентеру из activity
    open fun sharePresenter(): BasePresenter<MvpView>? = null

    fun setupToolbarBackButton(toolbar: Toolbar) {
        if (this is OnBackPressedListener) {
            toolbar.setNavigationOnClickListener { this.onBackPressed() }
        }
    }

    fun setTranslucentStatusBar() {
        systemBarsDecorator.setTranslucentStatusBar()
    }

    fun showMessage(contentView: View, @StringRes messageRes: Int) {
        contentView.longSnackbar(getString(messageRes))
    }

    fun showMessage(contentView: View, message: String) {
        contentView.longSnackbar(message)
    }

    private fun clearDisposable() = viewCompositeDisposable.clear()
}
