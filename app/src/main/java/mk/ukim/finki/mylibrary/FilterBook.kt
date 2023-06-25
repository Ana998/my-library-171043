package mk.ukim.finki.mylibrary

import android.widget.Filter

class FilterBook: Filter {

    var filterList: ArrayList<ModelBook>
    var adapterBook: AdapterBook

    constructor(filterList: ArrayList<ModelBook>, adapterBook: AdapterBook) {
        this.filterList = filterList
        this.adapterBook = adapterBook
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint
        var results = FilterResults()
        if(constraint != null && constraint.isNotEmpty()){
            constraint=constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelBook>()
            for(i in filterList.indices){
                if(filterList[i].title.lowercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterBook.bookArrayList = results.values as ArrayList<ModelBook>

        adapterBook.notifyDataSetChanged()
    }
}