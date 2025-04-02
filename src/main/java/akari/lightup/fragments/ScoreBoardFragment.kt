package akari.lightup.fragments


import akari.lightup.R
import akari.lightup.databinding.FragmentScoreBoardBinding
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ScoreBoardFragment : Fragment() {
    private lateinit var binding : FragmentScoreBoardBinding
    private lateinit var fragmentContext: Context
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreBoardBinding.inflate(inflater, container, false)
        fragmentContext = requireContext()
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var lvl1=loadScores(1)
        var lvl2=loadScores(2)
        var lvl3=loadScores(3)


        binding.scoreLevel1.text="Best score: $lvl1"
        binding.scoreLevel2.text="Best score: $lvl2"
        binding.scoreLevel3.text="Best score: $lvl3"


        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_scoreBoardFragment_to_menuFragment)
        }
    }


    private fun loadScores(lvl: Int): String {
        val file = File(fragmentContext.filesDir, getString(R.string.scores_json2))
        val jsonString = file.readText()

        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type
        val data: Map<String, String> = gson.fromJson(jsonString, type)
        return data["level$lvl"] as String
    }
}