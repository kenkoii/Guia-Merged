package ph.com.guia.Model;

public class Reward {
    public String id, tour_name, tour_location, tour_details, points, image;

    public Reward(String id, String tour_name, String tour_location, String tour_details, String points,
                  String image) {
        this.id = id;
        this.tour_name = tour_name;
        this.tour_location = tour_location;
        this.tour_details = tour_details;
        this.points = points;
        this.image = image;
    }
}
