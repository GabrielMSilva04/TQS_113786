package deti.tqs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
public class ProductFinderService {
    private static final String BASE_URL = "https://fakestoreapi.com";
    private static final String API_PRODUCTS = "/products";
    private ISimpleHttpClient httpClient;

    public ProductFinderService(ISimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Product findProductDetails(Integer id) {
        try {
            String response = httpClient.doHttpGet(BASE_URL + API_PRODUCTS + "/" + id);
            
            if (response == null || response.trim().isEmpty()) {
                return null;
            }

            JsonElement jsonElement = JsonParser.parseString(response);
            if (jsonElement.isJsonNull()) {
                return null;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            
            return new Product(
                jsonObject.get("id").getAsInt(),
                jsonObject.get("title").getAsString(),
                jsonObject.get("price").getAsDouble(),
                jsonObject.get("description").getAsString(),
                jsonObject.get("category").getAsString(),
                jsonObject.get("image").getAsString()
            );
        } catch (Exception e) {
            return null;
        }
    }
}
