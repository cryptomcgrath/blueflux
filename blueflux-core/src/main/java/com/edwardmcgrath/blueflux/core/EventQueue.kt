package com.edwardmcgrath.blueflux.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * A lifecycle aware queue used to relay events to Android fragments and/or activities
 */
interface EventQueue {

    /**
     * Adds an event to the queue to be relayed to the observing fragment or activity
     *
     * @param event - event to be added to the queue
     *
     */
    fun post(event: Event)

    /**
     * Handles queued events while respecting the lifecycle owner's active state
     *
     * @param owner - The lifecycle owner to respect (i.e., an Android Activity or Fragment)
     *
     * @param handlerFun - A function which handles its own event parameter
     */
    fun handleEvents(owner: LifecycleOwner, handlerFun: (event: Event) -> Unit)

    companion object {
        fun create(): EventQueue = EventQueueLiveDataWrapperImpl()
    }
}

/**
 * A livedata wrapper implementation of EventQueue interface
 */
private class EventQueueLiveDataWrapperImpl: EventQueue {
    private val eventQueueLiveData = MutableLiveData<InternalEventQueue>().apply { value = InternalEventQueue() }

    override fun post(event: Event) {
        eventQueueLiveData.value?.let {
            it.add(event)
            this.eventQueueLiveData.value = it
        }
    }

    override fun handleEvents(owner: LifecycleOwner, handlerFun: (event: Event) -> Unit) {
        eventQueueLiveData.observe(owner, Observer {
            it?.let { queue ->
                while(queue.hasEvents()) {
                    val e = queue.remove()
                    handlerFun(e)
                }
            }
        })
    }
}

private class InternalEventQueue {
    private val queue = ConcurrentLinkedQueue<Event>()

    fun add(e: Event) {
        queue.add(e)
    }

    fun hasEvents() = !queue.isEmpty()

    fun remove(): Event = queue.remove()
}
