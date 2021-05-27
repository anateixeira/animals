package br.com.avaty.animals.di

import br.com.avaty.animals.model.AnimalsApiService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    //onde a funcionalidade será injetada
    fun injectAnimal(service: AnimalsApiService)

}