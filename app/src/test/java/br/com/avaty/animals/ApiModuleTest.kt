package br.com.avaty.animals

import br.com.avaty.animals.di.ApiModule
import br.com.avaty.animals.model.AnimalsApiService

class ApiModuleTest(val mockService : AnimalsApiService) : ApiModule() {
    override fun provideAnimalApiService(): AnimalsApiService {
        return mockService
    }
}