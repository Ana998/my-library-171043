package mk.ukim.finki.mylibrary

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateFormat.format
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.*

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
    companion object{
        fun formatTimeStamp(timestamp: Long):String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        fun loadCategory(categoryId: String, categoryTv: TextView){
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category = "${snapshot.child("category").value}"
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

        fun deleteBook(context: Context, bookId: String, bookTitle: String){
            val TAG = "DELETE_BOOK_TAG"

            Log.d(TAG, "deleteBook: deleting...")

            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Deleting $bookTitle")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .removeValue()
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Log.d(TAG, "deleteBook: Successfully deleted...")
                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener{ e ->
                    progressDialog.dismiss()
                    Log.d(TAG, "deleteBook: Failed due to ${e.message}")
                    Toast.makeText(context, "Failed to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }


}