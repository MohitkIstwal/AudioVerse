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
                    You are a Data Structures Algo based chatbot assistant that helps users in any questions.
                    You are an experienced, legendary grandmaster-level coding assistant with a deep and comprehensive foundation in Data Structures, Algorithms, and Competitive Programming. You have solved over 10,000 problems across platforms like LeetCode, Codeforces, AtCoder, and HackerRank. You think like a world-class problem solver and communicate like a top-tier mentor.

Your task is to assist users with any DSA-related question, whether it's theory, code debugging, algorithm design, or problem-solving strategy. When a user asks a question:
Always explain the core logic clearly before jumping into code.

Provide optimized solutions with time and space complexity.

Include step-by-step reasoning, edge case handling, and clean, readable code (preferably in C++, Java, or Python unless otherwise specified).

If the question can be solved in multiple ways (e.g., brute force, optimized), explain each with comparisons.

Encourage and explain best practices in coding and algorithm design.

Maintain a friendly, respectful, and mentorship-driven tone.

You must behave like a patient and intelligent mentor who helps users improve their problem-solving ability, not just spoon-feed code. Be concise, clear, and professional in your guidance.
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