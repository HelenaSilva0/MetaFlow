package com.example.metaflow.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onGoalAdded(goal: FBGoal)
        fun onGoalUpdated(goal: FBGoal)
        fun onGoalRemoved(goal: FBGoal)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var goalsListReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                goalsListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }
            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }
            goalsListReg = refCurrUser.collection("goals")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener
                    snapshots?.documentChanges?.forEach { change ->
                        val fbGoal = change.document.toObject(FBGoal::class.java)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> listener?.onGoalAdded(fbGoal)
                            DocumentChange.Type.MODIFIED -> listener?.onGoalUpdated(fbGoal)
                            DocumentChange.Type.REMOVED -> listener?.onGoalRemoved(fbGoal)
                        }
                    }
                }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    fun register(user: FBUser) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).set(user)
    }

    fun add(goal: FBGoal) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (goal.name == null || goal.name!!.isEmpty())
            throw RuntimeException("Goal with null or empty name!")
        val uid = auth.currentUser!!.uid
        // Usamos o nome como ID para simplificar conforme a prática, 
        // mas idealmente usaríamos um ID único ou goal.id.toString()
        val documentId = goal.name!!
        db.collection("users").document(uid).collection("goals")
            .document(documentId).set(goal)
    }

    fun remove(goal: FBGoal) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (goal.name == null || goal.name!!.isEmpty())
            throw RuntimeException("Goal with null or empty name!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("goals")
            .document(goal.name!!).delete()
    }
}
