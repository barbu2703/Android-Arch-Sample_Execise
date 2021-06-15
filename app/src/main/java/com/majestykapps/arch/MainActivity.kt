package com.majestykapps.arch

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.majestykapps.arch.data.repository.TasksRepositoryImpl
import com.majestykapps.arch.data.source.local.TasksLocalDataSource
import com.majestykapps.arch.data.source.local.ToDoDatabase
import com.majestykapps.arch.presentation.common.ViewModelFactory
import com.majestykapps.arch.presentation.tasks.TasksFragment
import com.majestykapps.arch.presentation.tasks.TasksViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var tasksViewModel: TasksViewModel
    private var taskIdFromDeepLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Parse deep link if have
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        if (data != null) {
            val rawParam = data.toString()
            if (rawParam.startsWith("http://tasks.majestykapps.com/", true)) {
                taskIdFromDeepLink = rawParam.replace("http://tasks.majestykapps.com/", "")
            } else {
                taskIdFromDeepLink = ""
            }
        }

        tasksViewModel = initViewModel()
        initViewModelObservers()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, TasksFragment.newInstance())
                .commit()
        }
    }

    private fun initViewModel(): TasksViewModel {
        val tasksDao = ToDoDatabase.getInstance(applicationContext).taskDao()
        val localDataSource = TasksLocalDataSource.getInstance(tasksDao)
        val tasksRepository = TasksRepositoryImpl.getInstance(localDataSource)
        val factory = ViewModelFactory.getInstance(tasksRepository)
        return ViewModelProviders.of(this, factory).get(TasksViewModel::class.java)
    }

    private fun initViewModelObservers() {
        tasksViewModel.apply {
            launchEvent.observe(this@MainActivity, Observer { id ->
                Log.d(TAG, "launchTask: launching task with id = $id")
                // TODO add task detail fragment
            })

            // If app is called by deep link then turn on this observe
            if (!taskIdFromDeepLink.isEmpty()) {
                tasks.observe(this@MainActivity, Observer { tasks ->
                    // If deep link is detected, then go to detail page directly
                    val task = tasks.filter { it.id == taskIdFromDeepLink }
                    if (!task.isNullOrEmpty()) {
                        DetailActivity.navigateTODetailActivity(this@MainActivity, task.first())
                    }
                })
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
