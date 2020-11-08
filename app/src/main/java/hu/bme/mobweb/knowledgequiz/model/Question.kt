package hu.bme.mobweb.knowledgequiz.model

class Question (
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: MutableList<String>,
    var allAnswers: MutableList<String>
) {
    enum class Type(val type: String) {
        MULTIPLE("multiple"),
        TRUE_FALSE("boolean");

        companion object {
            fun from(findValue: String): Type = values().first { it.type == findValue }
        }
    }
}