package com.example.odh_project_1

class UserAccount {
    private var idToken: String? = null
    private var emailId: String? = null
    private var password: String? = null
    private var name: String? = null
    private var birth: String? = null

    fun getIdToken(): String? {
        return idToken
    }

    fun setIdToken(idToken: String?) {
        this.idToken = idToken
    }

    fun getEmailId(): String? {
        return emailId
    }

    fun setEmailId(emailId: String?) {
        this.emailId = emailId
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getBirth(): String? {
        return birth
    }

    fun setBirth(birth: String?) {
        this.birth = birth
    }
}