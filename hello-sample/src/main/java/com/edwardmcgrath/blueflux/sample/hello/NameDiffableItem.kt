package com.edwardmcgrath.blueflux.sample.hello


internal class NameDiffableItem(private val key: Int, val name: String) : DiffableItem {

    override fun areContentsTheSame(other: DiffableItem): Boolean {
        return other is NameDiffableItem &&
                other.key == key &&
                other.name == name
    }

    override fun areItemsTheSame(other: DiffableItem): Boolean {
        return other is NameDiffableItem && other.key == key
    }
}