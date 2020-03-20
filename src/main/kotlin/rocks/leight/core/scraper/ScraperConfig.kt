package rocks.leight.core.scraper

data class ScraperConfig(
	val url: String,
	val timeout: Int = 15000
)
