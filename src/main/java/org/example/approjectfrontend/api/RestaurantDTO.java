// مسیر: src/main/java/org/example/approjectfrontend/api/RestaurantDTO.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class RestaurantDTO {
    @SerializedName("id") private Long id;
    @SerializedName("name") private String name;
    @SerializedName("address") private String address;
    @SerializedName("phone") private String phone;
    @SerializedName("logoBase64") private String logoBase64;
    @SerializedName("tax_fee") private Integer taxFee;
    @SerializedName("additional_fee") private Integer additionalFee;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getLogoBase64() { return logoBase64; }
    public Integer getTaxFee() { return taxFee; }
    public Integer getAdditionalFee() { return additionalFee; }
}