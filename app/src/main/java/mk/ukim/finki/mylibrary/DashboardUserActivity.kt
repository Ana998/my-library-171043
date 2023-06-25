package mk.ukim.finki.mylibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import mk.ukim.finki.mylibrary.databinding.ActivityDashboardAdminBinding
import mk.ukim.finki.mylibrary.databinding.ActivityDashboardUserBinding

class DashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun checkUser() {
        val firebaseUer = firebaseAuth.currentUser
        if (firebaseUer == null)
        {
            binding.subTitleTextView.text = "Not logged in"
        } else {
            val email = firebaseUer.email
            binding.subTitleTextView.text = email
        }
    }
}