package deti.tqs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProductFinderService {
    public static final String API_PRODUCTS = "/api/products";
    private ISimpleHttpClient httpClient;

    public ProductFinderService(ISimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Product findProductDetails(Integer id) {
        String response = httpClient.doHttpGet(API_PRODUCTS + "/" + id);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

        
        return new Product(
            jsonObject.get("id").getAsInt(),
            jsonObject.get("title").getAsString(),
            jsonObject.get("price").getAsDouble(),
            jsonObject.get("category").getAsString(),
            jsonObject.get("description").getAsString(),
            jsonObject.get("image").getAsString()
        );
    }
}
