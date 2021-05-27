package br.com.avaty.animals

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.avaty.animals.di.ApiModule
import br.com.avaty.animals.di.AppModule
import br.com.avaty.animals.di.DaggerViewModelComponent
import br.com.avaty.animals.model.Animal
import br.com.avaty.animals.model.AnimalsApiService
import br.com.avaty.animals.model.ApiKey
import br.com.avaty.animals.util.SharedPreferencesHelper
import br.com.avaty.animals.viewmodel.ListViewModel
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor


class ListViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var animalsApiService: AnimalsApiService

    @Mock
    lateinit var prefs: SharedPreferencesHelper


    val application = Mockito.mock(Application::class.java)

    //assim, é utilizado o construtor padrão...
    //var listViewModel = ListViewModel(application)

    //assim, é utilziado o constrututor do test
    var listViewModel = ListViewModel(application, true)


    private val key = "Test key"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        DaggerViewModelComponent.builder()
            .appModule(AppModule(application))
            .apiModule(ApiModuleTest(animalsApiService))
            .prefsModule(PrefsModuleTest(prefs))
            .build()
            .injectServiceInViewModel(listViewModel)
    }

    @Test
    fun getAnimalsSucess() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(key)
        val animal = Animal("cow", null, null, null, null, null, null)
        val animalList = listOf(animal)

        val testSingle = Single.just(animalList)

        Mockito.`when`(animalsApiService.getApiAnimals(key)).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(1, listViewModel.animals.value?.size)
        Assert.assertEquals(false, listViewModel.loadError.value)
        Assert.assertEquals(false, listViewModel.loading.value)
    }

    @Test
    fun getAnimalsFailure() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(key)
        val testSingle = Single.error<List<Animal>>(Throwable())
        val testKey = Single.just(ApiKey("OK", key))

        Mockito.`when`(animalsApiService.getApiAnimals(key)).thenReturn(testSingle)
        Mockito.`when`(animalsApiService.getApiKey()).thenReturn(testKey)

        listViewModel.refresh()

        Assert.assertEquals(null, listViewModel.animals.value)
        Assert.assertEquals(false, listViewModel.loading.value)
        Assert.assertEquals(true, listViewModel.loadError.value)
    }


    @Test
    fun getKeySucess() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val apiKey = ApiKey("OK", key)
        val testKey = Single.just(apiKey)

        Mockito.`when`(animalsApiService.getApiKey()).thenReturn(testKey)

        val animal = Animal("cow", null, null, null, null, null, null)
        val animalList = listOf(animal)

        val testSingle = Single.just(animalList)

        Mockito.`when`(animalsApiService.getApiAnimals(key)).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(1, listViewModel.animals.value?.size)
        Assert.assertEquals(false, listViewModel.loading.value)
        Assert.assertEquals(false, listViewModel.loadError.value)

    }

    @Test
    fun getKeyFailure() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val keySingle = Single.error<ApiKey>(Throwable())

        Mockito.`when`(animalsApiService.getApiKey()).thenReturn(keySingle)

        listViewModel.refresh()

        Assert.assertEquals(null, listViewModel.animals.value)
        Assert.assertEquals(false, listViewModel.loading.value)
        Assert.assertEquals(true, listViewModel.loadError.value)

    }


    @Before
    fun setupRxSchedulers() {
        val inmediate = object : Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, true, false)
            }
        }

        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> inmediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> inmediate }
    }
}