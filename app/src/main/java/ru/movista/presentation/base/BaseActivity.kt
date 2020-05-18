package ru.movista.presentation.base

import android.os.Bundle
import moxy.MvpAppCompatActivity
import ru.movista.R
import ru.movista.presentation.utils.HandlersHolder
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import timber.log.Timber

abstract class BaseActivity : MvpAppCompatActivity() {

    private lateinit var navigator: Navigator
    private lateinit var navigatorHolder: NavigatorHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} CREATE")

        onActivityInject()

        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        setupContentView()

        navigator = this.createNavigator()
        navigatorHolder = this.getNavigationHolder()

        afterCreate(savedInstanceState)
        initUI()
    }

    override fun onResume() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} RESUME")
        super.onResume()
    }

    override fun onResumeFragments() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} RESUME_FRAGMENTS")
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} PAUSE")
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onStart() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} START")
        super.onStart()
    }

    override fun onStop() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} STOP")
        HandlersHolder.removeAllCallbacks()
        super.onStop()
    }

    override fun onDestroy() {
        Timber.tag("Lifecycle callback").d("${this.javaClass.simpleName} DESTROY")
        super.onDestroy()
    }

    abstract fun getLayoutRes(): Int

    abstract fun onActivityInject()

    abstract fun getNavigationHolder(): NavigatorHolder

    abstract fun createNavigator(): Navigator

    open fun initUI() {}

    open fun afterCreate(savedInstanceState: Bundle?) {}

    private fun setupContentView() {
        setContentView(getLayoutRes())
    }
}