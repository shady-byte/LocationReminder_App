package com.udacity.project4.ui

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.udacity.project4.R
import com.udacity.project4.adapters.ReminderListAdapter
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.databinding.FragmentRemindersListBinding
import com.udacity.project4.viewModels.RemindersListViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class RemindersListFragment : Fragment(),MenuProvider{
    private lateinit var binding: FragmentRemindersListBinding
    private val viewModel by viewModel<RemindersListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reminders_list, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //Listeners
        //viewModel.clearReminders()
        binding.listFab.setOnClickListener {
            findNavController().navigate(RemindersListFragmentDirections.actionRemindersListFragmentToReminderDetailsFragment2())
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.remindersList.adapter = ReminderListAdapter()

        //Observables
        viewModel.remindersList.observe(viewLifecycleOwner) {
            it?.let {
                if(it is Result.Success<List<ReminderData>>) {
                    if(it.data.isEmpty()) {
                        binding.noDataImage.visibility = View.VISIBLE
                        binding.noDataText.visibility = View.VISIBLE
                    } else {
                        binding.noDataImage.visibility = View.GONE
                        binding.noDataText.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                if(it) {
                    Snackbar.make(binding.remindersList,"Something went wrong, please try later.",
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu,menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.logout_item -> {
                logoutOfApp()
            }
            else -> {}
        }
        return  false
    }

    private fun logoutOfApp() {
        AuthUI.getInstance().signOut(requireContext())
    }
}
