package ph.com.guia.Model;

/**
 * Created by Kentoy on 12/30/2015.
 */
public class review {
    public String review_image, review_name, review_text;
    public int review_rate;

    public review(String review_image, String review_name, String review_text, int review_rate) {
        this.review_image = review_image;
        this.review_name = review_name;
        this.review_text = review_text;
        this.review_rate = review_rate;
    }
}
