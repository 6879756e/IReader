package org.ireader.source.sources.en.source_tower_deprecated

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import org.ireader.core.utils.*
import org.ireader.domain.utils.selectorReturnerListType
import org.ireader.domain.utils.selectorReturnerStringType
import org.ireader.source.core.Dependencies
import org.ireader.source.core.ParsedHttpSource
import org.ireader.source.models.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import kotlin.random.Random


data class SourceTower constructor(
    private val deps: Dependencies,
    override val id: Long = Random.nextLong(),
    override val baseUrl: String,
    override val lang: String,
    override val name: String,
    override val creator: String,
    override val supportsMostPopular: Boolean = false,
    override val supportSearch: Boolean = false,
    override val supportsLatest: Boolean = false,
    override val iconUrl: String = "",
    val creatorNote: String? = null,
    val customSource: Boolean = false,
    val latest: Latest? = null,
    val popular: Popular? = null,
    val detail: Detail? = null,
    val search: Search? = null,
    val chapters: Chapters? = null,
    val content: Content? = null,
) : ParsedHttpSource(deps) {


    override fun getFilterList(): FilterList {
        return FilterList()
    }

    val pageFormat = "{page}"
    val searchQueryFormat = "{query}"


    override fun headersBuilder() = Headers.Builder().apply {
        add(
            HttpHeaders.UserAgent,
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4793.0 Safari/537.36"
        )
        add(HttpHeaders.Referrer, baseUrl)
        add(HttpHeaders.CacheControl, "max-age=0")
    }


    override val headers: Headers
        get() = headersBuilder().build()

    override fun fetchLatestEndpoint(page: Int): String? = latest?.endpoint
    override fun fetchPopularEndpoint(page: Int): String? = popular?.endpoint
    override fun fetchSearchEndpoint(page: Int, query: String): String? = search?.endpoint


    /****************************SELECTOR*************************************************************/
    override fun popularSelector(): String = popular?.selector ?: ""
    override fun latestSelector(): String = latest?.selector ?: ""

    override fun popularNextPageSelector(): String? = popular?.nextPageSelector
    override fun latestNextPageSelector(): String? = latest?.nextPageSelector
    fun hasNextChapterSelector() = chapters?.nextPageSelector ?: ""
    override fun searchSelector(): String = search?.selector ?: ""
    override fun searchNextPageSelector(): String = search?.nextPageSelector ?: ""


    override fun chaptersSelector(): String = chapters?.selector ?: ""


    /****************************SELECTOR*************************************************************/


    /****************************REQUESTS**********************************************************/

    override fun popularRequest(page: Int): HttpRequestBuilder {
        return requestBuilder("$baseUrl${popular?.endpoint?.applyPageFormat(page)}")
    }


    override fun latestRequest(page: Int): HttpRequestBuilder {
        return requestBuilder(baseUrl + "${latest?.endpoint?.applyPageFormat(page)}")
    }

    override fun chaptersRequest(book: BookInfo): HttpRequestBuilder {
        return requestBuilder(book.link)
    }

    override fun searchRequest(page: Int, query: String, filters: FilterList): HttpRequestBuilder {
        return requestBuilder(baseUrl + "${
            fetchSearchEndpoint(page, query)?.replace(searchQueryFormat,
                query)
        }")
    }

    override fun contentRequest(chapter: ChapterInfo): HttpRequestBuilder {
        return requestBuilder(chapter.key)
    }


    override fun detailsRequest(book: BookInfo): HttpRequestBuilder {
        return requestBuilder(book.link)
    }
    /****************************REQUESTS**********************************************************/


    /****************************PARSE FROM ELEMENTS***********************************************/


    override fun popularFromElement(element: Element): BookInfo {
        val selectorLink = popular?.linkSelector
        val attLink = popular?.linkAtt
        val selectorName = popular?.nameSelector
        val attName = popular?.nameAtt
        val selectorCover = popular?.coverSelector
        val attCover = popular?.coverAtt


        val link = selectorReturnerStringType(element,
            selectorLink,
            attLink).replace(baseUrl, "")
        val title = selectorReturnerStringType(element, selectorName, attName).formatHtmlText()
        val cover = selectorReturnerStringType(element,
            selectorCover,
            attCover)



        return BookInfo(link = baseUrl + link, title = title, cover = cover)
    }


    override fun latestFromElement(element: Element): BookInfo {

        val selectorLink = latest?.linkSelector
        val attLink = latest?.linkAtt
        val selectorName = latest?.nameSelector
        val attName = latest?.nameAtt
        val selectorCover = latest?.coverSelector
        val attCover = latest?.coverAtt

        val link = selectorReturnerStringType(element,
            selectorLink,
            attLink).replace(baseUrl, "")
        val title = selectorReturnerStringType(element, selectorName, attName).formatHtmlText()
        val cover = selectorReturnerStringType(element,
            selectorCover,
            attCover)


        //Timber.e("Timber: SourceCreator" + val coverLink)

        return BookInfo(link = baseUrl + link, title = title, cover = cover)
    }

    override fun chapterFromElement(element: Element): ChapterInfo {

        val selectorLink = chapters?.linkSelector
        val attLink = chapters?.linkAtt
        val selectorName = chapters?.nameSelector
        val attName = chapters?.nameAtt

        val link = selectorReturnerStringType(element,
            selectorLink,
            attLink).replace(baseUrl, "")
        val title = selectorReturnerStringType(element, selectorName, attName).formatHtmlText()

        return ChapterInfo(key = baseUrl + link, name = title)
    }

    override fun searchFromElement(element: Element): BookInfo {
        val selectorLink = search?.linkSelector
        val attLink = search?.linkAtt
        val selectorName = search?.nameSelector
        val attName = search?.nameAtt
        val selectorCover = search?.coverSelector
        val attCover = search?.coverAtt

        val link = selectorReturnerStringType(element,
            selectorLink,
            attLink).replace(baseUrl, "")
        val title = selectorReturnerStringType(element, selectorName, attName).formatHtmlText()
        val cover = selectorReturnerStringType(element,
            selectorCover,
            attCover)


        return BookInfo(link = baseUrl + link, title = title, cover = cover)
    }

    /****************************PARSE FROM ELEMENTS***********************************************/

    /****************************PARSE*************************************************************/

    override fun latestParse(document: Document): BooksPage {
        val books = mutableListOf<BookInfo>()
        books.addAll(document.select(latest?.selector).map { element ->
            latestFromElement(element)
        })
        val hasNextPage = latestNextPageSelector()?.let { selector ->
            document.select(selector).first()
        } != null


        return BooksPage(
            books = books,
            hasNextPage = hasNextPage,
        )
    }

    override fun detailParse(document: Document): BookInfo {
        val selectorBookName = detail?.nameSelector
        val attBookName = detail?.nameAtt
        val coverSelector = detail?.coverSelector
        val coverAtt = detail?.coverAtt
        val selectorDescription = detail?.descriptionSelector
        val attDescription = detail?.descriptionBookAtt
        val selectorAuthor = detail?.authorBookSelector
        val attAuthor = detail?.authorBookAtt
        val selectorCategory = detail?.categorySelector
        val attCategory = detail?.categoryAtt


        val title = selectorReturnerStringType(document, selectorBookName, attAuthor)
        val coverLink = selectorReturnerStringType(document,
            coverSelector,
            coverAtt).replace("render_isfalse", "")
        val author = selectorReturnerStringType(document, selectorAuthor, attBookName)
        val description = selectorReturnerListType(document,
            selectorDescription,
            attDescription).map { it.formatHtmlText() }.joinToString()
            val category = selectorReturnerListType(document, selectorCategory, attCategory)
            return BookInfo(
                title = title,
                cover = coverLink,
                author = author,
                description = description,
                genres = category,
                link = "")



    }


    override fun chaptersParse(document: Document): List<ChapterInfo> {
        val chapters = mutableListOf<ChapterInfo>()

            chapters.addAll(document.select(chaptersSelector()).map { chapterFromElement(it) })


        val mChapters =
            if (this.chapters?.isChapterStatsFromFirst == true) chapters else chapters.reversed()

        return mChapters

    }

    override fun pageContentParse(document: Document): List<String> {

        val contentList: MutableList<String> = mutableListOf()

        val contentSelector = content?.pageContentSelector
        val contentAtt = content?.pageContentAtt
        val titleSelector = content?.pageTitleSelector
        val titleAtt = content?.pageTitleAtt
        val title =
            selectorReturnerStringType(document, titleSelector, titleAtt).formatHtmlText()
        val page = selectorReturnerListType(document,
            contentSelector,
            contentAtt).map { it.formatHtmlText() }
        contentList.addAll(merge(listOf(title), page))


        return contentList
    }

    override fun searchParse(document: Document): BooksPage {

        var books = mutableListOf<BookInfo>()

            /**
             * I Add Filter Because sometimes this value contains null values
             * so the null book shows in search screen
             */
            books.addAll(document.select(searchSelector()).map { element ->
                searchFromElement(element)
            })

        val hasNextPage = false


        return BooksPage(
            books,
            hasNextPage,
        )
    }

    override fun popularParse(document: Document): BooksPage {
        val books = document.select(popularSelector()).map { element ->
            popularFromElement(element)
        }

        val hasNextPage = popularNextPageSelector()?.let { selector ->
            document.select(selector).first()
        } != null

        return BooksPage(
            books,
            hasNextPage,
        )
    }

    /****************************PARSE FROM JSON**********************************/
    fun chapterListFromJson(jsonObject: Map<String, String>): ChapterInfo {

        val mName = chapters?.nameSelector
        val mLink = chapters?.linkSelector
        val title = jsonObject[mName]?.formatHtmlText() ?: ""
        val link = jsonObject[mLink] ?: ""

        return ChapterInfo(name = title, key = link)
    }

    fun searchBookFromJson(jsonObject: Map<String, String>): BookInfo {

        val mName = search?.nameSelector
        val mLink = search?.linkSelector
        val mCover = search?.coverSelector
        val title = jsonObject[mName]?.formatHtmlText() ?: ""
        val link = jsonObject[mLink] ?: ""
        val coverLink = jsonObject[mCover]
        return BookInfo(title = title, link = link, cover = coverLink ?: "")
    }

    fun detailFromJson(jsonObject: Map<String, String>): BookInfo {

        val mName = detail?.nameSelector
        val mCover = detail?.coverSelector
        val mDescription = detail?.descriptionSelector
        val mAuthor = detail?.authorBookSelector
        val mCategory = detail?.categorySelector

        val title = jsonObject[mName]?.formatHtmlText() ?: ""
        val coverLink = jsonObject[mCover]
        val description = jsonObject[mDescription]?.formatHtmlText() ?: ""
        val author = jsonObject[mAuthor]?.formatHtmlText() ?: ""
        val category = listOf(jsonObject[mCategory]?.formatHtmlText() ?: "")

        return BookInfo(
            title = title,
            description = description,
            author = author,
            genres = category,
            cover = coverLink ?: "",
            link = "",
        )
    }


    /**
     * Fetchers
     */
    override suspend fun getChapters(book: BookInfo): List<ChapterInfo> {
        return kotlin.runCatching {
            return@runCatching withContext(Dispatchers.IO) {
                val request = if (chapters?.isGetRequestType == true) {
                    client.get<Document>(chaptersRequest(book))
                } else {
                    client.post<Document>(chaptersRequest(book))
                }
                return@withContext chaptersParse(request)
            }
        }.getOrThrow()
    }

    /**
     * Returns a page with a list of book. Normally it's not needed to
     * override this method.
     * @param page the page number to retrieve.
     */
    override suspend fun getPopular(page: Int): BooksPage {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {

                val request = if (popular?.isGetRequestType == true) {
                    client.get<Document>(popularRequest(page))
                } else {
                    client.post<Document>(popularRequest(page))
                }

                var books = popularParse(request)

                return@withContext books
            }
        }.getOrThrow()


    }


    override suspend fun getLatest(page: Int): BooksPage {
        return kotlin.runCatching {
            return@runCatching withContext(Dispatchers.IO) {
                val request = if (latest?.isGetRequestType == true) {
                    client.get<Document>(latestRequest(page))
                } else {
                    client.post<Document>(latestRequest(page))
                }
                //val request = client.get<Document>(latestRequest(page))
                return@withContext latestParse(request)
            }
        }.getOrThrow()
    }

    /**
     * Returns a book. Normally it's not needed to
     * override this method.
     *
     * @param page the page number to retrieve.
     */
    override suspend fun getDetails(book: BookInfo): BookInfo {
        return kotlin.runCatching {
            return@runCatching withContext(Dispatchers.IO) {
                val request = if (detail?.isGetRequestType == true) {
                    client.get<Document>(detailsRequest(book))
                } else {
                    client.post<Document>(detailsRequest(book))
                }
                val book = detailParse(request)
                return@withContext book.copy(link = book.link)
            }
        }.getOrThrow()

    }


    /**
     * Returns a ChapterPage. Normally it's not needed to
     * override this method.
     *
     * @param page the page number to retrieve.
     */
    override suspend fun getContents(chapter: ChapterInfo): List<String> {
        return kotlin.runCatching {
            return@runCatching withContext(Dispatchers.IO) {
                val request = if (content?.isGetRequestType == true) {
                    client.get<Document>(contentRequest(chapter))
                } else {
                    client.post<Document>(contentRequest(chapter))
                }
                return@withContext pageContentParse(request)
            }
        }.getOrThrow()


    }

    /**
     * Returns a BooksPage. Normally it's not needed to
     * override this method.
     *
     * @param page the page number to retrieve.
     * @param query the search query to retrieve.
     */
    override suspend fun getSearch(page: Int, query: String, filters: FilterList): BooksPage {
        return kotlin.runCatching {
            return@runCatching withContext(Dispatchers.IO) {
                val request = if (content?.isGetRequestType == true) {
                    client.get<Document>(searchRequest(page, query, filters))
                } else {
                    client.post<Document>(searchRequest(page, query, filters))
                }
                return@withContext searchParse(request)
            }
        }.getOrThrow()
    }


}