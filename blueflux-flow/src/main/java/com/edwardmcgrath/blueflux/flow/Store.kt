package com.edwardmcgrath.blueflux.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface Store<T: State> {
    val stateStream: Flow<T>

    val state: T

    fun reduce(event: Event)

    companion object {
        fun <T: State> create(reducerFun: ReducerFun<T>): Store<T> = SimpleFlowStoreImpl(reducerFun)
    }
}

/**
 * Defines a function that takes a State (current State) and an Event and returns the result State (new State)
 */
typealias ReducerFun<T> = (T?, Event?) -> T

private class  SimpleFlowStoreImpl<T : State>(private val reducerFun: ReducerFun<T>): Store<T> {
    private val _stateFlow = MutableStateFlow(reducerFun.invoke(null,null))

    override val stateStream: StateFlow<T> = _stateFlow.asStateFlow()

    override val state: T
        get() = _stateFlow.value

    override fun reduce(event: Event) {
        val newState = reducerFun.invoke(state, event)

        _stateFlow.update {
            newState
        }
    }
}