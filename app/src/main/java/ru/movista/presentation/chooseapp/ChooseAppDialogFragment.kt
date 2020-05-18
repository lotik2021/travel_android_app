package ru.movista.presentation.chooseapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.dialog_choose_app.*
import kotlinx.android.synthetic.main.item_app_to_choose.view.*
import org.jetbrains.anko.image
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BaseDialogFragment
import ru.movista.presentation.utils.postOnMainThread
import ru.movista.presentation.viewmodel.CarDeeplinkViewModel
import ru.movista.utils.reportNullFieldError
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class ChooseAppDialogFragment : BaseDialogFragment() {

    companion object {
        private const val TAG = Screens.ChooseAppDialog.TAG

        private const val KEY_DEEPLINKS = "deeplinks"

        fun getInstance(carDeeplinksViewModel: ArrayList<CarDeeplinkViewModel>): ChooseAppDialogFragment {
            val instance = ChooseAppDialogFragment()

            val bundle = Bundle()
            bundle.putSerializable(KEY_DEEPLINKS, carDeeplinksViewModel)
            instance.arguments = bundle

            return instance
        }
    }

    private lateinit var carDeeplinksViewModel: ArrayList<CarDeeplinkViewModel>

    @Inject
    lateinit var router: Router

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.init(TAG)
        Injector.chooseAppComponent?.inject(this)
        super.onCreate(savedInstanceState)

        val bundle = arguments ?: return
        carDeeplinksViewModel = bundle.getSerializable(KEY_DEEPLINKS) as ArrayList<CarDeeplinkViewModel>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_choose_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onDestroyView() {
        apps_to_choose_grid_view.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Injector.destroy(TAG)
        super.onDestroy()
    }

    private fun initUI() {
        apps_to_choose_grid_view.adapter = object : BaseAdapter() {


            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                val grid = convertView ?: layoutInflater.inflate(R.layout.item_app_to_choose, parent, false)

                grid.app_to_choose_text.text = carDeeplinksViewModel[position].carDeepLinkTitle

                context?.let {
                    val checkExistence = resources.getIdentifier(
                        carDeeplinksViewModel[position].carDeepLinkImage,
                        "drawable",
                        it.packageName
                    )

                    if (checkExistence != 0) {
                        grid.app_to_choose_icon.image = resources.getDrawable(checkExistence, null)
                    } else {
                        // stub
                    }
                } ?: reportNullFieldError("context")

                return grid
            }

            override fun getItem(position: Int) = carDeeplinksViewModel[position]

            override fun getItemId(position: Int) = position.toLong()

            override fun getCount() = carDeeplinksViewModel.size
        }

        apps_to_choose_grid_view.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            onNavigationAppChosen(position)
        }
    }

    private fun onNavigationAppChosen(index: Int) {
        Timber.i("onNavigationAppChosen")

        val carDeeplinkViewModel = carDeeplinksViewModel[index]

        this.dismiss()

        postOnMainThread {
            router.navigateTo(Screens.ExternalAppByUrl(carDeeplinkViewModel.carDeepLinkUrl))
                .also { analyticsManager?.reportStartCarNavigation(carDeeplinkViewModel.carDeepLinkTitle) }
        }
    }
}