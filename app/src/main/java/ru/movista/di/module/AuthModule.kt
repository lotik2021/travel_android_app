package ru.movista.di.module

import dagger.Module
import dagger.Provides
import ru.movista.di.FlowScope
import ru.movista.domain.usecase.*

@Module
class AuthModule {

    @Provides
    @FlowScope
    fun provideAuthUseCase(authUseCase: AuthUseCase): IAuthUseCase {
        return authUseCase
    }

    @Provides
    @FlowScope
    fun provideAuthWithPhoneUseCase(authUseCase: AuthUseCase): AuthWithPhoneUseCase {
        return authUseCase
    }

    @Provides
    @FlowScope
    fun provideVerifyPhoneUseCase(authUseCase: AuthUseCase): VerifyPhoneUseCase {
        return authUseCase
    }

    @Provides
    @FlowScope
    fun provideRegisterUserUseCase(authUseCase: AuthUseCase): RegisterUserUseCase {
        return authUseCase
    }
}