package ucab.sqa.workit.aggregator.model

case class Group private(name: String)

object Group {
    def of(name: String) =
        Either.cond(name.trim() != "", Group(name), GroupNameEmpty())
}