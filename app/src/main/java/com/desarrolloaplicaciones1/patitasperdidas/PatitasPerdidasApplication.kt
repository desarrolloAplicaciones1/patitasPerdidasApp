package com.desarrolloaplicaciones1.patitasperdidas

import android.app.Application
import com.desarrolloaplicaciones1.patitasperdidas.di.AppContainer

class PatitasPerdidasApplication : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}
