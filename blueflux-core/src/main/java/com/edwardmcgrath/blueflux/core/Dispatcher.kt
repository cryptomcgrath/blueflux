package com.edwardmcgrath.blueflux.core

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 *    All dispatched events travel on this bus
 *    should only be observed by implementations of this interface and
 *    objects under the implementations control.
 *
 *    i.e.: An Android ViewModel can observe this dispatcher
 *
 *    Do not observe from android fragments, activities, etc
 */
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
    fun getEventBus(): Observable<Event>

    /**
     * Cleanup, should be called once when the dispatcher is no longer needed and is
     * going to be destroyed
     */
    fun onComplete()

    companion object {
        fun <T: State> create(store: RxStore<T>): Dispatcher = SimpleDispatcherImpl(store)
    }
}

private class SimpleDispatcherImpl(private val store: RxStore<*>): Dispatcher {
    // event bus to communicate across components
    private val _eventBus = PublishSubject.create<Event>()

    override fun post(event: Event) {
        store.reduce(event)
        _eventBus.onNext(event)
    }

    override fun getEventBus(): Observable<Event> = _eventBus

    override fun onComplete() = _eventBus.onComplete()
}