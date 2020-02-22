package rocks.leight.core.scraper

import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.scraper.IScraper
import java.net.URL
import java.net.URLEncoder
import kotlin.time.ExperimentalTime
import kotlin.time.MonoClock

@ExperimentalTime
class Scraper(container: IContainer) : IScraper {
    private val scraperConfig: ScraperConfig by container.lazy()
    private val logger = KotlinLogging.logger { }

    @Suppress("MemberVisibilityCanBePrivate")
    override fun download(url: String): String {
        val target = scraperConfig.url + "scraper/" + URLEncoder.encode(url, "UTF-8")
        logger.debug { "Download: [$url] Downloading from [$target]" }
        try {
            val monoClock = MonoClock
            val clockMark = monoClock.markNow()
            return URL(target).openConnection().apply {
                connectTimeout = 5000
                readTimeout = scraperConfig.timeout
            }.getInputStream().use { it.bufferedReader().readText() }.also {
                logger.debug { "Download: [$url] done in ${clockMark.elapsedNow()}ms" }
            }
        } catch (e: Throwable) {
            logger.error("Cannot download [$url] from [$target]", e)
            throw e
        }
    }

    override fun async(url: String) {
        val target = scraperConfig.url + "/scheduler/" + URLEncoder.encode(url, "UTF-8")
        logger.debug { "Async: [$url] Scheduling from [$target]" }
        val monoClock = MonoClock
        val clockMark = monoClock.markNow()
        URL(target).openConnection().apply {
            connectTimeout = 5000
            readTimeout = scraperConfig.timeout
        }.getInputStream().use { it.bufferedReader().readText() }.also {
            logger.debug { "Async: [$url] done in ${clockMark.elapsedNow()}ms" }
        }
    }

    override fun <T> scrape(url: String, callback: Document.() -> T) {
        callback(Jsoup.parse(download(url)))
    }
}
