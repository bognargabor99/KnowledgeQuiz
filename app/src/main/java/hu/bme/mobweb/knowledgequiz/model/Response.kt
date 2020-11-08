package hu.bme.mobweb.knowledgequiz.model

class Response (
        val response_code: String,
        val results: MutableList<Question>
)