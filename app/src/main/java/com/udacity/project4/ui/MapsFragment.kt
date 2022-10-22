package com.udacity.project4.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentMapsBinding
import com.udacity.project4.viewModels.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class MapsFragment : Fragment(),MenuProvider,OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private val viewModel by sharedViewModel<SaveReminderViewModel>()
    private val REQUEST_LOCATION_PERMISSION = 1

    private var latitide: Double = 0.0
    private var longitude: Double = 0.0
    private var locationName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.saveButton.setOnClickListener {
            viewModel.reminderLongitude.value = longitude
            viewModel.reminderLatitude.value = latitide
            viewModel.reminderLocation.value = locationName
            navigateToReminderDetails()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.maps_menu,menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.normal_map_option -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_map_option -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_map_option -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_map_option -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> {false}
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val latitude = 31.207569
        val longitude = 29.879175
        val zoom = 16f

        val homeLatLong =LatLng(latitude,longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLong,zoom))
        map.addMarker(MarkerOptions().position(homeLatLong))

        setMapStyle(map)
        setPoiClick(map)
        setMapClick(map)
        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if(isPermissionGranted()) {
            Log.d("MainTest","fine location is granted")
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        }
        else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
           //ActivityCompat.requestPermissions(requireActivity(),
           //     arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_LOCATION_PERMISSION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style)
            )
            if(!success) {
                Log.d("MainTest","Style parsing failed")
            }
        } catch (ex: Exception) {
            Log.d("MainTest","can't find style Error ${ex.message}")
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            marker?.showInfoWindow()
            val lat = poi.latLng.latitude
            val long = poi.latLng.longitude
            val location = poi.name
            latitide = lat
            longitude = long
            locationName = location
        }
    }

    private  fun setMapClick(map: GoogleMap) {
        map.setOnMapClickListener { latLong ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLong.latitude,latLong.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLong)
                    .snippet(snippet)
                    .title("Dropped Pin")
                    //to change color of mark from red to blue
                    .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )

            val lat = latLong.latitude
            val long = latLong.longitude
            val location = Geocoder(requireContext(),Locale.getDefault()).getFromLocation(lat,long,1)
                ?.get(0)?.subAdminArea
            latitide = lat
            longitude = long
            locationName = location!!
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun navigateToReminderDetails() {
        findNavController().popBackStack()
    }

}