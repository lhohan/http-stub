package eu.lhoest.httpstub

import org.scalatest.FunSuite

class JsonTests extends FunSuite with JsonSupport{

  import spray.json._

  test("1"){
    val source = """{ "some": "JSON source" }"""
    val jsonAst = source.parseJson // or JsonParser(source)
    val json = jsonAst.prettyPrint
    println(json)
  }

  test("2"){
    val source = """{ "id": "100", "count": 12345 }"""
    val jsonAst = source.parseJson // or JsonParser(source)
    val report = jsonAst.convertTo[Report]
    println(report)
  }

}
