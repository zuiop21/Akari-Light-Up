package akari.lightup

import android.widget.Button
import android.widget.ImageButton

class Cell(b: ImageButton, r: Int, c: Int) {
    private var lightLevel = 0
    private lateinit var type: String
    private val row: Int
    private val col: Int
    private val button: ImageButton

    init {
        button = b
        row = r
        col = c
    }

    fun setLightLevel(l:Int)
    {
        lightLevel=l
    }

    fun getLightLevel(): Int {
        return lightLevel
    }

    fun setType(t: String) {
        type = t
    }

    fun getType(): String? {
        return type
    }

    fun getRow(): Int {
        return row
    }

    fun getCol(): Int {
        return col
    }

    fun addLight() {
        lightLevel++
    }

    fun subLight() {
        lightLevel--
    }

    fun getButton(): ImageButton {
        return button
    }
}