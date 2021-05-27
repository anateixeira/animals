package br.com.avaty.animals.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.avaty.animals.di.AppModule
import br.com.avaty.animals.di.CONTEXT_APP
import br.com.avaty.animals.di.DaggerViewModelComponent
import br.com.avaty.animals.di.TypeOfContext
import br.com.avaty.animals.model.Animal
import br.com.avaty.animals.model.AnimalsApiService
import br.com.avaty.animals.model.ApiKey
import br.com.avaty.animals.util.SharedPreferencesHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ListViewModel(app: Application) : AndroidViewModel(app) {

    constructor(app: Application, test : Boolean = true): this(app){
        injected = true
    }
    /*
    * Lazy significa basicamente que o sistema não instanciará essa variável de dados de vida, a menos e quando for necessário.
    Portanto, ele só será criado se for necessário.
    E quando for necessário e não antes.
    E isso nos dá a vantagem de realmente adiar a criação dessa variável apenas na situação em que ela for necessária.
    E se nunca o usarmos em nosso código, ele não é criado corretamente.
    Isso torna nosso programa muito mais eficiente agora com dados de vida mutáveis, o que significa mutável. Bem, normalmente os dados de vida são observáveis que fornecem valores diferentes para quem está ouvindo
    que observável e mutável significa apenas que podemos alterar os valores em você sabe quando quisermos.
    *
    * */
    val animals by lazy { MutableLiveData<List<Animal>>() }
    val loadError by lazy { MutableLiveData<Boolean>() }
    val loading by lazy { MutableLiveData<Boolean>() }

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var apiService : AnimalsApiService
    //private val apiService = AnimalsApiService()

    @Inject
    @field:TypeOfContext(CONTEXT_APP)
    lateinit var prefs : SharedPreferencesHelper
    //private val prefs = SharedPreferencesHelper(getApplication())

    private var invalideApiKey = false

    private var injected = false


    //independente do que seja colocado no parametro, o init é rodado primeiro
    //quando tem init
    fun inject() {
        if(!injected){
            DaggerViewModelComponent.builder()
                .appModule(AppModule(getApplication()))
                .build()
                .injectServiceInViewModel(this)
        }

    }

    //Disposable - é um construtor nosso trabalho que basicamente leva o resultado de um observável
    fun refresh() {
        inject()
        loading.value = true
        invalideApiKey = false
        val key = prefs.getApiKey()
        if(key.isNullOrEmpty()){
            getKey()
        }else{
            getAnimals(key)
        }

    }

    fun hardRefresh() {
        inject()
        loading.value = true
        getKey()
    }

    fun getKey() {
        disposable.add(
            apiService.getApiKey()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ApiKey>() {
                    override fun onSuccess(value: ApiKey) {
                        value.let {
                            if (it.key.isNullOrEmpty()) {
                                loadError.value = true
                                loading.value = false
                            } else {
                                prefs.saveApiKey(it.key)
                                getAnimals(it.key)
                            }
                        }
                    }

                    override fun onError(e: Throwable?) {
                        Log.e("Animals", e?.message.toString())
                        loadError.value = true
                        loading.value = false
                    }

                })
        )
    }

    fun getAnimals(key: String) {
        disposable.add(
            apiService.getApiAnimals(key)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Animal>>() {
                    override fun onSuccess(values: List<Animal>) {
                        values.let {
                            loadError.value = false
                            animals.value = values
                            loading.value = false
                        }
                    }

                    override fun onError(e: Throwable?) {
                        Log.e("Animals", e?.message.toString())
                        if(!invalideApiKey){
                            invalideApiKey = true
                            getKey()
                        }else{
                            loadError.value = true
                            loading.value = false
                            animals.value = null
                        }
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}