package com.example.android.politicalpreparedness.ui.representative

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import com.example.android.politicalpreparedness.ui.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.ui.representative.adapter.RepresentativeListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment(), RepresentativeListener {

    companion object {
        //DONE: Add Constant for Location request
        private val REQUEST_CODE_LOCATION_PERMISSION = 1

    }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentRepresentativeBinding
    //DONE: Declare ViewModel
    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var adapter: RepresentativeListAdapter



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val database = ElectionDatabase.getInstance(requireContext())
        val representativeViewModelFactory = RepresentativeViewModelFactory(
            PoliticalPreparednessRepository(database)
        )
        //DONE: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this, representativeViewModelFactory).get(RepresentativeViewModel::class.java)
        binding.viewModel = viewModel

        //DONE: Define and assign Representative adapter
        adapter = RepresentativeListAdapter(this)
        binding.rvRepresentatives.adapter = adapter

        //DONE: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })


        viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getRepresentatives(it)
            }
        })

        //DONE: Establish button listeners for field and location search

        binding.buttonLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                getCurrentLocation()
            }
        }

        binding.buttonSearch.setOnClickListener {

            val addressLine1 = binding.addressLine1.text
            val addressLine2 = binding.addressLine2.text
            val state = binding.state.getItemAtPosition(binding.state.selectedItemPosition)
            val city = binding.city.text
            val zip = binding.zip.text

            val address = "$addressLine2 $addressLine1, $city, $state, $zip"
            viewModel.getRepresentatives(address)
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //DONE: Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            Snackbar.make(requireView(), "You need Permission!", Snackbar.LENGTH_LONG)
                .setAction("Settings") {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })

                }.show()
        }
    }



    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //DONE: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }



    private fun getCurrentLocation() {
        try {
            if (checkLocationPermissions()) {
                //DONE: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
                LocationServices.getFusedLocationProviderClient(requireContext())
                    .lastLocation.addOnSuccessListener { location ->
                        viewModel.getAddress(geoCodeLocation(location))
                        viewModel.getRepresentatives(viewModel.address.value.toString())
                    }
            }
            else {

                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
            }

        } catch (e: SecurityException) {
            Snackbar.make(requireView(), "Permission Denied", Snackbar.LENGTH_LONG).show()
        }

    }



    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun navigateToBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }



}