package com.example.ponggame_kotlin

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class  MainActivity : AppCompatActivity() {
    // Declaración de variables usando 'lateinit' para inicialización no nula en onCreate
    private lateinit var gameView: GameView
    private lateinit var scorePlayer1: TextView
    private lateinit var scorePlayer2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ocultar la barra de título (si el tema lo permite)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Ocultar la ActionBar (si existe)
        supportActionBar?.hide()
        // Establecer la ventana en modo de pantalla completa
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // Establecer el diseño de la actividad
        setContentView(R.layout.activity_main)
        // Inicializar las vistas con 'findViewById'
        scorePlayer1 = findViewById(R.id.scorePlayer1)
        scorePlayer2 = findViewById(R.id.scorePlayer2)
        val gameContainer: FrameLayout = findViewById(R.id.gameContainer)
        // Crear e inicializar GameView
        gameView = GameView(this)
        gameContainer.addView(gameView)
        // Configurar el listener para actualizar los puntajes
        // Usando una expresión lambda para la implementación de la interfaz SAM
        // (Single Abstract Method) si GameView.ScoreUpdateListener es una interfaz Java.
        // Si no, se usa un objeto anónimo. Aquí se usa el objeto anónimo para compatibilidad
        // con la sintaxis original, pero Kotlin puede simplificarlo.

        gameView.setScoreUpdateListener(object : GameView.ScoreUpdateListener {
            override fun onScoreUpdate(player1Score: Int, player2Score: Int) {
                // Ejecutar la actualización de la UI en el hilo principal
                runOnUiThread {
                    scorePlayer1.text = player1Score.toString() // Convierte Int a String
                    scorePlayer2.text = player2Score.toString() // Convierte Int a String
                }
            }
        })

    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }
}