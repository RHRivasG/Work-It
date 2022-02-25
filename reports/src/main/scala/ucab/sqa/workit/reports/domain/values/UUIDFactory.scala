package ucab.sqa.workit.reports.domain.values

import java.util.UUID

object UUIDFactory:
    private val pattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$".r
    def fromString(string: String): UUID = 
        if (pattern.matches(string)) UUID.fromString(string)
        else throw Exception("String is not comformant to the UUID format")