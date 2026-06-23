package com.example.metaflow

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.metaflow.ui.LoginPage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                LoginPage(
                    onLogin = { email, password ->
                        Firebase.auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    startActivity(
                                        Intent(this, MainActivity::class.java).setFlags(
                                            FLAG_ACTIVITY_SINGLE_TOP
                                        )
                                    )
                                    Toast.makeText(this, "Login OK!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this, "Login FALHOU!", Toast.LENGTH_LONG).show()
                                }
                            }
                    },
                    onRegister = {
                        startActivity(
                            Intent(this, RegisterActivity::class.java).setFlags(
                                FLAG_ACTIVITY_SINGLE_TOP
                            )
                        )
                    }
                )
            }
        }
    }
}