package ru.movista.presentation.intro.pager

import android.os.Bundle
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding3.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_slide_page.*
import moxy.presenter.InjectPresenter
import ru.movista.R
import ru.movista.presentation.base.BaseFragment
import ru.movista.presentation.utils.SpannableUtils
import ru.movista.presentation.utils.openInCustomBrowser
import ru.movista.presentation.utils.setInVisible
import ru.movista.presentation.utils.setVisible
import ru.movista.data.source.local.AGREEMENT_URL
import ru.movista.data.source.local.PERSONAL_DATA_POLICY_URL
import ru.movista.data.source.local.PRIVACY_URL
import ru.movista.utils.reportNullFieldError
import java.util.concurrent.TimeUnit

class SlidePageFragment : BaseFragment(), SlidePageView {

    companion object {
        private const val SLIDE_PAGE_TYPE_KEY = "SLIDE_PAGE_TYPE_KEY"

        fun newInstance(slidePageType: SlidePageType): SlidePageFragment {
            val bundle = Bundle()
            val fragment = SlidePageFragment()
            bundle.putSerializable(SLIDE_PAGE_TYPE_KEY, slidePageType)
            fragment.arguments = bundle
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SlidePagePresenter
    private lateinit var slidePageType: SlidePageType
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        slidePageType = arguments?.getSerializable(SLIDE_PAGE_TYPE_KEY) as SlidePageType
    }

    /*
     * Размер текста в макетах указан в dp,
     * чтобы он не изменился при увеличении размера шрифта из настроек Android'а
     */
    override fun getLayoutRes() = R.layout.fragment_slide_page

    override fun initUI() {
        when (slidePageType) {
            SlidePageType.SLIDE_PAGE_ONE -> setupPageOne()
            SlidePageType.SLIDE_PAGE_TWO -> setupPageTwo()
            SlidePageType.SLIDE_PAGE_THREE -> setupPageThree()
            SlidePageType.SLIDE_PAGE_FOUR -> setupPageFour() //TERM
            SlidePageType.SLIDE_PAGE_FIVE -> setupPageFive()
            SlidePageType.SLIDE_PAGE_SIX -> setupPageSix()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun setupPageOne() {
        context?.let { context ->
            base_description_group.setVisible()
            title.setText(R.string.welcome_to_movista_title)
            description.setText(R.string.welcome_to_movista_description)
            bg_slide_page_city.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_city_one))
            bg_slide_page_people.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_mobile_one))
        } ?: reportNullFieldError("context")
    }

    private fun setupPageTwo() {
        context?.let { context ->
            base_description_group.setVisible()
            title.setText(R.string.route_building_title)
            description.setText(R.string.route_building_description)
            bg_slide_page_city.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_city_two))
            bg_slide_page_people.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_mobile_two))
        } ?: reportNullFieldError("context")
    }

    private fun setupPageThree() {
        context?.let { context ->
            base_description_group.setVisible()
            title.setText(R.string.card_recharge_title)
            description.setText(R.string.card_recharge_description)
            bg_slide_page_city.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_city_three))
            bg_slide_page_people.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_mobile_three))
        } ?: reportNullFieldError("context")
    }

    private fun setupPageFour() {
        context?.let { context ->
            base_description_group.setVisible() /*geo_location_group.setVisible()*/
            title.setText(R.string.far_route_building_title)
            description.setText(R.string.far_route_building_description)
            bg_slide_page_city.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_city_four))
            bg_slide_page_people.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_mobile_four))
        } ?: reportNullFieldError("context")
    }

    private fun setupPageFive() {
        context?.let { context ->
            terms_group.setVisible()
            bg_slide_page_city.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_city_five))
            bg_slide_page_people.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_mobile_five))
            bg_slide_page_man.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_man_five))

            SpannableUtils.setLinkMovement(
                terms_of_use_description,
                R.string.terms_of_use_description,
                2,
                30
            ) { context.openInCustomBrowser(AGREEMENT_URL) }

            SpannableUtils.setLinkMovement(
                personal_data_policy_description,
                R.string.personal_data_policy_description,
                2,
                41
            ) { context.openInCustomBrowser(PERSONAL_DATA_POLICY_URL) }

            SpannableUtils.setLinkMovement(
                privacy_policy_description,
                R.string.privacy_policy_description,
                2,
                30
            ) { context.openInCustomBrowser(PRIVACY_URL) }

            observeTermsCheckedChange(terms_of_use_check_box)
            observeTermsCheckedChange(personal_data_policy_check_box)
            observeTermsCheckedChange(privacy_policy_check_box)
        } ?: reportNullFieldError("context")
    }

    private fun observeTermsCheckedChange(checkBox: CheckBox) {
        disposable.add(
            checkBox.checkedChanges()
                .filter { terms_of_use_check_box.isChecked && personal_data_policy_check_box.isChecked && privacy_policy_check_box.isChecked }
                .doOnNext { disabling_view.setVisible() }
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { presenter.onAllTermsSelected() }
        )
    }

    private fun setupPageSix() {
        context?.let { context ->
            geo_location_group.setVisible()
            title.setText("")
            description.setText(R.string.geo_description)
            bg_slide_page_city.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_city_six))
            bg_slide_page_people.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.onb_mobile_six))

            skip_btn.setOnClickListener { presenter.onSkipClick() }
            allow_btn.setOnClickListener { presenter.onGeoAllowClick() }
        } ?: reportNullFieldError("context")
    }
}