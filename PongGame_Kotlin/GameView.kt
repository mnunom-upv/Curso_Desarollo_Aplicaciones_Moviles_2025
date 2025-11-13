package com.example.ponggame_kotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class GameView(context: Context?) : View(context) {
    private var paint: Paint? = null
    private val paddleWidth = 25
    private val paddleHeight = 180
    private val ballRadius = 25

    private var leftPaddleY = 0f
    private var rightPaddleY = 0f
    private val PADDLE_MARGIN = 30

    // Variables para la pelota
    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 10f // Velocidad horizontal
    private var ballSpeedY = 10f // Velocidad vertical
    private val INITIAL_BALL_SPEED = 10f // Nueva constante para la velocidad inicial
    private val MAX_PADDLE_EFFECT = 10f // Reducida de 15 a 8 para el efecto de las paletas


    // Variable para el bucle del juego
    private var handler: Handler? = null
    private val FRAME_RATE = 60
    private var isPlaying = true

    private var player1Score = 0
    private var player2Score = 0
    private var isWaitingToStart = false
    private var startWaitTime: Long = 0
    private var gameOver = false
    private var winnerMessage = ""
    private val winnerColor = Color.GREEN
    private var winningPlayer = 0 // 0 = ninguno, 1 = jugador 1, 2 = jugador 2
    private var restartButtonBounds: RectF? = null
    private var isRestartButtonVisible = false
    private var buttonPaint: Paint? = null

    interface ScoreUpdateListener {
        fun onScoreUpdate(player1Score: Int, player2Score: Int)
    }

    private var scoreUpdateListener: ScoreUpdateListener? = null

    fun setScoreUpdateListener(listener: ScoreUpdateListener?) {
        this.scoreUpdateListener = listener
    }


    init {
        init()
    }

    private fun init() {
        paint = Paint()
        paint!!.isAntiAlias = true
        // Inicializar el paint para el botón
        buttonPaint = Paint()
        buttonPaint!!.isAntiAlias = true
        restartButtonBounds = RectF()

        leftPaddleY = 0f
        rightPaddleY = 0f
        handler = Handler()
        systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or SYSTEM_UI_FLAG_FULLSCREEN
                or SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun restartGame() {
        // Reiniciar todas las variables del juego
        player1Score = 0
        player2Score = 0
        gameOver = false
        isRestartButtonVisible = false
        winnerMessage = ""
        winningPlayer = 0

        // Notificar el cambio de puntaje
        if (scoreUpdateListener != null) {
            scoreUpdateListener!!.onScoreUpdate(player1Score, player2Score)
        }

        // Reiniciar la pelota y el juego
        ballX = (width / 2).toFloat()
        ballY = (height / 2).toFloat()
        // Restaurar las velocidades iniciales
        ballSpeedX = INITIAL_BALL_SPEED
        ballSpeedY = INITIAL_BALL_SPEED
        isWaitingToStart = true
        startWaitTime = System.currentTimeMillis()
        isPlaying = true
        startGame()

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Centrar las paletas y la pelota
        leftPaddleY = ((h - paddleHeight) / 2).toFloat()
        rightPaddleY = ((h - paddleHeight) / 2).toFloat()
        ballX = (w / 2).toFloat()
        ballY = (h / 2).toFloat()

        // Iniciar el bucle del juego
        startGame()
    }

    private fun startGame() {
        handler!!.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying) {
                    updateGame()
                    handler!!.postDelayed(this, (1000 / FRAME_RATE).toLong())
                }
            }
        }, (1000 / FRAME_RATE).toLong())
    }

    private fun updateGame() {
        if (gameOver) {
            invalidate()
            return
        }

        if (isWaitingToStart) {
            if (System.currentTimeMillis() - startWaitTime >= WAIT_TIME) {
                isWaitingToStart = false
                // Mantener la velocidad constante pero dar dirección aleatoria
                ballSpeedX = (if (Math.random() > 0.5) INITIAL_BALL_SPEED else -INITIAL_BALL_SPEED)
                ballSpeedY = (if (Math.random() > 0.5) INITIAL_BALL_SPEED else -INITIAL_BALL_SPEED)
            }
            invalidate()
            return
        }

        ballX += ballSpeedX
        ballY += ballSpeedY

        if (ballY <= ballRadius || ballY >= height - ballRadius) {
            ballSpeedY = -ballSpeedY
        }

        // Colisión con paleta izquierda
        if (ballX <= PADDLE_MARGIN + paddleWidth + ballRadius && ballY >= leftPaddleY && ballY <= leftPaddleY + paddleHeight) {
            ballX = (PADDLE_MARGIN + paddleWidth + ballRadius).toFloat()
            ballSpeedX = abs(INITIAL_BALL_SPEED) // Asegurar velocidad constante
            val relativeIntersectY = (leftPaddleY + (paddleHeight / 2)) - ballY
            val normalizedRelativeIntersectionY = (relativeIntersectY / (paddleHeight / 2))
            ballSpeedY = normalizedRelativeIntersectionY * -MAX_PADDLE_EFFECT
        }

        // Colisión con paleta derecha
        if (ballX >= width - PADDLE_MARGIN - paddleWidth - ballRadius && ballY >= rightPaddleY && ballY <= rightPaddleY + paddleHeight) {
            ballX = (width - PADDLE_MARGIN - paddleWidth - ballRadius).toFloat()
            ballSpeedX = -abs(INITIAL_BALL_SPEED) // Asegurar velocidad constante
            val relativeIntersectY = (rightPaddleY + (paddleHeight / 2)) - ballY
            val normalizedRelativeIntersectionY = (relativeIntersectY / (paddleHeight / 2))
            ballSpeedY = normalizedRelativeIntersectionY * -MAX_PADDLE_EFFECT
        }

        // Verificar si la pelota sale de los límites y actualizar puntajes
        if (ballX < 0) {
            player2Score++
            checkWinCondition()
            if (scoreUpdateListener != null) {
                scoreUpdateListener!!.onScoreUpdate(player1Score, player2Score)
            }
            if (!gameOver) {
                resetBall()
            }
        } else if (ballX > width) {
            player1Score++
            checkWinCondition()
            if (scoreUpdateListener != null) {
                scoreUpdateListener!!.onScoreUpdate(player1Score, player2Score)
            }
            if (!gameOver) {
                resetBall()
            }
        }

        invalidate()
    }

    private fun checkWinCondition() {
        if (player1Score >= WINNING_SCORE) {
            gameOver = true
            winnerMessage = "¡JUGADOR AMARILLO HA GANADO!"
            winningPlayer = 1
            stopGame()
        } else if (player2Score >= WINNING_SCORE) {
            gameOver = true
            winnerMessage = "¡JUGADOR AZUL HA GANADO!"
            winningPlayer = 2
            stopGame()
        }
    }

    private fun stopGame() {
        isPlaying = false
        ballSpeedX = 0f
        ballSpeedY = 0f
        isRestartButtonVisible = true
    }

    private fun resetBall() {
        ballX = (width / 2).toFloat()
        ballY = (height / 2).toFloat()
        // No reiniciar la velocidad a 0, solo esperar
        isWaitingToStart = true
        startWaitTime = System.currentTimeMillis()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Verificar si se hizo clic en el botón de reinicio
        if (gameOver && event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y

            if (restartButtonBounds!!.contains(touchX, touchY)) {
                restartGame()
                return true
            }
        }

        // Solo procesar toques si el juego no ha terminado
        if (!gameOver) {
            val pointerCount = event.pointerCount
            val minY = 0f
            val maxY = (height - paddleHeight).toFloat()
            for (i in 0..<pointerCount) {
                val touchX = event.getX(i)
                val touchY = event.getY(i)
                if (touchX < width / 2) {
                    leftPaddleY = min(max(touchY - paddleHeight / 2, minY), maxY)
                } else {
                    rightPaddleY = min(max(touchY - paddleHeight / 2, minY), maxY)
                }
            }
            invalidate()
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Fondo negro
        canvas.drawColor(Color.parseColor("#3E8C1E"))
        if (!gameOver) {
            // Línea central
            paint!!.color = Color.WHITE
            paint!!.strokeWidth = 7f
            canvas.drawLine(
                (width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(),
                paint!!
            )

            // Paddle izquierdo (amarillo)
            paint!!.color = Color.YELLOW
            canvas.drawRect(
                PADDLE_MARGIN.toFloat(),
                leftPaddleY,
                (PADDLE_MARGIN + paddleWidth).toFloat(),
                leftPaddleY + paddleHeight,
                paint!!
            )

            // Paddle derecho (azul)
            paint!!.color = Color.BLUE
            canvas.drawRect(
                (width - paddleWidth - PADDLE_MARGIN).toFloat(),
                rightPaddleY,
                (width - PADDLE_MARGIN).toFloat(),
                rightPaddleY + paddleHeight,
                paint!!
            )

            // Pelota (blanca)
            paint!!.color = Color.WHITE
            canvas.drawCircle(
                ballX,
                ballY,
                ballRadius.toFloat(),
                paint!!
            )

            // Si está esperando para iniciar, mostrar la cuenta regresiva
            if (isWaitingToStart) {
                paint!!.color = Color.BLACK
                paint!!.textSize = 50f
                paint!!.textAlign = Paint.Align.CENTER
                paint!!.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
                val remainingTime =
                    (WAIT_TIME - (System.currentTimeMillis() - startWaitTime)) / 1000 + 1
                canvas.drawText(
                    remainingTime.toString(), (width / 2).toFloat(), (height / 2 - 50).toFloat(),
                    paint!!
                )
            }
        } else {
            // Dibujar mensaje de victoria
            paint!!.color = Color.GREEN // Color del texto
            paint!!.textSize = 70f // Tamaño grande para el mensaje
            paint!!.textAlign = Paint.Align.CENTER

            // Dibujar el mensaje con el color del jugador ganador
            val playerColor = if (winningPlayer == 1) "AMARILLO" else "AZUL"
            paint!!.color = Color.GREEN
            canvas.drawText(
                "¡JUGADOR", (width / 2).toFloat(), (height / 2 - 40).toFloat(),
                paint!!
            )

            // Cambiar al color del jugador ganador
            paint!!.color = if (winningPlayer == 1) Color.YELLOW else Color.BLUE
            canvas.drawText(
                playerColor, (width / 2).toFloat(), (height / 2 + 40).toFloat(),
                paint!!
            )

            paint!!.color = Color.GREEN
            canvas.drawText(
                "HA GANADO!", (width / 2).toFloat(), (height / 2 + 120).toFloat(),
                paint!!
            )

            // Dibujar botón de reinicio
            val buttonWidth = 400
            val buttonHeight = 100
            restartButtonBounds!![(width / 2 - buttonWidth / 2).toFloat(), (height / 2 + 200).toFloat(), (width / 2 + buttonWidth / 2).toFloat()] =
                (height / 2 + 200 + buttonHeight).toFloat()
            // Fondo del botón
            buttonPaint!!.color = Color.BLACK
            canvas.drawRoundRect(restartButtonBounds!!, 20f, 30f, buttonPaint!!)
            // Texto del botón
            paint!!.color = Color.WHITE
            paint!!.textSize = 40f
            paint!!.textAlign = Paint.Align.CENTER
            canvas.drawText(
                "VOLVER A JUGAR",
                restartButtonBounds!!.centerX(),
                restartButtonBounds!!.centerY() + 15,
                paint!!
            )
        }
    }

    // Asegurarse de detener el juego cuando la vista se destruye
    fun pauseGame() {
        isPlaying = false
    }

    fun resumeGame() {
        isPlaying = true
        startGame()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_FULLSCREEN
                    or SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    companion object {
        private const val WAIT_TIME: Long = 3000

        private const val WINNING_SCORE = 11
    }
}