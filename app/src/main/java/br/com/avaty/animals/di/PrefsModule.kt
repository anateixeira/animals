package br.com.avaty.animals.di

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import br.com.avaty.animals.util.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

//classe em open para outras classes terem acesso
@Module
open class PrefsModule {

    //Singleton - unica instancia
    @Provides
    @Singleton
    @TypeOfContext(CONTEXT_APP)
    open fun provideSharedPreferences(app: Application): SharedPreferencesHelper {
        return SharedPreferencesHelper(app)
    }

    //qualifier
    @Provides
    @Singleton
    @TypeOfContext(CONTEXT_ACTIVITY)
    open fun provideActivitySharedPreferences(activity: AppCompatActivity): SharedPreferencesHelper {
        return SharedPreferencesHelper(activity)
    }
}

const val CONTEXT_APP = "Application context"
const val CONTEXT_ACTIVITY = "Application activity"

@Qualifier
annotation class TypeOfContext(val type: String)