package com.taghavi.expandablerecyclerview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.taghavi.expandablerecyclerview.databinding.ItemChildBinding
import com.taghavi.expandablerecyclerview.databinding.ItemParentBinding

class ExpandableCategoryAdapter(
    private val context: Context,
    private val itemList: ArrayList<Item>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentOpenedParent: Parent? = null
    var onItemClick: ((Child) -> Unit)? = null


    override fun getItemViewType(position: Int): Int {
        return itemList[position].getItemType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHILD -> {
                val bind =
                    ItemChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChildViewHolder(
                    bind
                )
            }
            else -> {
                val bind =
                    ItemParentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ParentViewHolder(bind)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            CHILD -> {
                val childViewHolder = (holder as ChildViewHolder)
                val childItem = itemList[position] as Child
                childViewHolder.bind(childItem)
            }
            else -> {
                val parentViewHolder = holder as ParentViewHolder
                val parentItem = itemList[position] as Parent
                parentViewHolder.bind(parentItem)
            }
        }
    }

    override fun getItemCount() = itemList.size

    inner class ParentViewHolder(val binding: ItemParentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parentItem: Parent) {
            updateViewState(parentItem)
            itemView.setOnClickListener {
                val startPosition = adapterPosition + 1
                val count = parentItem.childItems.size

                if (parentItem.isExpanded) {
                    itemList.removeAll(parentItem.childItems)
                    notifyItemRangeRemoved(startPosition, count)
                    parentItem.isExpanded = false
                    currentOpenedParent = null

                } else {
                    itemList.addAll(startPosition, parentItem.childItems)
                    notifyItemRangeInserted(startPosition, count)
                    parentItem.isExpanded = true

                    currentOpenedParent?.let { openParent ->
                        itemList.removeAll(openParent.childItems)
                        notifyItemRangeRemoved(
                            itemList.indexOf(openParent) + 1,
                            currentOpenedParent!!.childItems.size
                        )
                        currentOpenedParent?.isExpanded = false
                        notifyItemChanged(itemList.indexOf(openParent))
                    }

                    currentOpenedParent = parentItem
                }
                updateViewState(parentItem)
            }
        }

        private fun updateViewState(parentItem: Parent) {
            binding.apply {
                if (parentItem.selectedChild != null) {
                    title.text = parentItem.selectedChild?.title
                    title.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.parent_divider_color
                        )
                    )
                    return
                }
                if (parentItem.isExpanded) {
                    downArrow.rotation = 180f
                } else {
                    downArrow.rotation = 0f
                }
                title.text = parentItem.parent
            }
        }
    }

    inner class ChildViewHolder(val binding: ItemChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(childItem: Child) {
            binding.apply {
                title.text = childItem.title
                if (childItem.isSelected) {
                    title.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.sender_bubble_text_color
                        )
                    )
                } else {
                    title.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.parent_divider_color
                        )
                    )
                }
            }
            itemView.setOnClickListener {
                Log.d("Tag","child item: ${childItem.isSelected}")
                binding.apply {
                    if (childItem.isSelected) {
                        title.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.parent_divider_color
                            )
                        )
                        childItem.isSelected = false
                    } else {
                        title.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.sender_bubble_text_color
                            )
                        )
                        childItem.isSelected = true
                    }
                }

                onItemClick?.invoke(childItem)
            }
        }
    }
}