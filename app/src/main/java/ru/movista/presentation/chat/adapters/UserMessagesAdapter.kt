package ru.movista.presentation.chat.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.item_chat_user_message_choice.view.*
import ru.movista.R
import ru.movista.data.entity.ActionEntity
import ru.movista.presentation.utils.animateOnPress
import ru.movista.presentation.utils.animateOnRelease
import ru.movista.presentation.viewmodel.PredefinedUserAction
import ru.movista.data.source.local.HandableActionIds
import timber.log.Timber

class UserMessagesAdapter(private val listener: (Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.Adapter<UserMessagesAdapter.ViewHolder>() {

    var makeInactive = false

    var messages = arrayListOf<PredefinedUserAction>()
        set(value) {
            messages.clear()
            messages.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_chat_user_message_choice, parent, false)
        )
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(messages[position].actionEntity)
    }

    fun updateMessages(newList: List<PredefinedUserAction>) {
        val diffResult = DiffUtil.calculateDiff(UserMessagesDiffCallback(messages, newList))
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeHistoryAction() {
        val clearHistoryPosition = messages.indexOfFirst {
            it.actionEntity.action_id == HandableActionIds.CLEAR_CHAT
        }

        if (clearHistoryPosition != -1) {
            messages.removeAt(clearHistoryPosition)
            notifyItemRemoved(clearHistoryPosition)
        } else {
            Timber.tag("UserMessagesAdapter").e("Error. UserMessagesAdapter does not contain clearHistoryAction")
        }
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        init {
            itemView.user_message_choice_text.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        itemView.user_message_choice_text.animateOnPress()
                    }
                    MotionEvent.ACTION_UP -> {
                        itemView.user_message_choice_text.animateOnRelease()
                        listener.invoke(adapterPosition)
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        itemView.user_message_choice_text.animateOnRelease()
                    }
                    else -> {
                    }
                }
                return@setOnTouchListener true
            }
        }

        fun bind(userAction: ActionEntity) {

            if (makeInactive && itemView.alpha == 1f) {
                itemView.animate().alpha(0.5f)
            } else if (!makeInactive && itemView.alpha != 1f) {
                itemView.alpha = 1f
            }
            itemView.user_message_choice_text.text = userAction.title
        }
    }

    class UserMessageMarginItemDecorator(
        private val smallMargin: Int,
        private val bigMargin: Int
    ) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: androidx.recyclerview.widget.RecyclerView,
            state: androidx.recyclerview.widget.RecyclerView.State
        ) {

            when (parent.getChildAdapterPosition(view)) {
                0 -> {
                    outRect.left = bigMargin
                    outRect.right = smallMargin
                }
                state.itemCount - 1 -> {
                    outRect.right = bigMargin
                }

                else -> {
                    outRect.right = smallMargin
                }
            }
        }
    }

    class UserMessagesDiffCallback(
        private val oldList: List<PredefinedUserAction>,
        private val newList: List<PredefinedUserAction>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].actionEntity.action_id == newList[newItemPosition].actionEntity.action_id
        }

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}


