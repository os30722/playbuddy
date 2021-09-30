package com.hfad.sports.ui.host

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.hfad.sports.databinding.FragmentSportListBinding
import com.hfad.sports.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SportListFragment : Fragment() {

    companion object {
        val EVENT_SELECTED_KEY = "Event_Selected_Requested_Key"
    }

    private var binding by viewBinding<FragmentSportListBinding>()
    private val sportViewModel: HostGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSportListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSportList()
    }



    private fun initSportList(){
        val sportList = ArrayList<String>()

        sportList.add("Football")
        sportList.add("Cricket")
        sportList.add("BasketBall")
        sportList.add("Swimming")
        sportList.add("Badminton")
        sportList.add("Tennis")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_activated_1, sportList)
        binding.sportList.adapter = adapter

        // For search
        binding.searchSport.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.sportList.onItemClickListener =
            AdapterView.OnItemClickListener { _, view, _, _ ->
                setFragmentResult(EVENT_SELECTED_KEY, bundleOf("event" to (view as TextView).text.toString()))
                sportViewModel.sportSelected = (view as TextView).text.toString()
                NavHostFragment.findNavController(this).popBackStack()
            }

    }

}