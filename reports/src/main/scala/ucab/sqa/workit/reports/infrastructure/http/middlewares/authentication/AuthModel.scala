package ucab.sqa.workit.reports.infrastructure.http.middlewares.authentication

final case class AuthModel(roles: Vector[String], id: String)

object AuthModel:
    extension (model: AuthModel)
        def hasRoles(roles: Seq[String]) = model.roles.intersect(roles).length > 0
        def isAdmin = model.id == "admin"
