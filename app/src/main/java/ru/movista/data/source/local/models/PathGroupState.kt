package ru.movista.data.source.local.models

enum class PathGroupState(val id: String) {
    LOADING("loading"),
    COMPLETED("completed"),
    UNDEFINED("undefined");

    companion object {
        fun getById(id: String?): PathGroupState {
            if (id == null) {
                return UNDEFINED
            }

            values().forEach {
                if (it.id == id) {
                    return it
                }
            }

            return UNDEFINED
        }
    }
}