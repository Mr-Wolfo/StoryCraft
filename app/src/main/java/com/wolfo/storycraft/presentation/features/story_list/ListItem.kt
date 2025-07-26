package com.wolfo.storycraft.presentation.features.story_list

import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.yandex.mobile.ads.nativeads.NativeAd

sealed class ListItem {
    data class StoryItem(val story: StoryBaseInfo) : ListItem()
    data class AdItem(val nativeAd: NativeAd? = null) : ListItem()

    companion object {
        fun createMixedList(
            stories: List<StoryBaseInfo>,
            adInterval: Int = 4,
        ): List<ListItem> {
            return stories.flatMapIndexed { index, story ->
                if (index >= adInterval && index % adInterval == 0) {
                    listOf(StoryItem(story), AdItem())
                } else {
                    listOf(StoryItem(story))
                }
            }
        }
    }
}