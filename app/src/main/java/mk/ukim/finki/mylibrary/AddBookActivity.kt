package mk.ukim.finki.mylibrary

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import mk.ukim.finki.mylibrary.databinding.ActivityAddBookBinding
import mk.ukim.finki.mylibrary.databinding.ActivityAddCategoryBinding
import mk.ukim.finki.mylibrary.databinding.ActivityListBookBinding
import java.sql.Timestamp

class AddBookActivity : AppCompatActivity() {



        private lateinit var binding: ActivityAddBookBinding
        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var progressDialog: ProgressDialog
        private lateinit var categoryArrayList: ArrayList<ModelCategory>
        private var imgUri: Uri? = null
        private val TAG = "IMG_ADD_TAG"

        companion object {
            val IMAGE_REQUEST_CODE = 100
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAddBookBinding.inflate(layoutInflater)
            setContentView(binding.root)

            firebaseAuth = FirebaseAuth.getInstance()
            loadCategories()

            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please wait")
            progressDialog.setCanceledOnTouchOutside(false)

            binding.backBtn.setOnClickListener {
                onBackPressed()
            }

            binding.categoryTextView.setOnClickListener {
                categoryPickDialog()
            }

//            binding.addImageBtn.setOnClickListener {
//                imgPickIntent()
//            }

//            val numberPicker = binding.numberPicker
//            numberPicker.minValue = 1
//            numberPicker.maxValue =  10
           // var selectedNumber = 0
//            binding.numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
//                selectedNumber = newVal
//            }

            binding.addBookBtn.setOnClickListener {
                validateData()
            }
        }

        private var title = ""
        private var description = ""
        private var category = ""
        private var numOfPages = ""
        private var isRead = false
        private var rating = ""


        private fun validateData() {
            Log.d(TAG, "validateData: validating data")

            title = binding.titleEditText.text.toString().trim()
            description = binding.descriptionEditText.text.toString().trim()
            category = binding.categoryTextView.text.toString().trim()
            numOfPages = binding.numberOfPagesEditText.text.toString().trim()
            isRead = binding.isReadCheckbox.isChecked
//            var selectedNum = 0
//            binding.numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
//                val selectedNum = newVal
//            }
            rating = binding.ratingEditText.text.toString().trim()


            if (title.isEmpty()) {
                Toast.makeText(this, "Enter title...", Toast.LENGTH_SHORT).show()
            } else if (description.isEmpty()) {
                Toast.makeText(this, "Enter description...", Toast.LENGTH_SHORT).show()
            } else if (category.isEmpty()) {
                Toast.makeText(this, "Enter category..", Toast.LENGTH_SHORT).show()
            } else if (numOfPages.isEmpty()) {
                Toast.makeText(this, "Enter number of pages..", Toast.LENGTH_SHORT).show()
            }
//        else if(imgUri == null)
//        {
//            Toast.makeText(this, "Pick an image...", Toast.LENGTH_SHORT).show()
//        }
            else {
                addBookFirebase()
                //uploadToStorage()
            }
        }

        private fun addBookFirebase() {
            progressDialog.show()
            val timestamp = System.currentTimeMillis()
            val uid = firebaseAuth.uid
            val hashMap: HashMap<String, Any> = HashMap()

            hashMap["uid"] = "$uid"
            hashMap["id"] = "$timestamp"
            hashMap["title"] = "$title"
            hashMap["description"] = "$description"
            hashMap["categoryId"] = "$selectedCategoryId"
            hashMap["numberOfPages"] = "$numOfPages"
            hashMap["isRead"] = "$isRead"
            hashMap["rating"] = "$rating"
            //hashMap["imgUrl"] = "$imgUri"
            //hashMap["url"] = "$uploadedUrl"
            hashMap["timestamp"] = timestamp

            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child("$timestamp")
                .setValue(hashMap)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Added successfully...", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to add due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        private fun uploadToStorage() {
            Log.d(TAG, "uploadToSTorage: uploading to storage...")

            progressDialog.setMessage("Uploading ...")
            progressDialog.show()

            val timestamp = System.currentTimeMillis()

            val pathAndName = "bOOKS/${timestamp}"
            val storageReference = FirebaseStorage.getInstance().getReference(pathAndName)
            storageReference.putFile(imgUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.d(TAG, "uploadToStorage: Uploaded now getting url...")
                    val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val uploadedUrl = "${uriTask.result}"
                    uploadedInfoToDB(uploadedUrl, timestamp)
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Failed to upload due to ${e.message}")
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT)
                        .show()

                }
        }

        private fun uploadedInfoToDB(uploadedUrl: String, timestamp: Long) {
            Log.d(TAG, "uploadInfoToDb: uploading to db...")
            progressDialog.setMessage("Uploading info...")

            val uid = firebaseAuth.uid

            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["uid"] = "$uid"
            hashMap["id"] = "$timestamp"
            hashMap["title"] = "$title"
            hashMap["description"] = "$description"
            hashMap["category"] = "$category"
            hashMap["numberOfPages"] = "$numOfPages"
            hashMap["isRead"] = "$isRead"
            hashMap["rating"] = "$rating"
            //hashMap["imgUrl"] = "$imgUri"
              hashMap["url"] = "$uploadedUrl"
            hashMap["timestamp"] = timestamp

            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child("$timestamp")
                .setValue(hashMap)
                .addOnSuccessListener {
                    Log.d(TAG, "uploadInfoToDb: uploaded to db")
                    progressDialog.dismiss()
                    Toast.makeText(this, "Uploaded...", Toast.LENGTH_SHORT).show()
                    imgUri = null
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Failed to upload due to ${e.message}")
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT)
                        .show()

                }

        }

        private fun loadCategories() {
            Log.d(TAG, "loadCategories: Loading categories")
            categoryArrayList = ArrayList()

            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(shapshot: DataSnapshot) {
                    categoryArrayList.clear()
                    for (ds in shapshot.children) {
                        val model = ds.getValue(ModelCategory::class.java)
                        categoryArrayList.add(model!!)
                        Log.d(TAG, "onDataChange: ${model.category}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        private var selectedCategoryId = ""
        private var selectedCategoryTitle = ""

        private fun categoryPickDialog() {
            Log.d(TAG, "categoryPickDialog: Showing category pick dialog")

            val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
            for (i in categoryArrayList.indices) {
                categoriesArray[i] = categoryArrayList[i].category
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pick category")
                .setItems(categoriesArray) { dialog, which ->
                    selectedCategoryTitle = categoryArrayList[which].category
                    selectedCategoryId = categoryArrayList[which].id

                    binding.categoryTextView.text = selectedCategoryTitle
                    Log.d(TAG, "categoryPickDialog: Selected category ID: $selectedCategoryId")
                    Log.d(
                        TAG,
                        "categoryPickDialog: Selected category Title: $selectedCategoryTitle"
                    )
                }
                .show()
        }
    private fun imgPickIntent(){
        Log.d(TAG, "imgPickIntent: Starting img pick intent")

        val intent = Intent()
        //val intent = Intent(Intent.ACTION_PICK)
        intent.type = "application/image"
        intent.action = Intent.ACTION_PICK
        imgActivityResultLauncher.launch(intent)
        //startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    val imgActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if(result.resultCode == RESULT_OK){
                Log.d(TAG, "IMG Picked")
                imgUri = result.data!!.data
            }
            else {
                Log.d(TAG, "IMG Pick cancelled")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT)
            }
        }
    )

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
//            binding.image.setImageURI(data?.data)
//        }
//    }
    }
