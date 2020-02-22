package rocks.leight.core.api.scraper

import org.jsoup.nodes.Document

interface IScraper {
    fun download(url: String): String

    fun async(url: String)

    fun <T> scrape(url: String, callback: Document.() -> T)
}
