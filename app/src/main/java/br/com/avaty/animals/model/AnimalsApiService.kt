package br.com.avaty.animals.model

import br.com.avaty.animals.di.DaggerApiComponent
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

open class AnimalsApiService {

    @Inject
    lateinit var api : AnimalApi

    init {
        DaggerApiComponent.create().injectAnimal(this)
    }

    fun getApiKey(): Single<ApiKey> {
        return api.getApiKey()
    }

    fun getApiAnimals(key: String): Single<List<Animal>> {
        return api.getAnimals(key)
    }

}