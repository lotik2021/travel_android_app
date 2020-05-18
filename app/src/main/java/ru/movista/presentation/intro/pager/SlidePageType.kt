package ru.movista.presentation.intro.pager

import java.io.Serializable

enum class SlidePageType(val page: Int) : Serializable {
    SLIDE_PAGE_ONE(0),
    SLIDE_PAGE_TWO(1),
    SLIDE_PAGE_THREE(2),
    SLIDE_PAGE_FOUR(3),
    SLIDE_PAGE_FIVE(4),
    SLIDE_PAGE_SIX(5)
}