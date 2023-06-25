package mk.ukim.finki.mylibrary

class ModelBook {
    var uid: String = ""
    var id: String = ""
    var title: String = ""
    var description: String = ""
    var categoryId: String = ""
    var numberOfPages: String = ""
    var isRead: Boolean = false
    var rating: String = ""
    var timestamp: Long = 0

    constructor()
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        numberOfPages: String,
        isRead: Boolean,
        rating: String,
        timestamp: Long
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.numberOfPages = numberOfPages
        this.isRead = isRead
        this.rating = rating
        this.timestamp = timestamp
    }

}