package rocks.leight.core.api.message

interface IMessage {
    val type: String
    val target: String?
}
