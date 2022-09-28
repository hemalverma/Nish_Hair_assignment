package com.hvx.nishhair

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hvx.nishhair.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var auth: FirebaseAuth = Firebase.auth
    private val currentUser = auth.currentUser

    private lateinit var dialog : Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)

        binding.llSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text
            val email = binding.etEmail.text
            val password = binding.etPassword.text
            val confirmPassword = binding.etConfirmPassword.text


            if (name.isNullOrBlank()) {
                Toast.makeText(applicationContext, "Name cannot be Empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (email!!.isBlank()) {
                    Toast.makeText(applicationContext, "Email cannot be Empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
                        if (password.isNullOrBlank() || password.trim().length < 6) {
                            Toast.makeText(
                                applicationContext,
                                "Password length should be more than 6",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (password.trim() == confirmPassword!!.trim()) {
                                dialog.show()
                                signUp(email.toString(), password.toString())
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Password and confirm password does not matches",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Email is badly formatted.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    if(dialog.isShowing){
                        dialog.dismiss()
                    }
                    Log.d(TAG, "createUserWithEmail:success")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    if(dialog.isShowing){
                        dialog.dismiss()
                    }
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onStart() {
        super.onStart()
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}