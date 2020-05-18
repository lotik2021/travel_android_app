package ru.movista.presentation.chat.viewholders

import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.item_chat_user_message.view.*
import org.jetbrains.anko.toast
import ru.movista.R
import ru.movista.presentation.chat.UserMessage
import ru.movista.presentation.utils.animateOnPress
import ru.movista.presentation.utils.animateOnRelease
import ru.movista.presentation.utils.copyToClipboard

class UserMessageViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    init {
        itemView.user_message_text.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    itemView.user_message_text.animateOnPress()
                }
                MotionEvent.ACTION_UP -> {
                    itemView.user_message_text.animateOnRelease()
                }
                MotionEvent.ACTION_CANCEL -> {
                    itemView.user_message_text.animateOnRelease()
                }
                else -> {
                }
            }
            return@setOnTouchListener false
        }
    }

    fun bind(userMessage: UserMessage) {
        itemView.user_message_text.text = userMessage.message

        itemView.user_message_text.setOnLongClickListener {
            with(itemView.context) {
                copyToClipboard(userMessage.message)
                toast(R.string.copied)
            }
            true
        }
    }
}