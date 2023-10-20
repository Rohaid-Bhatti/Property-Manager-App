package com.hzm.sharedpreference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hzm.sharedpreference.roomDb.Contact

class UserListAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<UserListAdapter.ContactViewHolder>() {

    private var contacts = emptyList<Contact>()

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvNameRow)
        val emailTextView: TextView = itemView.findViewById(R.id.tvEmailRow)
        // Add more TextViews for other contact properties here

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val contact = contacts[position]
                    clickListener.onItemClick(contact)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_row, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = contacts[position]
        holder.nameTextView.text = currentContact.name
        holder.emailTextView.text = currentContact.email
        // Bind other contact properties here
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun setContacts(contacts: List<Contact>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(contact: Contact)
    }
}