package ru.practicum.android.diploma.features.filters.presentation.ui.specialization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemSpecializationBinding
import ru.practicum.android.diploma.features.filters.presentation.model.ui.IndustryUI

class SpecializationSelectionAdapter(
    private val onItemClick: (IndustryUI, Int) -> Unit,
    private val onSelectionChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<SpecializationSelectionViewHolder>() {

    private var items: List<IndustryUI> = emptyList()
    private var selectedItem: IndustryUI? = null
    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    fun updateItems(newItems: List<IndustryUI>, savedSelectedItem: IndustryUI? = null) {
        items = newItems
        selectedItem = savedSelectedItem

        selectedItemPosition = if (savedSelectedItem != null) {
            items.indexOf(savedSelectedItem)
        } else {
            RecyclerView.NO_POSITION
        }

        notifyDataSetChanged()
    }

    fun updateSelectedItemPosition(position: Int) {
        val previousSelectedPosition = selectedItemPosition
        selectedItemPosition = position
        selectedItem = items[position]

        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedItemPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecializationSelectionViewHolder {
        val binding = ItemSpecializationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpecializationSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpecializationSelectionViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, item == selectedItem)
        holder.itemView.setOnClickListener {
            onItemClick(item, position)
        }
    }

    override fun getItemCount(): Int = items.size
}
