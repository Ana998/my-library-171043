package mk.ukim.finki.mylibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mk.ukim.finki.mylibrary.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding

    private var bookId = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!

        loadBookDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadBookDetails(){
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val title = "${snapshot.child("title").value}"
                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val numberOfPages = "${snapshot.child("numberOfPages").value}"
                    val isRead = "${snapshot.child("isRead").value}"
                    val rating = "${snapshot.child("rating").value}"

                    MyApplication.loadCategory(categoryId, binding.categoryTextView)

                    binding.titleTextView.text = title
                    binding.descriptionTextView.text = description
                    binding.numberOfPagesTextView.text = numberOfPages
                    if(isRead.equals("true")) {
                        binding.isReadTextView.text = "Yes"
                    }
                    else
                    {
                        binding.isReadTextView.text = "No"
                    }
                    if(rating.isEmpty()){
                        binding.ratingextView.text = "N/A"
                    }
                    else{
                        binding.ratingextView.text = rating
                    }

                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}