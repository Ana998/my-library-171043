package mk.ukim.finki.mylibrary

import android.app.AlertDialog
import android.app.ProgressDialog
import android.media.tv.TvContract.Programs
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mk.ukim.finki.mylibrary.databinding.ActivityEditBookBinding

class EditBookActivity : AppCompatActivity() {

    private companion object{
        private const val TAG = "BOOK_EDIT_TAG"
    }

    private lateinit var binding: ActivityEditBookBinding

    private var bookId = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryTitleArrayList:ArrayList<String>
    private lateinit var categoryIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadBook()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.categoryTextView.setOnClickListener {
            categoryDialog()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    private fun loadBook() {
        Log.d(TAG, "loadBook: Loading book")
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    selectedCategoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val numOfPages = snapshot.child("numberOfPages").value.toString()
                    //val isRead = snapshot.child("isRead").value.toString()
                    val isRead = snapshot.child("isRead").value.toString()
                    val rating = snapshot.child("rating").value.toString()

                    binding.titleEditText.setText(title)
                    binding.descriptionEditText.setText(description)
                    binding.numberOfPagesEditText.setText(numOfPages)
                    //binding.isReadCheckbox.setText(isRead.toString())
                    if(isRead == "true")
                    {
                        binding.isReadCheckbox.isChecked = true
                    }
                    else if(isRead == "false")
                    {
                        binding.isReadCheckbox.isChecked = false
                    }

                    binding.ratingEditText.setText(rating)

                    Log.d(TAG, "onDataChanged: Loading book")
                    val refBookCategory = FirebaseDatabase.getInstance().getReference("Categories")
                    refBookCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val category = snapshot.child("category").value
                                binding.categoryTextView.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private var title = ""
    private var description = ""
    private var numOfPages = ""
    private var isRead = false
    private var rating = ""

    private fun validateData(){
        title = binding.titleEditText.text.toString().trim()
        description = binding.descriptionEditText.text.toString().trim()
        numOfPages = binding.numberOfPagesEditText.text.toString().trim()
        isRead = binding.isReadCheckbox.isChecked
        rating = binding.ratingEditText.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Enter title...", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Enter description...", Toast.LENGTH_SHORT).show()
        } else if (selectedCategoryId.isEmpty()) {
            Toast.makeText(this, "Enter category..", Toast.LENGTH_SHORT).show()
        } else if (numOfPages.isEmpty()) {
            Toast.makeText(this, "Enter number of pages..", Toast.LENGTH_SHORT).show()
        }
        else {
            updateBook()
        }
    }

    private fun updateBook(){
        Log.d(TAG, "updateBook: Starting updating book...")

        progressDialog.setMessage("Updating book")
        progressDialog.show()
        val hashMap = HashMap<String, Any>()

        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["numberOfPages"] = "$numOfPages"
        hashMap["isRead"] = "$isRead"
        hashMap["rating"] = "$rating"

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updateBook: Updated succesfully...")
                Toast.makeText(this, "Updated succesfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{ e->
                Log.d(TAG, "updateBook: Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryDialog(){
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoryTitleArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose category")
            .setItems(categoriesArray){dialog, position ->
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                binding.categoryTextView.text = selectedCategoryTitle
            }
            .show()
    }

    private fun loadCategories(){
        Log.d(TAG, "loadCategories: loading categories")

        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()

                for(ds in snapshot.children){
                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG, "onDataChanged:Category ID $id")
                    Log.d(TAG, "onDataChanged:Category  $category")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}