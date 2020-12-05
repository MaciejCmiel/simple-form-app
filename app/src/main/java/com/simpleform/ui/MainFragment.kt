package com.simpleform.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.simpleform.R
import com.simpleform.ViewModelFactory
import kotlinx.android.synthetic.main.main_fragment.*
import timber.log.Timber

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ElementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(MainViewModel::class.java)

        setupObserver()
        showLoadingIndicator(
            getString(R.string.fetching_form_text)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ElementsAdapter(arrayListOf())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter

        btnSave.setOnClickListener {
            Timber.d("Data: ${ElementsAdapter.editElements}")
            viewModel.sendFilledForm(ElementsAdapter.editElements)
        }
    }

    private fun setupObserver() {
        viewModel.getElements().observe(viewLifecycleOwner, {

            hideLoadingIndicator()

            if (it == null) {
                //TODO show error

            } else {
                adapter.addData(it)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.isUpdating().observe(viewLifecycleOwner, {
            if (it) {
                showLoadingIndicator(
                    getString(R.string.please_wait_text)
                )
            } else {
                hideLoadingIndicator()
            }
        })

        viewModel.updateFinishedSuccessfully.observe(viewLifecycleOwner, {
            if (it) {
                showSuccess()
            } else {
                showError()
            }
        })
    }

    private fun showError() {
        hideLoadingIndicator()
        tvLoadingIndicator.visibility = View.VISIBLE
        tvLoadingIndicator.text = getString(R.string.error_message)
    }

    private fun showSuccess() {
        hideLoadingIndicator()
        tvLoadingIndicator.visibility = View.VISIBLE
        tvLoadingIndicator.text = getString(R.string.success_message)
    }

    private fun showLoadingIndicator(loadingText: String) {
        loadingIndicator.visibility = View.VISIBLE
        tvLoadingIndicator.visibility = View.VISIBLE
        tvLoadingIndicator.text = loadingText
        btnSave.text = ""
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
        tvLoadingIndicator.visibility = View.GONE
        btnSave.text = getString(R.string.save_button_text)
    }

}