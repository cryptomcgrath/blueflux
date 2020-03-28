package com.edwardmcgrath.blueflux.core

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

interface RxStore<T: State> {
    val stateStream: Observable<T>

    val state: T

    fun reduce(event: Event)
    
    companion object {
        fun <T: State> create(reducerFun: ReducerFun<T>): RxStore<T> = SimpleRxStoreImpl(reducerFun)
    }
}

/**
 * Defines a function that takes a State (current State) and an Event and returns the result State (new State)
 */
typealias ReducerFun<T> = (T?, Event?) -> T

private class  SimpleRxStoreImpl<T : State>(private val reducerFun: ReducerFun<T>): RxStore<T> {
    private val _stateSubject: BehaviorSubject<T> = BehaviorSubject.create()

    override val stateStream: Observable<T> = _stateSubject

    override val state: T
        get() = _stateSubject.value ?: reducerFun.invoke(null, null)

    override fun reduce(event: Event) {
        val newState = reducerFun.invoke(state, event)

        _stateSubject.onNext(newState)
    }
}