package br.com.avaty.animals

import android.app.Application
import br.com.avaty.animals.di.PrefsModule
import br.com.avaty.animals.util.SharedPreferencesHelper

class PrefsModuleTest(val mockPrefs: SharedPreferencesHelper) : PrefsModule() {
    override fun provideSharedPreferences(app: Application): SharedPreferencesHelper {
        return mockPrefs
    }
}