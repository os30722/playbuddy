package com.hfad.sports.ui.host

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentSecondHostGameBinding
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.Resource

class SecondHostGameFragment : Fragment() {

    private var binding: FragmentSecondHostGameBinding by viewBinding()
    private val eventModel: HostGameViewModel by hiltNavGraphViewModels(R.id.host_game_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSecondHostGameBinding.inflate(inflater, container, false)
        binding.event = eventModel

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectLocation.editText?.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_second_host_game_to_map_fragment))

        initHostEvent();
    }

    fun initHostEvent(){
        eventModel.state.observe(viewLifecycleOwner, {resp ->
            loadingUi(resp is Resource.Loading)

            when(resp){

                is Resource.Success -> {
                    findNavController().navigate(R.id.action_second_host_game_to_first_host_game)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(),"Connection Error", Toast.LENGTH_LONG).show()
                }
            }

        })
    }


    private fun loadingUi(enable: Boolean){
        binding.progressBar.visibility = View.VISIBLE
        binding.navDone.visibility = if (enable) View.INVISIBLE else View.VISIBLE
    }


}