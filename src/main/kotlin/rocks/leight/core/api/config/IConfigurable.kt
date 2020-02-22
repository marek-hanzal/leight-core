package rocks.leight.core.api.config

typealias Configurator<T> = T.() -> Unit

interface IConfigurable<T> {
    /**
     * register a lamba configurator
     */
    fun configurator(configurator: Configurator<T>)

    /**
     * makes object ready to setup (executes configurators)
     */
    fun configure()

    /**
     * make an object ready to use; this could be thought as late
     * constructor
     */
    fun setup()

    /**
     * release an object; free all allocated resources (kind of destructor)
     */
    fun release()
}
