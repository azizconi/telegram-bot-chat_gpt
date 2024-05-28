package it.skills.itskills.bot.data.request.text_to_speech

data class TextToSpeechRequestModel(
    val input: String,
    val model: String = "tts-1-hd",
    val voice: String = "nova"//"alloy"
)
