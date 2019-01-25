package mongomart.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.Util;

import mongomart.config.Utils;
import mongomart.model.Category;
import mongomart.model.Item;
import org.bson.Document;
import mongomart.model.Item;
import mongomart.model.Review;
import org.bson.types.Decimal128;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * All database access to the "item" collection
 *
 * ALL T O D O BLOCKS MUST BE COMPLETED
 */
public class ItemDao {
    // TODO-lab1 this variable must be assigned in constructor
    private final MongoCollection<Document> itemCollection;

    private static final int ITEMS_PER_PAGE = 5;

    /**
     *
     * @param mongoMartDatabase
     */
    public ItemDao(final MongoDatabase mongoMartDatabase) {
        /**
         * TODO-lab1
         *
         * LAB #1: Get the "item" collection and assign it to itemCollection variable here
         *
         * DONE
         */
        itemCollection = mongoMartDatabase.getCollection("item"); // TODO-lab1 replace this line

    }

    /**
     * Get an Item by id
     *
     * @param id
     * @return
     */
    public Item getItem(int id) {
        /**
         * TODO-lab2
         *
         * LAB #2: Query the "item" collection by _id and return a Cart object
         * 
         * DONE
         */

        Document itemDoc = itemCollection.find(eq("_id", id)).first();
        return docToItem(itemDoc);

    }

    private List<Item> docToItem(List<Document> documents) {
        List<Item> returnValue = new ArrayList<Item>();

        for (Document document : documents) {
            returnValue.add(docToItem(document));
        }

        return returnValue;
    }

    /**
     * Map a document to an Item
     *
     * @param document
     * @return
     */
    private Item docToItem(Document document) {
        Item item = new Item();
        item.setId(document.getInteger("_id"));
        item.setTitle(document.getString("title"));
        item.setDescription(document.getString("description"));
        item.setCategory(document.getString("category"));
        item.setPrice(document.get("price", Decimal128.class).bigDecimalValue());
        item.setImg_url(document.getString("img_url"));
        item.setSlogan(document.getString("slogan"));
        if (document.containsKey("quantity")) {
            item.setQuantity(document.getInteger("quantity"));
        }
        if (document.containsKey("reviews") && document.get("reviews") instanceof List) {
            List<Review> reviews = new ArrayList<>();
            List<Document> reviewsList = (List<Document>)document.get("reviews");
            int totalStars = 0;

            for (Document reviewDoc : reviewsList) {
                Review review = new Review();
                review.setComment(reviewDoc.getString("comment"));
                review.setName(reviewDoc.getString("name"));
                review.setStars(reviewDoc.getInteger("stars"));
                totalStars += reviewDoc.getInteger("stars");
                review.setDate(reviewDoc.getDate("date"));
                reviews.add(review);
            }

            item.setStars(totalStars / reviewsList.size());
            item.setNum_reviews(reviewsList.size());
            item.setReviews(reviews);
        }
        else {
            item.setReviews(new ArrayList<>());
        }

        return item;
    }

    /**
     * Convert a Review object to a document
     *
     * @param review
     * @return
     */
    private Document reviewToDoc(Review review) {
        Document document = new Document();
        document.append("_id", review.getId());
        document.append("name", review.getName());
        document.append("date", review.getDate());
        document.append("comment", review.getComment());
        document.append("stars", review.getStars());
        return document;
    }

    /**
     * Get items by page number
     *
     * @param page_str the page number the user is currently on, starting at 0
     * @return
     */
    public List<Item> getItems(String page_str) {

        /**
         * TODO-lab2
         *
         * LAB #2: Return a list of items from the "item" collection, limit by ITEMS_PER_PAGE and
         *         skip by (page_str * ITEMS_PER_PAGE)
         *
         * HINT: page_str is a string,
         * 
         * DONE
         */
        int pageNum = Utils.getIntFromString(page_str);

        ArrayList<Document> itemDocs = itemCollection.find()
            .skip(ITEMS_PER_PAGE * pageNum)
            .limit(ITEMS_PER_PAGE)
            .into(new ArrayList<>());

        return docToItem(itemDocs);
    }

    /**
     * Get number of items, useful for pagination
     *
     * @return
     */
    public long getItemsCount() {

        /**
         * TODO-lab2
         *
         * LAB #2: Count the items in the "item" collection, used for pagination
         * 
         * DONE
         *
         */

        long count = itemCollection.count();

        /**
         * TODO-lab2 Replace all code above
         */

        return count;
    }

