package com.edwardmcgrath.blueflux.sample.hello

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edwardmcgrath.blueflux.flow.Store
import kotlinx.coroutines.launch

internal class HelloAdapter(
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val store: Store<HelloState>
): RecyclerView.Adapter<HelloAdapter.NameItemViewHolder>() {

    private val itemModels: List<DiffableItem>
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        lifecycleOwner.lifecycleScope.launch {
            // subscribe and react to state changes
            store.stateStream.collect {
                differ.submitList(buildList(it))
            }
        }
    }

    private fun buildList(state: HelloState): List<DiffableItem> {
        return state.list.mapIndexed { idx, it ->
                NameDiffableItem(
                        key = idx,
                        name = it
                )
        }.ifEmpty {
            listOf(NameDiffableItem(
                    key = 1,
                    name = context.getString(R.string.empty_list_message)
            ))
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int = itemModels.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameItemViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_name, parent, false)
        return NameItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: NameItemViewHolder, position: Int) {
        (itemModels[position] as? NameDiffableItem)?.let {
            holder.nameTextView.text = it.name
        }
    }

    class NameItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView

        init {
            nameTextView = view.findViewById(R.id.name_text)
        }
    }
}

private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<DiffableItem>() {
    override fun areItemsTheSame(oldItem: DiffableItem, newItem: DiffableItem): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: DiffableItem, newItem: DiffableItem): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}

interface DiffableItem {

    fun areContentsTheSame(other: DiffableItem): Boolean

    fun areItemsTheSame(other: DiffableItem): Boolean

}
