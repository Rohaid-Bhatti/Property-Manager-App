package com.hzm.sharedpreference

import androidx.lifecycle.LiveData
import com.hzm.sharedpreference.roomDb.Contact
import com.hzm.sharedpreference.roomDb.ContactDAO

class ContactRepository(private val contactDao: ContactDAO) {

    val allContacts: LiveData<List<Contact>> = contactDao.getUser()

    suspend fun insert(contact: Contact) {
        contactDao.insertUser(contact)
    }

    suspend fun update(contact: Contact) {
        contactDao.updateUser(contact)
    }

    suspend fun delete(contact: Contact) {
        contactDao.deleteUser(contact)
    }
}