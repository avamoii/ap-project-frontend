// مسیر: src/main/java/org/example/approjectfrontend/api/UpdateRestaurantRequest.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class UpdateRestaurantRequest {
    @SerializedName("name") private String name;
    @SerializedName("address") private String address;
    @SerializedName("phone") private String phone;
    @SerializedName("logoBase64") private String logoBase64;
    @SerializedName("tax_fee") private Integer taxFee;
    @SerializedName("additional_fee") private Integer additionalFee;

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }
    public void setTaxFee(Integer taxFee) { this.taxFee = taxFee; }
    public void setAdditionalFee(Integer additionalFee) { this.additionalFee = additionalFee; }
}