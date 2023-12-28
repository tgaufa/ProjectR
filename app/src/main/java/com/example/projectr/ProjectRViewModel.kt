package com.example.projectr

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.projectr.data.COLLECTION_USER
import com.example.projectr.data.Event
import com.example.projectr.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProjectRViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel(){


    val inProgress = mutableStateOf(false)
    val popUpNotification = mutableStateOf<Event<String>?>(null)
    val signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)


    init {
        //onLogout()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let {uid ->
            getUserData(uid)
        }


    }

    fun onSigneUp(name: String, number: String, email: String, password: String){
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "please fill in all fields")
            return
        }
        inProgress.value = true
        db.collection(COLLECTION_USER).whereEqualTo("number", number)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty)
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                signedIn.value = true
                                //create user profile
                                createOrUpdateProfile(name = name, number = number)
                            } else
                                handleException(task.exception, customMessage = "SignUp Failed")
                        }
                else
                    handleException(customMessage = "number already exists")

                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
            }


    }

    fun onLogin(email: String, pass: String){
        if (email.isEmpty() or pass.isEmpty()){
            handleException(customMessage = "please fill all fields")
            return
        }
        inProgress.value = false
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    signedIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                } else
                    handleException(task.exception, customMessage = "Login Failed")
            }
            .addOnFailureListener {
                handleException(it, customMessage = "Login Failed ")
            }

    }

    private fun createOrUpdateProfile(
        name: String? = "",
        number: String? = "",
        imageURL: String? = ""
    ){
        val uid  = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageURL = imageURL ?: userData.value?.imageURL
        )

        uid?.let {uid ->
            inProgress.value = true
            db.collection(COLLECTION_USER).document(uid)
                .get()
                .addOnSuccessListener {
                    if(it.exists()) {
                        //update User
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                inProgress.value = false
                            }
                            .addOnFailureListener { handleException(it, customMessage = "Cannot Update User") }
                    } else {
                        // create user profile
                        db.collection(COLLECTION_USER).document(uid)
                            .set(userData)
                        inProgress.value = false
                        getUserData(uid)
                    }
                }
                .addOnFailureListener {
                    handleException(it, customMessage = "Cannot retrieve user")
                }
        }
    }

    private fun getUserData(uid: String){
        inProgress.value = true
        db.collection(COLLECTION_USER).document(uid)
            .addSnapshotListener { value, error ->
                if(error !=null)
                    handleException(error, customMessage = "Cannot retrieve User Data")

                if(value != null){
                    val user = value.toObject<UserData>()
                    userData.value = user
                    inProgress.value = false

                }
            }
    }

    fun onLogout(){
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popUpNotification.value = Event("Logged Out")
    }
    private fun handleException(exception: Exception? = null, customMessage: String = ""){
        Log.e("Project R", "Chat App Exception", exception )
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popUpNotification.value = Event(message)
        inProgress.value = false
    }

}