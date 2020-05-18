package ru.movista.presentation.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.common.OnBackPressedListener
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import javax.inject.Inject
import javax.inject.Named

class AuthFlowFragment : BaseFragment(), AuthFlowView, OnBackPressedListener {

    companion object {
        fun newInstance(): AuthFlowFragment = AuthFlowFragment()
    }

    private val currentFragment
        get() = childFragmentManager.findFragmentById(R.id.auth_flow_container) as? BaseFragment

    private lateinit var navigator: Navigator

    @InjectPresenter
    lateinit var presenter: AuthFlowPresenter

    @Inject
    @field:[Named("FlowNavigatorHolder")]
    lateinit var navigatorHolder: NavigatorHolder

    override fun getLayoutRes() = R.layout.fragment_auth_flow

    override fun onFragmentInject() {
        super.onFragmentInject()
        Injector.init(Screens.AuthFlow.TAG)
        Injector.authFlowComponent?.inject(this)
        navigator = createNavigator()
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        val currentFragment = currentFragment
        if (currentFragment != null && currentFragment is OnBackPressedListener && childFragmentManager.backStackEntryCount > 0) {
            currentFragment.onBackPressed()
        } else presenter.onBackPressed()
    }

    private fun createNavigator(): Navigator {
        return object : SupportAppNavigator(activity, childFragmentManager, R.id.auth_flow_container) {

            override fun setupFragmentTransaction(
                command: Command?,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction?
            ) {
                fragmentTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }
        }
    }
}
