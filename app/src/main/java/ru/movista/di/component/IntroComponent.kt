package ru.movista.di.component

import dagger.Component
import ru.movista.di.module.IntroModule
import ru.movista.di.FragmentScope
import ru.movista.presentation.intro.IntroPresenter
import ru.movista.presentation.intro.pager.SlidePagePresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class],
    modules = [IntroModule::class]
)
interface IntroComponent {
    fun inject(presenter: IntroPresenter)
    fun inject(presenter: SlidePagePresenter)
}