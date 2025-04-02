package akari.lightup.fragments

import akari.lightup.R
import akari.lightup.databinding.FragmentLevelSelectionBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController


class LevelSelectionFragment : Fragment() {
    private lateinit var binding : FragmentLevelSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLevelSelectionBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLvl1.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(GameFragment.ARG_SELECTED_LEVEL, 1)
            findNavController().navigate(R.id.action_levelSelectionFragment_to_gameFragment, bundle)
        }

        binding.btnLvl2.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(GameFragment.ARG_SELECTED_LEVEL, 2)
            findNavController().navigate(R.id.action_levelSelectionFragment_to_gameFragment, bundle)
        }

        binding.btnLvl3.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(GameFragment.ARG_SELECTED_LEVEL, 3)
            findNavController().navigate(R.id.action_levelSelectionFragment_to_gameFragment, bundle)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_levelSelectionFragment_to_menuFragment)
        }
    }
}