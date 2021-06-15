package com.majestykapps.arch.presentation.tasks

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.majestykapps.arch.DetailActivity
import com.majestykapps.arch.R
import com.majestykapps.arch.domain.entity.Task
import com.majestykapps.arch.presentation.adapter.TaskListAdapter
import com.yalantis.jellytoolbar.listener.JellyListener
import kotlinx.android.synthetic.main.fragment_tasks.*

/**
 * @description     Tasks List Fragment
 *
 * @author          Adrian
 */
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by activityViewModels()
    private var editText: AppCompatEditText? = null

    private var allTasks: List<Task>? = null

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

        // Tool bar
        editText = LayoutInflater.from(activity).inflate(R.layout.edit_text, null) as AppCompatEditText
        editText!!.setBackgroundResource(android.R.color.transparent)
        editText!!.maxLines = 1
        editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val key = editText!!.text.toString()
                if (key.isEmpty()) {
                    updateTaskList(allTasks)
                } else {
                    val filteredTask = allTasks!!.filter { it.title.contains(key, true) || it.description.contains(key, true) }
                    updateTaskList(filteredTask)
                }
            }
        })

        toolbar.contentView = editText

        toolbar.jellyListener = jellyListener
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
                showError()
            })

            tasks.observe(viewLifecycleOwner, Observer { tasks ->
                Log.d(TAG, "tasks observed: $tasks")

                allTasks = tasks
                updateTaskList(tasks)
            })
        }
    }

    private fun updateTaskList(tasks: List<Task>?) {
        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = tasks?.let {
                TaskListAdapter(it) { task: Task ->
                    activity?.let {
                        DetailActivity.navigateTODetailActivity(it, task)
                    }
                }
            }
        }
    }

    private val jellyListener: JellyListener = object : JellyListener() {
        override fun onToolbarExpandingStarted() {
            toolbar.toolbar!!.title = ""
        }

        override fun onToolbarCollapsingStarted() {
            toolbar.toolbar!!.title = getString(R.string.app_name)
        }

        override fun onCancelIconClicked() {
            if (TextUtils.isEmpty(editText!!.text)) {
                toolbar.collapse()
            } else {
                editText!!.text!!.clear()
            }
        }
    }

    private fun showError() {
        view?.let {
            Snackbar.make(it, R.string.error_failed_catch_data, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) {
                    viewModel.refresh()
                }
                .show()
        }
    }

    companion object {
        private const val TAG = "TasksFragment"

        fun newInstance() = TasksFragment()
    }
}