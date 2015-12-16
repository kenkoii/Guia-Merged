package ph.com.guia.Model;

/**
 * Created by Stephanie Lyn on 11/9/2015.
 */
public class Tours {
    public String tour_id, tour_name, tour_location, tour_description,
            duration_format, tour_preference, tour_guideId, main_image;
    public String[] additional_images;
    public int tour_duration, tour_rate, points;

    public Tours(String tour_id, String tour_name, String tour_location,String tour_description,
                 String duration_format, String tour_preference, String tour_guideId, int tour_rate,
                 String main_image, int tour_duration, String[] additional_images, int points) {
        this.tour_id = tour_id;
        this.tour_name = tour_name;
        this.tour_location = tour_location;
        this.tour_description = tour_description;
        this.duration_format = duration_format;
        this.tour_preference = tour_preference;
        this.tour_guideId = tour_guideId;
        this.tour_rate = tour_rate;
        this.main_image = main_image;
        this.tour_duration = tour_duration;
        this.additional_images = additional_images;
        this.points = points;
    }
}
