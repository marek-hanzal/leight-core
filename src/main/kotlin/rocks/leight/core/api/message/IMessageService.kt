package rocks.leight.core.api.message

import rocks.leight.core.api.config.IConfigurable

interface IMessageService<T> : IConfigurable<T> {
    fun getTarget(): String?

    fun <TResponse : IMessage> execute(message: IMessage): TResponse?

    fun fire(message: IMessage) = execute<IMessage>(message)
}
