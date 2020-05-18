package ru.movista.presentation.chat.viewholders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_chat_message_skills.view.*
import ru.movista.R
import ru.movista.presentation.chat.SkillsItem
import ru.movista.presentation.utils.inject
import ru.movista.utils.inflate

class SkillsMessageViewHolder(view: View, val listener: (Int, Int) -> Unit) :
    RecyclerView.ViewHolder(view) {

    fun bind(skillsItem: SkillsItem) {
        val context = itemView.context

        val parentLayout = itemView as LinearLayout
        parentLayout.removeAllViews()

        skillsItem.skills.forEachIndexed { index, item ->
            val view: View = context.inflate(R.layout.layout_chat_message_skills)
            with(view) {
                chat_skill_image.inject(item.icon, item.iosIcon)
                chat_skill_text.text = item.text
                setOnClickListener { listener.invoke(adapterPosition, index) }
                parentLayout.addView(this)
            }
        }
    }
}