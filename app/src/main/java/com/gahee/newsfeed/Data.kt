package com.gahee.newsfeed

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class RssResponse(
    @field: Element(name = "channel")
    var channel: ChannelResponse? = null
)

@Root(name = "channel", strict = false)
data class ChannelResponse(
    @field: Element(name = "title")
    var title: String? = NO_TITLE,
    @field: Element(name = "link")
    var link: String? = null,
    @field: Element(name = "copyright")
    var copyright: String? = null,
    @field: Element(name = "pubDate", required = false)
    var pubDate: String = UNKNOWN,
    @field: Element(name = "lastBuildDate", required = false)
    var lastBuildDate: String = UNKNOWN,
    @field: ElementList(inline = true)
    var itemList: List<NewsItem>? = null
)

@Root(name = "item", strict = false)
data class NewsItem(
    @field: Element(name = "title")
    var title: String = NO_TITLE,
    @field: Element(name = "link")
    var link: String? = null,
    @field: Element(name = "image", required = false)
    var image: String? = null,
    @field: Element(name = "author", required = false)
    var author: String = UNKNOWN,
    @field: Element(name = "pubDate", required = false)
    var pubDate: String = UNKNOWN,
)

const val NO_TITLE = "TITLE IS EMPTY"
const val UNKNOWN = "UNKNOWN"