package com.majestykapps.arch.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.majestykapps.arch.R
import com.majestykapps.arch.domain.entity.Task

/**
 * @description     Task List Adapter
 *
 * @author          Adrian
 */
class TaskListAdapter(private val list: List<Task>)
    : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task: Task = list[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = list.size

}

class TaskViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_task, parent, false)) {
    private var tvTitle: TextView? = null
    private var tvDescription: TextView? = null


    init {
        tvTitle = itemView.findViewById(R.id.tvTitle)
        tvDescription = itemView.findViewById(R.id.tvDescription)
    }

    fun bind(task: Task) {
        tvTitle?.text = task.title
        tvDescription?.text = task.description
    }
}