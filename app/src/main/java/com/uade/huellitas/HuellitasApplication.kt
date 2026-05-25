package com.uade.huellitas

import android.app.Application
import com.uade.huellitas.di.AppContainer

class HuellitasApplication : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}
