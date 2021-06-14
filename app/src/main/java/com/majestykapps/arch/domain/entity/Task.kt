package com.majestykapps.arch.domain.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Immutable model class for a Task. In order to compile with Room, we can use @JvmOverloads to
 * generate multiple constructors.
 *
 * For the purposes of simplicity, one entity is used for network and database models.
 *
 * @param id          id of the task
 * @param title       title of the task
 * @param description description of the task
 */
@Entity(tableName = "tasks", indices = [Index(value = ["id"], unique = true)])
data class Task @JvmOverloads constructor (
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: String? = null,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String = "",

    @ColumnInfo(name = "description")
    @SerializedName("description")
    val description: String = ""
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var rowId: Long? = null

    /**
     * True if the task is completed, false if it's active.
     */
    @ColumnInfo(name = "completed")
    var isCompleted = false

    val titleForList: String?
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() && description.isEmpty()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        rowId = parcel.readValue(Long::class.java.classLoader) as? Long
        isCompleted = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeValue(rowId)
        parcel.writeByte(if (isCompleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}