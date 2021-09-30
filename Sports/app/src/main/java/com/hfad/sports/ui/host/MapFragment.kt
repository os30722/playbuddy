package com.hfad.sports.ui.host

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentMapBinding
import com.hfad.sports.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentMapBinding by viewBinding()
    private lateinit var mapView: MapView
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var gMap: GoogleMap
    private var locationPermission: Boolean = false
    private val locationModel: HostGameViewModel by hiltNavGraphViewModels(R.id.host_game_nav)
    private val DEFAULT_MAP_ZOOM: Float = 20f
    private var cancellationTokenSource = CancellationTokenSource()


    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            locationPermission = isGranted
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.viewModel = locationModel
        mapView = binding.mapView

        mapView.onCreate(savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY))
        mapView.getMapAsync(this)

        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchLocation.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                locationModel.geoLocate()
            }
            false
        }

        binding.setLocation.setOnClickListener{
            NavHostFragment.findNavController(this).navigateUp()
        }

    }

    private fun setPoint(location: LatLng) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_MAP_ZOOM))
        val pointMarker = MarkerOptions().position(location)
        gMap.clear()
        gMap.addMarker(pointMarker)

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY) ?: Bundle().also {
            outState.putBundle(MAPVIEW_BUNDLE_KEY, it)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }


    override fun onMapReady(map: GoogleMap) {
        Toast.makeText(requireActivity(), "Map Ready", Toast.LENGTH_SHORT).show()
        gMap = map
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        gMap.isMyLocationEnabled = true
        gMap.uiSettings.isMyLocationButtonEnabled = false
        gMap.uiSettings.isCompassEnabled = false

        gMap.setOnMapClickListener { location -> locationModel.getAddress(location) }


        locationModel.location.observe(viewLifecycleOwner, { latLng ->
            setPoint(latLng)
        })

        binding.currentLocation.setOnClickListener{getDeviceLocation()}

    }

    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        val currentLocationTask: Task<Location> = fusedLocationProvider.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )

        currentLocationTask.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                val location: Location = task.result
                locationModel.getAddress(LatLng(location.latitude, location.longitude))
                Log.d("debug", "Latitude ${location.latitude} Longitude ${location.longitude}")

            } else {
                val exception = task.exception
                Log.d("debug64", exception?.message.toString())
            }

        }



    }


    override fun onResume() {
        super.onResume()
        requireActivity().actionBar?.hide()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }



    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        private const val REQUEST_CHECK_SETTINGS = 3
    }

}