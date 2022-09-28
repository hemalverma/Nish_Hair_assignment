package com.hvx.nishhair

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hvx.nishhair.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    private var auth: FirebaseAuth = Firebase.auth
    private val currentUser = auth.currentUser

    private lateinit var dialog : Dialog
    private lateinit var emailDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)


        emailDialog = Dialog(this)
        emailDialog.setContentView(R.layout.email_dialog)
        val btnGetOtp = emailDialog.findViewById<Button>(R.id.btn_get_otp)
        val etEmail =emailDialog.findViewById<EditText>(R.id.et_email_d)

        btnGetOtp.setOnClickListener{
            if (etEmail.text.isBlank()){
                Toast.makeText(applicationContext, "Email cannot be Empty", Toast.LENGTH_SHORT)
                    .show()
            }else{
                if (etEmail.text.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
                    sendOtp(etEmail.text.toString())
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Email is badly formatted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        binding.llSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text
            val password = binding.etPassword.text
            if (email.isNullOrBlank()) {
                Toast.makeText(applicationContext, "Email cannot be Empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
                    if (password.isNullOrBlank() || password.trim().length < 6) {
                        Toast.makeText(
                            applicationContext, "Password length should be more than 6",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        dialog.show()
                        signIn(email.toString(), password.toString())

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

        binding.tvForgotPassword.setOnClickListener{
            emailDialog.show()
        }

    }

    private fun sendOtp(email: String) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext,"Reset email sent",Toast.LENGTH_SHORT).show()
                    emailDialog.dismiss()
                }
                else{
                    Toast.makeText(applicationContext,"Failed to sent reset link",Toast.LENGTH_SHORT).show()
                    emailDialog.dismiss()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    if(dialog.isShowing){
                        dialog.dismiss()
                    }
                    Log.d(TAG, "signInWithEmail:success")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if(dialog.isShowing){
                        dialog.dismiss()
                    }
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, task.exception!!.localizedMessage!!.toString(),
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