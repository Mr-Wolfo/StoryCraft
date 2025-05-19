package com.wolfo.storycraft.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(
) {
    @Serializable
    data object StoryList : Screen()

    @Serializable
    data object StoryView : Screen() {

        @Serializable
        data class Details(val storyId: String?) : Screen()

        @Serializable
        data class Reader(val storyId: String) : Screen()
    }

    @Serializable
    data class StoryEditor(val storyId: String?) : Screen()

    @Serializable
    data object Profile : Screen() {

        @Serializable
        data object Profile : Screen()

        @Serializable
        data object Auth : Screen()

        @Serializable
        data object Login : Screen()

        @Serializable
        data object Register : Screen()
    }
}

/*
sealed interface ScreenBase
{
    @get:StringRes val titleResId: Int

    companion object {
        private val titleRegistry: Map<KClass<out ScreenBase>, Int> = mapOf(
            StoryList::class to R.string.screen_title_story_list,
            StoryView::class to R.string.screen_title_story_reader,
            StoryEditor::class to R.string.screen_title_story_editor,
            Profile::class to R.string.screen_title_profile
        )

        fun findTitleResIdByRoute(route: String?): Int? {
            if (route == null) return null
            return titleRegistry.entries.find { (kClass, _) ->
                val simpleName = kClass.simpleName ?: "---"
                val qualifiedName = kClass.qualifiedName ?: "---"
                val routeBase = route.substringBefore("/{").substringBefore("?")
                routeBase.endsWith(simpleName) || routeBase == qualifiedName
            }?.value
        }
    }
}
*/
