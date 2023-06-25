package mk.ukim.finki.mylibrary

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import mk.ukim.finki.mylibrary.databinding.RowBookBinding

class AdapterBook :RecyclerView.Adapter<AdapterBook.HolderBook>, Filterable{

    private var context: Context
    public var bookArrayList: ArrayList<ModelBook>
    private val filterList:ArrayList<ModelBook>

    private lateinit var binding: RowBookBinding

    private var filter: FilterBook? = null

    constructor(context: Context, bookArrayList: ArrayList<ModelBook>) : super(){
        this.context = context
        this.bookArrayList = bookArrayList
        this.filterList = bookArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBook {
        binding = RowBookBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderBook(binding.root)
    }

    override fun onBindViewHolder(holder: HolderBook, position: Int) {
        val model = bookArrayList[position]
        val id = model.id
        val title = model.title
        val description = model.description
        val numberOfPages = model.numberOfPages
        val isRead = model.isRead
        val rating = model.rating
        val category = model.categoryId
        val uid = model.uid
        val timestamp = model.timestamp
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        holder.titleTv.text = title
        holder.descriptionTv.text = description
        if(rating.isEmpty())
        {
            holder.ratingTv.text = "N/A"
        }
        else
        {
            holder.ratingTv.text = rating
        }
        holder.categoryTv
        MyApplication.loadCategory(category, holder.categoryTv)

        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra("bookId", id)
            context.startActivity(intent)
        }

    }

    private fun moreOptionsDialog(model: ModelBook, holder: AdapterBook.HolderBook) {
        val bookId = model.id
        val bookTitle = model.title

        val options = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose option")
            .setItems(options){dialog, position ->
                if(position==0){
                    val intent = Intent(context, EditBookActivity::class.java)
                    intent.putExtra("bookId", bookId)
                    context.startActivity(intent)
                }
                else if(position==1)
                {
                    MyApplication.deleteBook(context, bookId, bookTitle)
                }
            }
            .show()
    }

    override fun getItemCount(): Int {
        return bookArrayList.size
    }


    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterBook(filterList, this)

        }
        return filter as FilterBook
    }
    inner class HolderBook(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTv = binding.bookTextView
        val categoryTv = binding.categoryTextView
        val descriptionTv = binding.descriptionTextView
        val ratingTv = binding.ratingTextView
        val moreBtn = binding.moreBtn
    }

}
    /*private val context: Context
    public var bookArrayList: ArrayList<ModelBook>
    private lateinit var filterList: ArrayList<ModelBook>

    //private var filter: FilterBook? = null

    private lateinit var binding: RowBookBinding

    constructor(context: Context, bookArrayList: ArrayList<ModelBook>) : super() {
        this.context = context
        this.bookArrayList = bookArrayList
    }

    inner class HolderBook(itemView: CardView): RecyclerView.ViewHolder(itemView){
        var bookTextView: TextView = binding.bookTextView
        var deleteBtn: ImageButton = binding.deleteBtn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBook {
        binding = RowBookBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderBook(binding.root)
    }

    override fun onBindViewHolder(holder: HolderBook, position: Int) {
        val model = bookArrayList[position]
        val id = model.id
        val title = model.title
        val description = model.description
        val numberOfPages = model.numberOfPages
        val isRead = model.isRead
        val rating = model.rating
        val category = model.categoryId
        val uid = model.uid
        val timestamp = model.timestamp

        holder.bookTextView.text = title
        holder.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteBook(model, holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ListBookActivity::class.java)
            intent.putExtra("bookId", id)
            intent.putExtra("title", title)
            context.startActivity(intent)
        }
    }

    private fun deleteBook(model:ModelBook, holder: HolderBook){
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
    override fun getItemCount(): Int {
        return bookArrayList.size
    }

//    override fun getFilter(): Filter {
//        if(filter == null){
//            filter = FilterBook(filterList, this)
//        }
//        return filter as FilterBook
//    }
}*/