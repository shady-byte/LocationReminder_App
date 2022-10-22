package com.udacity.project4.ui

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentHomeBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            //val user = FirebaseAuth.getInstance().currentUser
            //R.id.action_homeFragment_to_remindersListFragment
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRemindersListFragment())
        } else {
            Log.d("HomeTest","user failed signed in ")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
        val x = mutableListOf<String>()
        //Listeners
        binding.loginButton.setOnClickListener {
            launchSignInFlow()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_UP &&  keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().finish()
            }
            false
        }
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
             signInLauncher.launch(signInIntent)
    }

}