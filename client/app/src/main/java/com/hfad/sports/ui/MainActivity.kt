package com.hfad.sports.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.hfad.sports.R
import com.hfad.sports.api.UserApi

import com.hfad.sports.databinding.ActivityMainBinding
import com.hfad.sports.service.MessageService
import com.hfad.sports.ui.auth.AuthActivity
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.vo.LoggedUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private val ERROR_DIALOG_REQUEST = 5
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2
    private var locationPermissionGranted: Boolean = false
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {

        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    override fun onStart() {
        super.onStart()

        // Starting messaging service
        Intent(this, MessageService::class.java).also {intent ->
         bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!TokenToolkit.containsToken()){
            val mainScreen = Intent(applicationContext, AuthActivity::class.java)
            mainScreen.putExtra("resume", false)
            startActivity(mainScreen)
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

    }

    private fun initNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(binding.mainNavHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setSupportActionBar(binding.mainToolbar)

        // Customize toolbar and bottom nav bar for each screen
        navController.addOnDestinationChangedListener{_, destination,_ ->
            if (destination.parent?.id == R.id.host_game_nav
                || destination.id == R.id.event_info || destination.id == R.id.join_request
                || destination.id == R.id.chat) {

                binding.bottomNavBar.visibility = View.GONE

                // We don't want toolbar in map fragment
                if(destination.id == R.id.map_fragment || destination.id == R.id.event_info)
                    binding.mainToolbar.visibility = View.GONE
                else   binding.mainToolbar.visibility = View.VISIBLE



            } else{
                binding.bottomNavBar.visibility = View.VISIBLE
                binding.mainToolbar.visibility = View.VISIBLE

            }
        }


        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavBar.setupWithNavController(navController)
    }

    // Checking Google Play Services
    private fun isServiceEnabled(): Boolean{

        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        if(available == ConnectionResult.SUCCESS){
            return true
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST)
            dialog.show()
        } else{
            Toast.makeText(this, "Google Play Service Is Not Enabled", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        isServiceEnabled()

    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return findNavController(binding.mainNavHostFragment.id).navigateUp(appBarConfiguration)
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

}