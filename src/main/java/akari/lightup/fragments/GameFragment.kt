package akari.lightup.fragments

import akari.lightup.Cell
import akari.lightup.R
import akari.lightup.databinding.FragmentGameBinding
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val board = ArrayList<ArrayList<Cell>>()
    private lateinit var timerRunnable: Runnable
    private lateinit var handler: Handler
    private var startTime: Long = 0
    private lateinit var fragmentContext: Context
    private lateinit var initBoard: List<List<Int>>
    private lateinit var solBoard: List<List<Int>>
    private lateinit var currentLvlScore: String
    private lateinit var formattedTime: String

    companion object {
        const val ARG_SELECTED_LEVEL = "level1"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        fragmentContext = requireContext()
        return binding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (row in 0 until 7) {
            val rowCells = ArrayList<Cell>()
            for (col in 0 until 7) {
                val buttonId = resources.getIdentifier("btn$row" + "_" + col, "id", requireContext().packageName)
                val button = view.findViewById<ImageButton>(buttonId)
                val cell = Cell(button, row, col)
                rowCells.add(cell)
            }
            board.add(rowCells)
        }
        val selectedLevel = arguments?.getInt(ARG_SELECTED_LEVEL, 0) ?: 0
        initBoard=loadMap("lvl",selectedLevel)
        solBoard=loadMap("sol",selectedLevel)
        currentLvlScore=loadScores(selectedLevel)
        tickTimer()
        initialise(initBoard)
        setBulbOnClick()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
        }
        binding.btnDone.setOnClickListener()
        {

            if(checkSolution())
            {
                Toast.makeText(requireContext(), "Congratulations!", Toast.LENGTH_SHORT).show()
                handler.removeCallbacks(timerRunnable)
                binding.btnReset.isEnabled=false
                binding.btnDone.isEnabled=false
                modifyTime(formattedTime,selectedLevel)
                endGame()
            }
            else
            {
                Toast.makeText(requireContext(), "Incorrect Solution!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnReset.setOnClickListener(){
            resetMap()
        }
    }

    private fun loadScores(lvl: Int): String {
        val file = File(fragmentContext.filesDir, "scores.json")
        val jsonString = file.readText()

        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type
        val data: Map<String, String> = gson.fromJson(jsonString, type)
        return data["level$lvl"] as String
    }

    private fun timeStringToMinutes(timeString: String?): Int {
        if (timeString != null) {
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                return hours * 60 + minutes
            }
        }
        return 0 // Return default value or handle the case when timeString is null or malformed
    }

    private fun compareTimes(time1: String?, time2: String): Boolean {
        val minutes1 = timeStringToMinutes(time1)
        val minutes2 = timeStringToMinutes(time2)
        return minutes1 > minutes2
    }

    private fun modifyTime(newTime: String, lvl: Int) {
        val fileName = "scores.json"
        val fileContents = fragmentContext.openFileInput(fileName).bufferedReader().use { it.readText() }

        val gson = Gson()
        val data = gson.fromJson(fileContents, object : TypeToken<Map<String, String>>() {}.type) as MutableMap<String, String>

        if(compareTimes(data["level$lvl"],newTime))
            data["level$lvl"]=newTime

        val modifiedJsonString = gson.toJson(data)

        fragmentContext.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(modifiedJsonString.toByteArray())
        }
    }


    private fun checkSolution():Boolean{

        var sol:Boolean=true;
        for(row in 0 until 7)
        {
            for(col in 0 until 7)
            {
                if(solBoard[row][col]==7)
                {
                    if(board[row][col].getType()!="bulb")
                    {
                        sol=false
                    }
                }
            }
        }

        return sol
    }

    private fun resetMap()
    {
        for(row in 0 until 7)
        {
            for(col in 0 until 7)
            {
                if(board[row][col].getType()=="lit" || board[row][col].getType()=="empty")
                {
                    board[row][col].setLightLevel(0)
                }
                else if( board[row][col].getType()=="bulb")
                {
                    board[row][col].setLightLevel(0)
                    val color = Color.rgb((0.5f * 255).toInt(), (0.5f * 255).toInt(), (0.5f * 255).toInt()) //Grey
                    board[row][col].getButton().setBackgroundColor(color)
                    board[row][col].getButton().setImageResource(R.drawable.empty)
                    board[row][col].setType("empty")
                }
            }
        }
        updateBoard()
    }

    private fun loadMap(type:String,lvl: Int): List<List<Int>> {
        val inputStream = fragmentContext.assets.open("$type$lvl.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        val jsonString = String(buffer, Charsets.UTF_8)

        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val data: Map<String, Any> = gson.fromJson(jsonString, type)
        return data["board"] as List<List<Int>>
    }




    private fun tickTimer()
    {
        handler = Handler()
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = SystemClock.elapsedRealtime() - startTime
                val seconds = (elapsedTime / 1000).toInt()
                val minutes = seconds / 60
                formattedTime = String.format("%02d:%02d", minutes, seconds % 60)
                binding.textTimer.text = formattedTime
                handler.postDelayed(this, 1000)
            }
        }
        startTime = SystemClock.elapsedRealtime()
        handler.postDelayed(timerRunnable, 0)

    }

    private fun initialise(ib: List<List<Int>>)
    {
        var nextButtonType: Int
        val block0: Int = R.drawable.block0
        val block1: Int = R.drawable.block1
        val block2: Int = R.drawable.block2
        val block3: Int = R.drawable.block3
        val block4: Int = R.drawable.block4
        for(row in 0 until 7)
        {
            for(col in 0 until 7)
            {
                nextButtonType=ib[row][col]
                if (nextButtonType <= 4) {
                    board[row][col].setType("block$nextButtonType")
                    val color = Color.rgb((0f * 255).toInt(), (0f * 255).toInt(), (0f * 255).toInt())
                    board[row][col].getButton().setBackgroundColor(color)
                    when (nextButtonType) {
                        0 -> board[row][col].getButton().setImageResource(block0)
                        1 -> board[row][col].getButton().setImageResource(block1)
                        2 -> board[row][col].getButton().setImageResource(block2)
                        3 -> board[row][col].getButton().setImageResource(block3)
                        4 -> board[row][col].getButton().setImageResource(block4)
                    }
                }
                else if(nextButtonType==5)
                {
                    board[row][col].setType("block");
                    val color = Color.rgb((0f * 255).toInt(), (0f * 255).toInt(), (0f * 255).toInt())
                    board[row][col].getButton().setBackgroundColor(color)
                }
                else
                {
                    board[row][col].setType("empty");
                    val color = Color.rgb((0.5f * 255).toInt(), (0.5f * 255).toInt(), (0.5f * 255).toInt())
                    board[row][col].getButton().setBackgroundColor(color)
                }
            }
        }
    }

    private fun setBulbOnClick()
    {
        for (row in 0 until 7) {
            for (col in 0 until 7)
            {
                    board[row][col].getButton().setOnClickListener()
                    {
                        when(board[row][col].getType())
                        {
                            "empty" ->
                            {

                                val color = Color.rgb((1f * 255).toInt(), (1f * 255).toInt(), (0f * 255).toInt()) //Yellow
                                board[row][col].getButton().setBackgroundColor(color)
                                board[row][col].getButton().setImageResource(R.drawable.bulb)
                                board[row][col].setType("bulb")
                                addRays(board[row][col])
                            }

                            "bulb" ->
                            {

                                val color = Color.rgb((0.5f * 255).toInt(), (0.5f * 255).toInt(), (0.5f * 255).toInt()) //Grey
                                board[row][col].getButton().setBackgroundColor(color)
                                board[row][col].getButton().setImageResource(R.drawable.empty)
                                board[row][col].setType("empty")
                                subRays(board[row][col])
                            }
                        }
                    }
            }
        }
    }

    //add rays from bulb up
    private fun addFromBulbUp(row: Int, col: Int)
    {
        var row = row
        while (row > 0) {
            row--
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].addLight()
            }
            else
            {
                row = 0
            }
        }
    }

    //add rays from bulb left
    private fun addFromBulbLeft(row: Int, col: Int)
    {
        var col = col
        while (col > 0) {
            col--
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].addLight()
            }
            else
            {
                col = 0
            }
        }
    }

    //add rays from bulb down
    private fun addFromBulbDown(row: Int, col: Int)
    {
        var row = row
        while (row < 7 - 1) {
            row++
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].addLight()
            }
            else
            {
                row = 7
            }
        }
    }

    //add rays from bulb right
    private fun addFromBulbRight(row: Int, col: Int)
    {
        var col = col
        while (col < 7 - 1)
        {
            col++
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].addLight()
            }
            else
            {
                col = 7
            }
        }
    }

    //sub rays from bulb up
    private fun subFromBulbUp(row: Int, col: Int)
    {
        var row = row
        while (row > 0)
        {
            row--
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].subLight()
            }
            else
            {
                row = 0
            }
        }
    }

    //sub rays from bulb left
    private fun subFromBulbLeft(row: Int, col: Int)
    {
        var col = col
        while (col > 0)
        {
            col--
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].subLight()
            }
            else
            {
                col = 0
            }
        }
    }

    //sub rays from bulb down
    private fun subFromBulbDown(row: Int, col: Int)
    {
        var row = row
        while (row < 7- 1)
        {
            row++
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].subLight()
            }
            else
            {
                row = 7
            }
        }
    }

    //sub rays from bulb right
    private fun subFromBulbRight(row: Int, col: Int)
    {
        var col = col
        while (col < 7 - 1)
        {
            col++
            if (board[row][col].getType().equals("empty") || board[row][col].getType().equals("lit"))
            {
                board[row][col].subLight()
            }
            else
            {
                col = 7
            }
        }
    }

    private fun addRays(cell: Cell)
    {
        var r=cell.getRow()
        var c=cell.getCol()
        addFromBulbUp(r,c)
        addFromBulbLeft(r,c)
        addFromBulbDown(r,c)
        addFromBulbRight(r,c)
        updateBoard()
    }

    private fun subRays(cell: Cell)
    {
        var r=cell.getRow()
        var c=cell.getCol()
        subFromBulbUp(r,c)
        subFromBulbDown(r,c)
        subFromBulbLeft(r,c)
        subFromBulbRight(r,c)
        updateBoard()
    }



    //Change color of board according to light level
    private fun updateBoard()
    {
        for (row in 0 until 7)
        {
            for (col in 0 until 7)
            {
                when (board[row][col].getType())
                {
                    "lit" ->
                    {
                        if (board[row][col].getLightLevel() == 0)
                        {
                            val color = Color.rgb((0.5f * 255).toInt(), (0.5f * 255).toInt(), (0.5f * 255).toInt()) //Grey
                            board[row][col].getButton().setBackgroundColor(color)
                            board[row][col].setType("empty")
                        }
                    }

                    "empty" ->
                    {
                        if (board[row][col].getLightLevel() > 0)
                        {
                            val color = Color.rgb((1f * 255).toInt(), (1f * 255).toInt(), (0f * 255).toInt()) //Yellow
                            board[row][col].getButton().setBackgroundColor(color)
                            board[row][col].setType("lit")
                        }
                    }
                }
            }
        }
    }

    private fun endGame() {
        for (row in 0 until 7) {
            for (col in 0 until 7) {
                 board[row][col].getButton().isEnabled = false
            }
        }
    }
}
