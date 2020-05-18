package ru.movista.data.repository

import android.os.Bundle
import ru.movista.presentation.chat.ChatItem
import ru.movista.presentation.viewmodel.PredefinedUserAction
import java.io.Serializable

class SavedInstanceStateRepository {

    companion object {
        private const val CHAT_STATE = "chat_state"
    }

    fun saveChatState(bundle: Bundle, chatState: ChatState) {
        bundle.putSerializable(CHAT_STATE, chatState)
    }


    fun restoreChatState(bundle: Bundle): ChatState? {
        return bundle.getSerializable(CHAT_STATE) as? ChatState?
    }

    data class ChatState(
        val chatMessages: List<ChatItem>,
        val userActions: List<PredefinedUserAction>,
        val wasFirstViewAttach: Boolean,
        val sessionIsCreated: Boolean
    ) : Serializable
}