package mk.ukim.finki.mylibrary

import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Display.Mode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mk.ukim.finki.mylibrary.databinding.ActivityListBookBinding

class ListBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBookBinding

    private companion object{
        const val TAG = "BOOK_LIST"
    }
    private var categoryId = ""
    private var category = ""
    private lateinit var bookArrayList:java.util.ArrayList<ModelBook>
    private lateinit var adapterBook: AdapterBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        binding.subtitleTextView.text = category

        loadBookList()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try{
                    adapterBook.filter!!.filter(s)
                }
                catch (e: Exception){
                    Log.d(TAG,"onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun loadBookList(){
        bookArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookArrayList.clear()
                    for(ds in snapshot.children){
                        val model = ds.getValue(ModelBook::class.java)
                        if (model != null) {
                            bookArrayList.add(model)
                            Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
                        }
                    }
                    adapterBook = AdapterBook(this@ListBookActivity, bookArrayList)
                    binding.booksRv.adapter = adapterBook
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}