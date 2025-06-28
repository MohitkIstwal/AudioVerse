package com.example.audio_verse.bot

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<Message>()
    }

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.apiKey
    )

    fun sendMessage(question : String){
        viewModelScope.launch {
            try{
                val systemPrompt = content{
                    text("""
                    You are a chatbot assistant that helps users in any questions.
                    U can answer any questions or any general questions
                """.trimIndent())
                }
                val chat = generativeModel.startChat(
                    history = listOf(systemPrompt) + messageList.map {
                        content { text(it.message) }
                    }
                )

                messageList.add(Message(question,"user"))
                messageList.add(Message("Typing....","model"))

                val response = chat.sendMessage(question)
                messageList.removeAt(messageList.size - 1)
                messageList.add(Message(response.text.toString(),"model"))
            }catch (e : Exception){
                messageList.removeAt(messageList.size - 1)
                messageList.add(Message("Error : "+e.message.toString(),"model"))
            }


        }
    }
}