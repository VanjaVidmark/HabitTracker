package com.example.habittrackerfrontend

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel

actual open class BaseViewModel {

    actual val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun clear() {  // kom ihåg att köra cancel efter en view blir destroyed, undvika memory leak
        scope.cancel()
    }

}