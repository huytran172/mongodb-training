package mongomart.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import mongomart.model.Cart;
import mongomart.model.Item;
import org.bson.Document;
import org.bson.types.Decimal128;

import static com.mongodb.client.model.Filters.eq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * All database access to the "cart" collection
 *
 * ALL T O D O BLOCKS MUST BE COMPLETED
 */
public class CartDao {
    // TODO-lab1 this variable must be assigned in constructor
    private final MongoCollection<Document> cartCollection;

    /**
     *
     * @param mongoMartDatabase
     */
    public CartDao(final MongoDatabase mongoMartDatabase) {

        /**
         * TODO-lab1
         *
         * LAB #1: Get the "cart" collection and assign it to cartCollection variable here
         * 
         * DONE
         *
         */
        cartCollection = mongoMartDatabase.getCollection("cart"); // TODO-lab1 replace this line
    }

    /**
     * Get a cart by userid
     *
     * @param userid
     * @return
     */
    public Cart getCart(String userid) {

        /**
         * TODO-lab2
         *
         * LAB #2: Query the "cart" collection by userid and return a Cart object
         * 
         * DONE
         */
        Document cartDoc = cartCollection.find(eq("userid", userid)).first();
        Cart cart = docToCart(cartDoc); // TODO-lab2 replace this line

        if (cart == null) {
            cart = new Cart();
            cart.setUserid(userid);
            cart.setLast_modified(new Date());
            cart.setStatus("active");
            cart.setItems(new ArrayList<Item>());
            return cart;
        }

        return cart;
    }

    private Cart docToCart(Document document) {
        if (document == null) return null;

        Cart cart = new Cart();

        cart.setId(document.getObjectId("_id"));
        cart.setLast_modified(document.getDate("last_modified"));
        cart.setStatus(document.getString("status"));
        cart.setUserid(document.getString("userid"));

        if (document.containsKey("items") && document.get("items") instanceof List) {
            List<Item> items = new ArrayList<>();
            List<Document> itemsList = (List<Document>) document.get("items");

            for (Document itemDoc : itemsList) {
                Item item = new Item();

                if (itemDoc.containsKey("_id")) {
                    item.setId(itemDoc.getInteger("_id"));
                }

                if (itemDoc.containsKey("title")) {
                    item.setTitle(itemDoc.getString("title"));
                }

                if (itemDoc.containsKey("category")) {
                    item.setCategory(itemDoc.getString("category"));
                }

                if (itemDoc.containsKey("quantity")) {
                    item.setQuantity(itemDoc.getInteger("quantity"));
                }
                if (itemDoc.containsKey("price")) {
                    item.setPrice(itemDoc.get("price", Decimal128.class).bigDecimalValue());
                }
                if (itemDoc.containsKey("img_url")) {
                    item.setImg_url(itemDoc.getString("img_url"));

                }

                items.add(item);
            }

            cart.setItems(items);
        }
        else {
            cart.setItems(new ArrayList<>());
        }

        return cart;
    }

    /**
     * Convert a Cart object to a document
     *
     * @param cart
     * @return
     */
    private Document cartToDoc(Cart cart) {
        Document document = new Document();
        document.append("_id", cart.getId());
        document.append("status", cart.getStatus());
        document.append("last_modified", cart.getLast_modified());
        document.append("userid", cart.getUserid());
        document.append("items", cart.getItems());
        return document;
    }

    /**
     * Add an item to a cart
     *
     * @param item
     * @param userid
     */

    public void addToCart(Item item, String userid) {

        /**
         * TODO-lab2
         *
         * LAB #2: Add an item to a user's cart document
         *
         * HINT: There are several cases you must account for here, such as an empty initial cart
         */

        
        
        Document itemDoc = new Document("_id", item.getId())
            .append("title", item.getTitle())
            .append("category", item.getCategory())
            .append("quantity", item.getQuantity()) //
            .append("price", item.getPrice())
            .append("img_url", item.getImg_url());

        Document updateDoc = new Document("$push", new Document("items", itemDoc));
        cartCollection.updateOne(eq("userid", userid), updateDoc, new UpdateOptions().upsert(true));

    }

    /**
     * Update the quantity of an item in a cart.  If quantity is 0, remove item from cart
     *
     * @param itemid
     * @param quantity
     * @param userid
     */
    public void updateQuantity(int itemid, int quantity, String userid) {

        /**
         * TODO-lab2
         *
         * LAB #2: Update the quantity of an item in a users cart, if the quantity is 0, remove the item from the cart
         *
         * HINT: You may want to create a helper method for determining if an item already exists in a cart
         */

    }

}
