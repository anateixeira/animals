package br.com.avaty.animals.di

import br.com.avaty.animals.model.AnimalsApiService
import br.com.avaty.animals.viewmodel.ListViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, PrefsModule::class, AppModule::class])
interface ViewModelComponent {
    //onde a funcionalidade será injetada
    fun injectServiceInViewModel(list: ListViewModel)
}