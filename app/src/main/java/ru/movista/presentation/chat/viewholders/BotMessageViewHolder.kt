package ru.movista.presentation.chat.viewholders

import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.item_chat_bot_message.view.*
import org.jetbrains.anko.toast
import ru.movista.R
import ru.movista.presentation.chat.BotMessage
import ru.movista.presentation.utils.animateOnPress
import ru.movista.presentation.utils.animateOnRelease
import ru.movista.presentation.utils.copyToClipboard

class BotMessageViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    init {
        itemView.message_text.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    itemView.message_text.animateOnPress()
                }
                MotionEvent.ACTION_UP -> {
                    itemView.message_text.animateOnRelease()
                }
                MotionEvent.ACTION_CANCEL -> {
                    itemView.message_text.animateOnRelease()
                }
                else -> {
                }
            }
            return@setOnTouchListener false
        }
    }

    fun bind(botMessage: BotMessage) {
        itemView.message_text.text = botMessage.message

        itemView.message_text.setOnLongClickListener {
            with(itemView.context) {
                copyToClipboard(botMessage.message)
                toast(R.string.copied)
            }
            true
        }
    }
}