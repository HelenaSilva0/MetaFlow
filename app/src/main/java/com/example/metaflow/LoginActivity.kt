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

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                LoginPage(
                    onLogin = {
                        Toast.makeText(
                            this,
                            "Login realizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(this, MainActivity::class.java).setFlags(
                                FLAG_ACTIVITY_SINGLE_TOP
                            )
                        )
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