    /**
     * Get items by category, and page (starting at 0)
     *
     * @param category
     * @param page_str
     * @return
     */
    public List<Item> getItemsByCategory(String category, String page_str) {

        /**
         * TODO-lab2
         *
         * LAB #2: Return a list of items from the "item" collection by category, limit by ITEMS_PER_PAGE and
         *         skip by (page_str * ITEMS_PER_PAGE)
         *
         * HINT: page_str is a string,
         * 
         * DONE
         */

        int pageNum = Utils.getIntFromString(page_str);

        ArrayList<Document> itemDocs = itemCollection.find(eq("category", category))
            .skip(ITEMS_PER_PAGE * pageNum)
            .limit(ITEMS_PER_PAGE)
            .into(new ArrayList<>());

        /**
         * TODO-lab2 Replace all code above
         */

        return docToItem(itemDocs);
    }

    /**
     * Get number of items in a category, useful for pagination
     *
     * @param category
     * @return
     */
    public long getItemsByCategoryCount(String category) {

        /**
         * TODO-lab2
         *
         * LAB #2: Count the items in the "item" collection by category, used for pagination
         *
         * DONE
         */

        long count = itemCollection.count(eq("category", category));

        /**
         * TODO-lab2 Replace all code above
         */

        return count;
    }

    /**
     * Text search, requires the index:
     *      db.item.createIndex( { "title" : "text", "slogan" : "text", "description" : "text" } )
     *
     * @param query_str
     * @param page_str
     * @return
     */
    public List<Item> textSearch(String query_str, String page_str) {

        /**
         * TODO-lab2
         *
         * LAB #2: Perform a text search against the item collection, , limit by ITEMS_PER_PAGE and
         *         skip by (page_str * ITEMS_PER_PAGE)
         *
         * HINT: page_str is a string,
         * 
         * DONE
         */

        int pageNum = Utils.getIntFromString(page_str);

        List<Document> docs = itemCollection.find(text(query_str))
            .skip(ITEMS_PER_PAGE * pageNum)
            .limit(ITEMS_PER_PAGE)
            .into(new ArrayList<>());

        /**
         * TODO-lab2 Replace all code above
         */

        return docToItem(docs);
    }

    /**
     * Get count for text search results, useful for pagination
     *
     * @param query_str
     * @return
     */
    public long textSearchCount(String query_str) {

        /**
         * TODO-lab2
         *
         * LAB #2: Count the items in the "item" collection based on a text search, used for pagination
         *
         */

        int count = (int) itemCollection.count(text(query_str));

        /**
         * TODO-lab2 Replace all code above
         */

        return count;
    }

    /**
     * Use aggregation to get a count of the number of products in each category
     *
     * @return
     */
    public List<Category> getCategoriesAndNumProducts() {
        /**
         * TODO-lab2
         *
         * LAB #2: Create an aggregation query to return the total number of items in each category.  The
         *         Category object contains "name" and "num_items".  Remember to include an "All" category
         *         for counting all items in the database.
         *
         * HINT: Test your mongodb query in the shell first before implementing it in Java
         */

        List<Category> categories = new ArrayList<>();
        List aggrStages = Arrays.asList(
            Aggregates.group("$category", Accumulators.sum("count", 1)), 
            Aggregates.sort(Sorts.descending("count"))
        );

        MongoCursor<Document> cursor = itemCollection.aggregate(aggrStages, Document.class)
            .useCursor(true).iterator();

        int total = 0;

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            int count = doc.getInteger("count");
            Category cat = new Category(doc.getString("_id"), count);
            categories.add(cat);
            total += count;
        }

        Category all = new Category("All", total);
        categories.add(all);


        /**
         * TODO-lab2 Replace all code above
         */

        return categories;

    }

    /**
     * Add a review to an item
     *
     * @param itemid
     * @param review_text
     * @param name
     * @param stars
     */
    public void addReview(String itemid, String review_text, String name, String stars) {
        /**
         * TODO-lab2
         *
         * LAB #2: Add a review to an item document
         *
         * HINT: Remember that reviews are a list within the Item object
         *
         */

        int idNum = Utils.getIntFromString(itemid);
        Review review = new Review();
        review.setComment(review_text);
        review.setDate(new Date());
        review.setName(name);
        review.setStars(Utils.getIntFromString(stars));

        Document updateDoc = new Document("$push", new Document("reviews", reviewToDoc(review)));
        itemCollection.updateOne(eq("_id", idNum), updateDoc);
    }

    /**
     * Return the constant ITEMS_PER_PAGE
     *
     * @return
     */
    public int getItemsPerPage() {
        return ITEMS_PER_PAGE;
    }
}
