package com.hfad.sports.ui.auth


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.hfad.sports.R
import com.hfad.sports.api.UserApi
import com.hfad.sports.databinding.ActivityAuthBinding
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.vo.LoggedUser
import com.hfad.sports.vo.Token
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class AuthActivity : AppCompatActivity(), UserAuthorizedListener {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        initNavigation()

    }

    private fun initNavigation() {
        val navController = findNavController(binding.navHostFragment.id)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.login) {
                binding.authToolbar.visibility = View.GONE
            } else {
                binding.authToolbar.visibility = View.VISIBLE
            }
        }

        binding.authToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onUserAuthorized(tokens: Token) {
        TokenToolkit.saveTokens(tokens)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(object :
            OnCompleteListener<String> {
            override fun onComplete(task: Task<String>) {
                if (!task.isSuccessful()) {
                    Log.w("debug64", "Failed Device Registration");
                    return
                }

                val call = UserApi.create().registerDevice(LoggedUser(task.result))
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("debug64", "Success")
                        finish()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("debug64", t.message.toString())
                    }

                })


            }


        })


    }


}