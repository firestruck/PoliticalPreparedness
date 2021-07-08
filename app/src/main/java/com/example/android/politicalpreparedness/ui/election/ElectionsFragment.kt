package com.example.android.politicalpreparedness.ui.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import com.example.android.politicalpreparedness.ui.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.ui.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {
    lateinit var db: ElectionDatabase

    //DONE: Declare ViewModel
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ElectionsViewModel




    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        db = ElectionDatabase.getInstance(requireActivity())

        //DONE: Add ViewModel values and create ViewModel

        viewModel = ViewModelProvider(this, ElectionsViewModelFactory(this.requireContext(), PoliticalPreparednessRepository(db))).get(ElectionsViewModel::class.java)

        //DONE: Add binding values
        _binding = FragmentElectionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel


        //DONE: Link elections to voter info

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id,it.division))
                viewModel.onNavigationCompleted()
            }
        })

        //DONE: Initiate recycler adapters

        val upcomingAdapter = ElectionListAdapter(ElectionListener{ election ->
            election.let {
                viewModel.electionClickListener(election)
            }
        })
        val savedAdapter = ElectionListAdapter(ElectionListener{ saved ->
            saved.let {
                viewModel.electionClickListener(saved)
            }
        })

        binding.rvUpcomingElections.adapter = upcomingAdapter
        binding.rvSavedElections.adapter = savedAdapter

        //DONE: Populate recycler adapters

        viewModel.upcomingElection.observe(viewLifecycleOwner, Observer {
            it.let {
                upcomingAdapter.submitList(it)
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            it.let {
                savedAdapter.submitList(it)
            }
        })

        return binding.root

    }

    //DONE: Refresh adapters when fragment loads

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshAdapters()
    }

}