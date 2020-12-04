package com.edwardmcgrath.blueflux.sample.hello

import androidx.lifecycle.ViewModel
import com.edwardmcgrath.blueflux.core.Dispatcher
import com.edwardmcgrath.blueflux.core.Event
import com.edwardmcgrath.blueflux.core.ReducerFun
import com.edwardmcgrath.blueflux.core.RxStore
import com.edwardmcgrath.blueflux.core.State

internal class HelloViewModel : ViewModel() {
    // the store holds our immutable state and all state changes are performed by the store
    // via its reducer function
    val store = RxStore.create(helloReducerFun)

    // the store listens to events that are posted to the dispatcher and makes state
    // changes as defined in the reducer function
    private val dispatcher = Dispatcher.create(store)

    init {
        dispatcher.post(HelloEvent.Init)
    }

    fun addItem(name: String) = dispatcher.post(HelloEvent.AddItemToList(name))
}

internal data class HelloState(
     val list : List<String> = emptyList()
): State

internal sealed class HelloEvent : Event {
    object Init : HelloEvent()
    class AddItemToList(val item: String) : HelloEvent()
}

private  val helloReducerFun: ReducerFun<HelloState> = { inState, event ->
    val state = inState ?: HelloState()

    when (event) {
        is HelloEvent.AddItemToList -> {
            state.copy(
                    list = state.list.toMutableList().apply {
                        add(event.item)
                    }
            )
        }

        HelloEvent.Init -> {
            HelloState()
        }

        else -> state
    }
}
