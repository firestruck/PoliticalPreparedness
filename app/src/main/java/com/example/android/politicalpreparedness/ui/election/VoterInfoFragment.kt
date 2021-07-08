package com.example.android.politicalpreparedness.ui.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    private  var _binding: FragmentVoterInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {



        //DONE: Add ViewModel values and create ViewModel

        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        val database = ElectionDatabase.getInstance(requireContext())
        val voterInfoViewModelFactory = VoterInfoViewModelFactory(
            PoliticalPreparednessRepository(database), electionId, division)
        viewModel = ViewModelProvider(this, voterInfoViewModelFactory).get(VoterInfoViewModel::class.java)



        //DONE: Add binding values

        _binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel


        //DONE: Populate voter info -- hide views without provided data.

        viewModel.getVoterInformation()
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //DONE: Handle loading of URLs

        viewModel.eventUrl.observe(viewLifecycleOwner, Observer {
            it?.let {
                webViewIntent(it)
                viewModel.onWebviewCompleted()
            }
        })

        viewModel.ballotUrl.observe(viewLifecycleOwner, Observer {
            it?.let {
                webViewIntent(it)
                viewModel.onBallotNavigated()
            }
        })



        //DONE: Handle save button UI state
        //DONE: cont'd Handle save button clicks

        viewModel.electionStatus().observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    binding.btnFollowElections.text = getString(R.string.txt_unfollow)
                }else{
                    binding.btnFollowElections.text = getText(R.string.txt_follow)
                }
            }
        })


        return binding.root


    }

    //DONE: Create method to load URL intents

    private fun webViewIntent(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}