plugins {
    alias(libs.plugins.app.common.library)
    alias(libs.plugins.vanniktech.maven.publisher)
}

android {
    namespace = "com.android.base.core"
}

dependencies {
    // androidx
    api (libs.androidx.annotations)
    api (libs.androidx.arch.runtime)
    api (libs.androidx.arch.common)
    api (libs.androidx.lifecycle.common)
    api (libs.androidx.lifecycle.common.java8)
    api (libs.androidx.lifecycle.runtime.ktx)
    api (libs.androidx.lifecycle.livedata.core)
    api (libs.androidx.lifecycle.livedata.ktx)
    api (libs.androidx.lifecycle.viewmodel.ktx)
    api (libs.androidx.lifecycle.reactivestreams)
    api (libs.androidx.lifecycle.process)
    // kotlin
    api (libs.kotlin.stdlib)
    api (libs.kotlin.reflect)
    api (libs.kotlinx.coroutines)
    api (libs.kotlinx.coroutines.android)
    // log
    implementation (libs.jakewharton.timber)
}