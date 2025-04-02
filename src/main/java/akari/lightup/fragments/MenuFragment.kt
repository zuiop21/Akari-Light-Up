package akari.lightup.fragments

import akari.lightup.R
import akari.lightup.databinding.FragmentMenuBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlin.system.exitProcess


class MenuFragment : Fragment() {
    private lateinit var binding : FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlay.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_levelSelectionFragment)
        }
        binding.btnScoreboard.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_scoreBoardFragment)
        }

        binding.btnExit.setOnClickListener {
            exitProcess(0);
        }
    }
}