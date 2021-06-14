package com.majestykapps.arch.presentation.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.majestykapps.arch.R
import com.majestykapps.arch.presentation.adapter.TaskListAdapter
import kotlinx.android.synthetic.main.fragment_tasks.swipeRefresh
import kotlinx.android.synthetic.main.fragment_tasks.list_recycler_view

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        // Set Decoration To RecycledView
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false).apply {
            list_recycler_view.layoutManager = this
        }

        DividerItemDecoration(
            activity, // context
            layoutManager.orientation
        ).apply {
            list_recycler_view.addItemDecoration(this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        viewModel.apply {
            loadingEvent.observe(viewLifecycleOwner, Observer { isRefreshing ->
                Log.d(TAG, "loadingEvent observed")
                swipeRefresh.isRefreshing = isRefreshing
            })

            errorEvent.observe(viewLifecycleOwner, Observer { throwable ->
                Log.e(TAG, "errorEvent observed", throwable)
                //text.text = throwable.localizedMessage
                // TODO show error
            })

            tasks.observe(viewLifecycleOwner, Observer { tasks ->
                Log.d(TAG, "tasks observed: $tasks")
                //text.text = tasks.toString()

                list_recycler_view.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = TaskListAdapter(tasks)
                }
            })
        }
    }

    companion object {
        private const val TAG = "TasksFragment"

        fun newInstance() = TasksFragment()
    }
}