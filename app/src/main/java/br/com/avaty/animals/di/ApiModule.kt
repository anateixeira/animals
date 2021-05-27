package br.com.avaty.animals.di

import br.com.avaty.animals.model.AnimalApi
import br.com.avaty.animals.model.AnimalsApiService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
open class ApiModule {

    //CLEARTEXT communication to us-central1-apis-4674e.cloudfunctions.net not permitted by network security policy

    private var URL = "http://us-central1-apis-4674e.cloudfunctions.net"

    @Provides
    fun provideAnimalApi() : AnimalApi{
        return Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(AnimalApi::class.java)
    }

    @Provides
    open fun provideAnimalApiService() : AnimalsApiService{
        return AnimalsApiService()
    }
}