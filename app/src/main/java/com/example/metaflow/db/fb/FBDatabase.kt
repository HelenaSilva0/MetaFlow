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
    private var userReg: ListenerRegistration? = null
    private var rankingReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                goalsListReg?.remove()
                userReg?.remove()
                rankingReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }
            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            
            userReg?.remove()
            userReg = refCurrUser.addSnapshotListener { snapshot, _ ->
                snapshot?.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }

            goalsListReg?.remove()
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

    fun register(user: FBUser, onResult: (Boolean) -> Unit = {}) {
        if (auth.currentUser == null) {
            onResult(false)
            return
        }
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).set(user)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    fun updateUser(user: FBUser) {
        if (auth.currentUser == null) return
        val uid = auth.currentUser!!.uid
        val changes = mapOf(
            "name" to user.name,
            "email" to user.email,
            "profilePic" to user.profilePic,
            "theme" to user.theme
        )
        db.collection("users").document(uid).update(changes)
    }

    fun updateUserStats(user: FBUser) {
        if (auth.currentUser == null) return
        val uid = auth.currentUser!!.uid
        val changes = mapOf(
            "xp" to user.xp,
            "streak" to user.streak,
            "lastActivityDate" to user.lastActivityDate,
            "badges" to user.badges,
            "totalCompleted" to user.totalCompleted
        )
        db.collection("users").document(uid).update(changes)
    }

    fun login(email: String, password: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(null)
                } else {
                    onResult(task.exception?.localizedMessage ?: "Erro ao entrar")
                }
            }
    }

    fun registerAuth(email: String, password: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(null)
                } else {
                    onResult(task.exception?.localizedMessage ?: "Erro ao criar conta")
                }
            }
    }

    fun add(goal: FBGoal) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (goal.name == null || goal.name!!.isEmpty())
            throw RuntimeException("Goal with null or empty name!")
        val uid = auth.currentUser!!.uid
        val documentId = goal.id?.toString() ?: db.collection("users").document(uid).collection("goals").document().id
        goal.id = documentId
        db.collection("users").document(uid).collection("goals")
            .document(documentId).set(goal)
    }

    fun remove(goal: FBGoal) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val goalIdStr = goal.id?.toString()
        if (goalIdStr == null || goalIdStr.isEmpty())
            throw RuntimeException("Goal with null or empty ID!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("goals")
            .document(goalIdStr).delete()
    }

    fun update(goal: FBGoal) {
        if (auth.currentUser == null) throw RuntimeException("Not logged in!")
        val uid = auth.currentUser!!.uid
        val goalIdStr = goal.id?.toString() ?: return
        val changes = mapOf(
            "name" to goal.name,
            "category" to goal.category,
            "reminderTime" to goal.reminderTime,
            "priority" to goal.priority,
            "recurrence" to goal.recurrence,
            "deadline" to goal.deadline,
            "location" to goal.location,
            "latitude" to goal.latitude,
            "longitude" to goal.longitude,
            "completed" to goal.completed,
            "completedAt" to goal.completedAt,
            "monitored" to goal.monitored
        )
        db.collection("users").document(uid)
            .collection("goals").document(goalIdStr).update(changes)
    }

    fun updateUserXP(xp: Int) {
        if (auth.currentUser == null) return
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).update("xp", xp)
    }

    fun getAllUsers(onResult: (List<FBUser>) -> Unit) {
        rankingReg?.remove()
        rankingReg = db.collection("users")
            .orderBy("xp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                val users = snapshots?.toObjects(FBUser::class.java) ?: emptyList()
                onResult(users)
            }
    }
}
