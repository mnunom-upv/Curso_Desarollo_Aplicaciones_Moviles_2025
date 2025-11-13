package com.example.myapplication2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activit)
    }
}


class MainActivity : AppCompatActivity() {

    lateinit var B1_1 : ImageButton
    lateinit var B1_2 : ImageButton
    lateinit var B1_3 : ImageButton
    lateinit var B2_1 : ImageButton
    lateinit var B2_2 : ImageButton
    lateinit var B2_3 : ImageButton
    lateinit var B3_1 : ImageButton
    lateinit var B3_2 : ImageButton
    lateinit var B3_3 : ImageButton
    lateinit var TextViewPizarra : TextView
    // 1-Turno Cruz, 2-Turno Corazon
    var Turno : Int = 1
    var numRows : Int = 3
    var numCols : Int = 3
    lateinit var matrix: Array<Array<CeldaGato?>>
    // # de Celdas Marcadas Cruz
    var ConteoCruces : Int = 0
    // de Celdas Marcadas Corazon
    var ConteoCorazones : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InicializarBotonesConIds()
        AsignarListenerABotones()
        CrearMatrizEstatus ()
        ReiniciarControles()
    }

    fun InicializarBotonesConIds () {
        TextViewPizarra = findViewById(R.id.textView_Pizarra_Estatus)
        B1_1 = findViewById (R.id.boton1_1)
        B1_2 = findViewById (R.id.boton1_2)
        B1_3 = findViewById (R.id.boton1_3)
        B2_1 = findViewById (R.id.boton2_1)
        B2_2 = findViewById (R.id.boton2_2)
        B2_3 = findViewById (R.id.boton2_3)
        B3_1 = findViewById (R.id.boton3_1)
        B3_2 = findViewById (R.id.boton3_2)
        B3_3 = findViewById (R.id.boton3_3)
    }


    fun AsignarListenerABotones () {
        B1_1.setOnClickListener (btnListener)
        B1_2.setOnClickListener (btnListener)
        B1_3.setOnClickListener (btnListener)
        B2_1.setOnClickListener (btnListener)
        B2_2.setOnClickListener (btnListener)
        B2_3.setOnClickListener (btnListener)
        B3_1.setOnClickListener (btnListener)
        B3_2.setOnClickListener (btnListener)
        B3_3.setOnClickListener (btnListener)
    }

    val btnListener = View.OnClickListener {
        val (fila, columna) = ObtieneFilaColumna (it)
        //Toast.makeText(applicationContext,
        //    "PresionasteBoton ["+fila+","+columna+"]"+
        //            matrix[fila][columna]!!.deviceStatus,
        //    Toast.LENGTH_SHORT).show()
        //TextViewPizarra.text = "Alguna informacion util"
        val b: ImageButton = findViewById(it.id)
        CambiarEstadoCasilla (b, fila, columna)
        //ChecarAlgunGanadorFin(fila, columna)
        if (!ChecarAlgunGanadorFin(fila, columna))
            ChecarEmpate()
    }

    fun ChecarEmpate() {
        // Numero de Celdas Marcadas con Gato
        if (ConteoCruces + ConteoCorazones == 9) {
            MostrarAlertDialog("Empate")
        }
    }

    fun CrearMatrizEstatus() {
        matrix = Array(numRows) { row ->
            Array(numCols) { col ->
                CeldaGato(row, col)
            }
        }
    }

    fun ObtieneFilaColumna (it : View):  Pair<Int, Int> {
        var fila = 0
        var columna = 0
        when (it.id) {
            R.id.boton1_1 -> { fila = 1;  columna = 1 }
            R.id.boton1_2 -> { fila = 1 ; columna = 2 }
            R.id.boton1_3 -> { fila = 1;  columna = 3 }
            R.id.boton2_1 -> { fila = 2;  columna = 1 }
            R.id.boton2_2 -> { fila = 2;  columna = 2 }
            R.id.boton2_3 -> { fila = 2;  columna = 3 }
            R.id.boton3_1 -> { fila = 3;  columna = 1 }
            R.id.boton3_2 -> { fila = 3;  columna = 2 }
            R.id.boton3_3 -> { fila = 3;  columna = 3 }
            else -> {          fila = -1; columna = -1 }
        }
        return Pair(fila-1, columna-1)
    }

    fun ReiniciarControles() {
        for (i in 0..numRows - 1) {
            for (j in 0..numCols - 1) {
                matrix[i][j]!!.deviceStatus = "SIN_MARCA"
            }
        }
        B1_1.setImageResource(R.drawable.usuario)
        B1_2.setImageResource(R.drawable.usuario)
        B1_3.setImageResource(R.drawable.usuario)
        B2_1.setImageResource(R.drawable.usuario)
        B2_2.setImageResource(R.drawable.usuario)
        B2_3.setImageResource(R.drawable.usuario)
        B3_1.setImageResource(R.drawable.usuario)
        B3_2.setImageResource(R.drawable.usuario)
        B3_3.setImageResource(R.drawable.usuario)
        Turno = 1  // 1 - Turno Cruz, 2 - Turno Corazon
        ConteoCruces = 0  // Numero de Celdas MArcadas con Cruz
        ConteoCorazones = 0  // Numero de Celdas Marcadas con Gato

        TextViewPizarra.text = "Inicio del Juego: " +
                "\n Conteo Cruces: " + ConteoCruces +
                "\n Contro Corazones: " + ConteoCorazones
    }


    fun ActualizarEstatusTablero(fila: Int, columna: Int) {
        TextViewPizarra.text = "Ultimo Turno: " +
                matrix[fila][columna]!!.deviceStatus +
                "\n Conteo Cruces: " + ConteoCruces +
                "\n Contro Corazones: " + ConteoCorazones
    }









    fun CambiarEstadoCasilla (b : ImageButton, fila : Int, columna: Int) {
        if (matrix[fila][columna]!!.deviceStatus.equals("SIN_MARCA")) {
            if (Turno==1)  {
                b.setImageResource(R.drawable.cruz)
                matrix[fila][columna]!!.deviceStatus="CRUZ"
                ConteoCruces+=1
            } else {
                b.setImageResource(R.drawable.corazon)
                matrix[fila][columna]!!.deviceStatus="CORAZON"
                ConteoCorazones+=1
            }
            if (Turno == 1) Turno = 2 else Turno = 1
            TextViewPizarra.text = "Ultimo Turno: " +
                    matrix[fila][columna]!!.deviceStatus +
                    "\n Conteo Cruces: "+ConteoCruces+
                    "\n Contro Corazones: "+ConteoCorazones
            //Toast.makeText(applicationContext,
            // "PresionasteBoton"+fila+""+columna+matrix[fila][columna]!!.deviceStatus,
            // Toast.LENGTH_SHORT)
        }
        else {
            //Toast.makeText(applicationContext, "Casilla Ocupada con la marca "+
            // matrix[fila][columna]!!.deviceStatus + "Posicion "+fila+""+
        }
    }

    fun MostrarAlertDialog(C: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Juego Finalizado")
        builder.setMessage(C)
        builder.setCancelable(false)
        builder.setPositiveButton("Reiniciar") { dialog, which ->
            Toast.makeText(applicationContext, android.R.string.yes, Toast.LENGTH_SHORT).show()
            ReiniciarControles()
        }
        builder.setNegativeButton("Salir") { dialog, which ->
            Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.show()
    }


    fun ChecarGanadorPorFilas(): Triple<Boolean, Int, String> {
        // Recorrido por filas
        var Pivote: String = ""
        for (i in 0..numRows - 1) {
            Pivote = matrix[i][0]!!.deviceStatus
            for (j in 1..numCols - 1) {
                if (matrix[i][j]!!.deviceStatus.equals(Pivote))
                    Pivote = matrix[i][j]!!.deviceStatus
                else
                    Pivote = "NO"
            }
            if (Pivote.equals("CRUZ") || Pivote.equals("CORAZON")) {
                return Triple(true, i, Pivote)
            }
        }
        return Triple(false, -1, "No");
    }

    fun ChecarGanadorPorColumnas(): Triple<Boolean, Int, String> {
        // Recorrido por columnas
        var Pivote: String = ""
        for (i in 0..numCols - 1) {
            Pivote = matrix[0][i]!!.deviceStatus
            for (j in 1..numRows - 1) {
                if (matrix[j][i]!!.deviceStatus.equals(Pivote))
                    Pivote = matrix[j][i]!!.deviceStatus
                else
                    Pivote = "No"
            }
            if (Pivote.equals("CRUZ") || Pivote.equals("CORAZON")) {
                return Triple(true, i, Pivote)
            }
        }
        return Triple(false, -1, "NO");
    }

    fun ChecarGanadorPorDiagonales(): Triple<Boolean, Int, String> {
        var Pivote: String
        // Recorrido por diagonales
        Pivote = matrix[0][0]!!.deviceStatus
        for (i in 1..numRows - 1) {
            if (matrix[i][i]!!.deviceStatus.equals(Pivote))
                Pivote = matrix[i][i]!!.deviceStatus
            else
                Pivote = "NO"
        }
        if (Pivote.equals("CRUZ") || Pivote.equals("CORAZON")) {
            return Triple(true, 0, Pivote)
        }
        var Col = 2
        Pivote = matrix[0][Col]!!.deviceStatus
        for (i in 1..numRows - 1) {
            Col -= 1
            if (matrix[i][Col]!!.deviceStatus.equals(Pivote))
                Pivote = matrix[i][Col]!!.deviceStatus
            else
                Pivote = "NO"
        }
        if (Pivote.equals("CRUZ") || Pivote.equals("CORAZON")) {
            return Triple(true, 1, Pivote)
        }
        return Triple(false, -1, "NO");
    }

    fun ChecarAlgunGanadorFin(fila: Int, columna: Int): Boolean {
        if ((ConteoCorazones > 2) || (ConteoCruces > 2)) {
            // Checa si hay fila ganadora
            var (Ganador, Indice, Jugador) = ChecarGanadorPorFilas()
            if (Ganador) { // Hay una fila ganadora
                TextViewPizarra.text = "Gano: " + Jugador + " Fila: " + Indice
                MostrarAlertDialog("Ganador: " + Jugador)
                return (true)
            } else { // Checa si hay columna ganadora
                var (Ganador2, Indice2, Jugador2) = ChecarGanadorPorColumnas()
                if (Ganador2) { // Hay una columna ganadora
                    MostrarAlertDialog("Ganador: " + Jugador2)
                    TextViewPizarra.text = "Gano: " + Jugador2 + " Columna: " + Indice2
                    return true
                } else {
                    var (Ganador3, Indice3, Jugador3) = ChecarGanadorPorDiagonales()
                    if (Ganador3) { // Ganador por alguna columna
                        MostrarAlertDialog("Ganador: " + Jugador3)
                        TextViewPizarra.text = "Gano: " + Jugador3 + " Diagonal: " + Indice3
                        return true
                    } else
                        return (false)
                }
            }
        } else {
            ActualizarEstatusTablero(fila, columna)
        }
        return false
        //return false
    }

}

class CeldaGato (val row: Int, val col: Int) {
    var deviceStatus = "SIN_MARCA"
    constructor(row: Int, col: Int, statusCode: Int)
            : this(row, col) {
        deviceStatus = when (statusCode) {
            0 -> "TACHA"
            1 -> "CORAZON"
            else -> "SIN_MARCA"
        }
    }
}
