package com.twitter.finatra.http.tests.integration.json

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.jackson.ScalaObjectMapper
import com.twitter.finatra.jackson.caseclass.exceptions.CaseClassMappingException
import com.twitter.util.validation.ScalaValidator
import jakarta.validation.ValidationException
import javax.inject.Inject

class ValidationController @Inject() (
  validator: ScalaValidator,
  scalaObjectMapper: ScalaObjectMapper)
    extends Controller {

  get("/validate_things") { _: Request =>
    var errorMessage: String = ""
    try {
      validator.verify(Things(Seq.empty[String]))
    } catch {
      case e: ValidationException =>
        errorMessage = e.getMessage
    }
    errorMessage
  }

  get("/parse_things") { _: Request =>
    var errorMessage: String = ""
    val raw = """
            { "names": [] }
        """
    try {
      scalaObjectMapper.parse[Things](raw)
    } catch {
      case e: CaseClassMappingException =>
        errorMessage = e.errors.head.getMessage
    }
    errorMessage
  }
}
