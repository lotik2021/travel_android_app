package ru.movista.data.entity.objects

data class SkillDataEntity(
    val action_id: String,
    val description: String,
    val id: String,
    val versions: List<SkillVersionsEntity>,
    val android_icon_url: String,
    val ios_icon_url: String
)

data class SkillVersionsEntity(
    val id: String
)
