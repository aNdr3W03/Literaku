package com.fractaldev.literaku

import com.google.gson.annotations.SerializedName

data class PenjelajahResponse(

	@field:SerializedName("kind")
	val kind: String,

	@field:SerializedName("context")
	val context: Context,

	@field:SerializedName("queries")
	val queries: Queries,

	@field:SerializedName("searchInformation")
	val searchInformation: SearchInformation,

	@field:SerializedName("items")
	val items: List<ItemsItem>,

	@field:SerializedName("url")
	val url: Url
)

data class NextPageItem(

	@field:SerializedName("inputEncoding")
	val inputEncoding: String,

	@field:SerializedName("totalResults")
	val totalResults: String,

	@field:SerializedName("startIndex")
	val startIndex: Int,

	@field:SerializedName("outputEncoding")
	val outputEncoding: String,

	@field:SerializedName("searchTerms")
	val searchTerms: String,

	@field:SerializedName("cx")
	val cx: String,

	@field:SerializedName("count")
	val count: Int,

	@field:SerializedName("safe")
	val safe: String,

	@field:SerializedName("title")
	val title: String
)

data class Url(

	@field:SerializedName("template")
	val template: String,

	@field:SerializedName("type")
	val type: String
)

data class Queries(

	@field:SerializedName("request")
	val request: List<RequestItem>,

	@field:SerializedName("nextPage")
	val nextPage: List<NextPageItem>
)

data class ItemsItem(

	@field:SerializedName("snippet")
	val snippet: String,

	@field:SerializedName("kind")
	val kind: String,

	@field:SerializedName("mime")
	val mime: String,

	@field:SerializedName("link")
	val link: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("formattedUrl")
	val formattedUrl: String,

	@field:SerializedName("cacheId")
	val cacheId: String,

	@field:SerializedName("htmlFormattedUrl")
	val htmlFormattedUrl: String,

	@field:SerializedName("htmlTitle")
	val htmlTitle: String,

	@field:SerializedName("pagemap")
	val pagemap: Pagemap,

	@field:SerializedName("displayLink")
	val displayLink: String,

	@field:SerializedName("htmlSnippet")
	val htmlSnippet: String,

	@field:SerializedName("fileFormat")
	val fileFormat: String
)

data class CseThumbnailItem(

	@field:SerializedName("src")
	val src: String,

	@field:SerializedName("width")
	val width: String,

	@field:SerializedName("height")
	val height: String
)

data class MetatagsItem(

	@field:SerializedName("creationdate")
	val creationdate: String,

	@field:SerializedName("producer")
	val producer: String
)

data class CseImageItem(

	@field:SerializedName("src")
	val src: String
)

data class RequestItem(

	@field:SerializedName("inputEncoding")
	val inputEncoding: String,

	@field:SerializedName("totalResults")
	val totalResults: String,

	@field:SerializedName("startIndex")
	val startIndex: Int,

	@field:SerializedName("outputEncoding")
	val outputEncoding: String,

	@field:SerializedName("searchTerms")
	val searchTerms: String,

	@field:SerializedName("cx")
	val cx: String,

	@field:SerializedName("count")
	val count: Int,

	@field:SerializedName("safe")
	val safe: String,

	@field:SerializedName("title")
	val title: String
)

data class Context(

	@field:SerializedName("title")
	val title: String
)

data class SearchInformation(

	@field:SerializedName("searchTime")
	val searchTime: Double,

	@field:SerializedName("totalResults")
	val totalResults: String,

	@field:SerializedName("formattedTotalResults")
	val formattedTotalResults: String,

	@field:SerializedName("formattedSearchTime")
	val formattedSearchTime: String
)

data class Pagemap(

	@field:SerializedName("cse_thumbnail")
	val cseThumbnail: List<CseThumbnailItem>,

	@field:SerializedName("metatags")
	val metatags: List<MetatagsItem>,

	@field:SerializedName("cse_image")
	val cseImage: List<CseImageItem>
)
