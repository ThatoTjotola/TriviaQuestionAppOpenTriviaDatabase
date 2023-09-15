package com.example.firebase

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class TriviaQuestion(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)

data class ApiResponse(val results: List<TriviaQuestion>)

interface ApiService {

    @GET("api.php")
    fun getTrueFalseQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String
    ): Call<ApiResponse>
}

class MainActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private var currentIndex = 0
    private var streak = 0
    private var score = 0
    private lateinit var questions: List<TriviaQuestion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val response = apiService.getTrueFalseQuestions(10, 15, "medium", "boolean").enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val questionsResponse = response.body()
                        questionsResponse?.let {
                            questions = it.results
                            displayQuestion()
                        }
                    } else {
                        // Handle error
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Handle failure

                }
            })
        }
    }

    private fun displayQuestion() {
        val questionTextView = findViewById<TextView>(R.id.questionTextView)
        val trueButton = findViewById<Button>(R.id.trueButton)
        val falseButton = findViewById<Button>(R.id.falseButton)

        questionTextView.text = questions[currentIndex].question

        trueButton.setOnClickListener { onAnswerSelected("True") }
        falseButton.setOnClickListener { onAnswerSelected("False") }
    }

    private fun onAnswerSelected(answer: String) {
        val currentQuestion = questions[currentIndex]
        if (answer == currentQuestion.correctAnswer) {
            streak++
            score++
        } else {
            // Game Over,
        }

        currentIndex++

        if (currentIndex < questions.size) {
            displayQuestion()
        } else {
            // Game Over, display score
            val resultTextView = findViewById<TextView>(R.id.resultTextView)
            resultTextView.text = "Score: $score out of ${questions.size}"
        }
    }

    
}
