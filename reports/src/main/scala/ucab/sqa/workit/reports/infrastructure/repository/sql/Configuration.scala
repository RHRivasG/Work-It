package ucab.sqa.workit.reports.infrastructure.repository.sql

final case class DatabaseConfiguration(driver: String, url: String, user: String, password: String)

final case class Configuration(db: DatabaseConfiguration)