package jp.jiho.cmpprofiledemo.di

import jp.jiho.cmpprofiledemo.data.network.HttpClientProvider
import jp.jiho.cmpprofiledemo.data.repository.ProfileRepositoryImpl
import jp.jiho.cmpprofiledemo.domain.repository.ProfileRepository
import jp.jiho.cmpprofiledemo.presentation.editprofile.EditProfileViewModel
import jp.jiho.cmpprofiledemo.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.onClose

val networkModule = module {
    single { HttpClientProvider() } onClose { it?.close() }
    single { get<HttpClientProvider>().apiClient }
}

val repositoryModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::EditProfileViewModel)
}

val appModules = listOf(networkModule, repositoryModule, viewModelModule)
