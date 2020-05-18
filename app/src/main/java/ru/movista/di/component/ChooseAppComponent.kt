package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.presentation.chooseapp.ChooseAppDialogFragment

@FragmentScope
@Component(
    dependencies = [MainComponent::class]
)
interface ChooseAppComponent {
    fun inject(chooseAppDialogFragment: ChooseAppDialogFragment)
}