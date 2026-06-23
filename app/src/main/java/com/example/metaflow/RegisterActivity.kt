package com.example.metaflow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.metaflow.ui.RegisterPage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                RegisterPage(
                    onRegister = { email, password ->
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this@RegisterActivity) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Registro OK!", Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Registro FALHOU!", Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                )
            }
        }
    }
}