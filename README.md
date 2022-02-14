# Android MVVM sample app that uses kotlin coroutines flow (without LiveData)
This is a sample app that uses kotlin coroutines [flow](https://kotlinlang.org/docs/reference/coroutines/flow.html).

It is MVVM Architecture without [LiveData](https://developer.android.com/topic/libraries/architecture/livedata).

There is a user search feature on Github.

## Screenshot
top|detail
:--:|:--:
<img src="images/screenshot1.png" width="250px" />|<img src="images/screenshot2.png" width="250px" />

## Architecture
<img src="images/architecture.png" width="250px" />

### ViewModel -> View
Use kotlin coroutines flow with [StateFlow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/).

After transformed to hot stream with [ViewModelScope](https://developer.android.com/topic/libraries/architecture/coroutines#viewmodelscope), bind to view with [LifecycleScope](https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope).

### View -> ViewModel
Call a ViewModel function, and emit to [MutableSharedFlow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-mutable-shared-flow/).

### View <-> ViewModel (2-way data binding)
Combine the above two.



## Libraries
* [kotlin](https://kotlinlang.org/)
  * [kotlin coroutines](https://github.com/Kotlin/kotlinx.coroutines)
* [androidx](https://developer.android.com/jetpack/androidx)
  * [appcompat](https://developer.android.com/jetpack/androidx/releases/appcompat)
  * [android ktx](https://developer.android.com/kotlin/ktx)
  * [constraintlayout](https://developer.android.com/reference/android/support/constraint/ConstraintLayout)
  * [lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)
* [material-components](https://github.com/material-components/material-components-android)
* [coil](https://github.com/coil-kt/coil)
* [koin](https://github.com/InsertKoinIO/koin)
* [retrofit](https://github.com/square/retrofit)
* [okhttp](https://github.com/square/okhttp)
* [moshi](https://github.com/square/moshi)
