package com.edwardmcgrath.blueflux.flow

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface Dispatcher {

    /**
     * posts a new event to this Dispatcher
     *
     * @param event The event to be posted
     */
    fun post(event: Event)

    /**
     * @return An Observable stream of the Events that are posted to this dispatcher
     */
    fun getEventBus(): Flow<Event>

    /**
     * Cleanup, should be called once when the dispatcher is no longer needed and is
     * going to be destroyed
     */
    fun onComplete()

    companion object {
        fun <T: State> create(store: Store<T>): Dispatcher = SimpleDispatcherImpl(store)
    }
}

private class SimpleDispatcherImpl(private val store: Store<*>): Dispatcher {
    // event bus to communicate across components
    private val _eventBus = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 1,
        BufferOverflow.DROP_OLDEST)

    override fun post(event: Event) {
        store.reduce(event)
        _eventBus.tryEmit(event)
    }

    override fun getEventBus() = _eventBus

    override fun onComplete() = Unit
}