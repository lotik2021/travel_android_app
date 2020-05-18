package ru.movista.di.component

import dagger.Component
import ru.movista.di.FragmentScope
import ru.movista.di.module.ChatModule
import ru.movista.presentation.chat.ChatFragment
import ru.movista.presentation.chat.ChatPresenter

@FragmentScope
@Component(
    dependencies = [MainComponent::class],
    modules = [ChatModule::class]
)
interface ChatComponent {

    fun inject(chatFragment: ChatFragment)
    fun inject(chatPresenter: ChatPresenter)
}