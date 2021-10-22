package v.repositories.retrofitrepository
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProviders {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}