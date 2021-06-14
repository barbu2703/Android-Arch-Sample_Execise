package com.majestykapps.arch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.majestykapps.arch.domain.entity.Task
import kotlinx.android.synthetic.main.activity_detail.tvTitle
import kotlinx.android.synthetic.main.activity_detail.tvDescription

/**
 * @description     Detail Activity
 *
 * @author          Adrian
 */
class DetailActivity : AppCompatActivity(R.layout.activity_detail) {
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.task_detail_title)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        task = intent.getParcelableExtra(PARAM_TASK)
        tvTitle.text = task!!.title
        tvDescription.text = task!!.description
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "DetailActivity"
        const val PARAM_TASK = "task"

        fun navigateTODetailActivity(activity: Activity, task: Task) {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(PARAM_TASK, task)
            activity.startActivity(intent)
        }
    }
}
