// Request body
data class AiRequest(
    val inputs: String,
    val parameters: GenerationParameters? = null
)

data class GenerationParameters(
    val max_new_tokens: Int? = null,
    val temperature: Double? = null
)

// Response body (array of items)
data class AiResponseItem(
    val generated_text: String
)