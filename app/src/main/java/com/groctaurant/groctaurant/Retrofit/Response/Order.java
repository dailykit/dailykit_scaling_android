
package com.groctaurant.groctaurant.Retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class Order {

    @SerializedName("cus_phone")
    @Expose
    private String cusPhone;
    @SerializedName("cus_name")
    @Expose
    private String cusName;
    @SerializedName("cus_address")
    @Expose
    private String cusAddress;
    @SerializedName("mer_id")
    @Expose
    private String merId;
    @SerializedName("Recipe_SKU")
    @Expose
    private String recipeSKU;
    @SerializedName("Recipe_Name")
    @Expose
    private String recipeName;
    @SerializedName("rec_cuisine")
    @Expose
    private String recCuisine;
    @SerializedName("Recipe_Servings")
    @Expose
    private String recipeServings;
    @SerializedName("Recipe_Quantity")
    @Expose
    private String recipeQuantity;
    @SerializedName("rec_price")
    @Expose
    private String recPrice;
    @SerializedName("sub_total")
    @Expose
    private String subTotal;
    @SerializedName("discount_percentage")
    @Expose
    private String discountPercentage;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("new_cus_discount")
    @Expose
    private String newCusDiscount;
    @SerializedName("sgst")
    @Expose
    private String sgst;
    @SerializedName("cgst")
    @Expose
    private String cgst;
    @SerializedName("del_charges")
    @Expose
    private String delCharges;
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("grcash")
    @Expose
    private String grcash;
    @SerializedName("walletcash")
    @Expose
    private String walletcash;
    @SerializedName("final_amount")
    @Expose
    private String finalAmount;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("add_notes")
    @Expose
    private String addNotes;
    @SerializedName("del_time")
    @Expose
    private String delTime;
    @SerializedName("order_at")
    @Expose
    private String orderAt;
    @SerializedName("order_cancel_till")
    @Expose
    private String orderCancelTill;
    @SerializedName("Order_Status ")
    @Expose
    private String orderStatus;
    @SerializedName("Order_Number")
    @Expose
    private String orderNumber;
    @SerializedName("delivery_expected")
    @Expose
    private String deliveryExpected;
    @SerializedName("dispatch_real")
    @Expose
    private String dispatchReal;
    @SerializedName("number_of_ingredient")
    @Expose
    private String numberOfIngredient;
    @SerializedName("ingredient")
    @Expose
    private List<Ingredient> ingredient = null;

    public String getCusPhone() {
        return cusPhone;
    }

    public void setCusPhone(String cusPhone) {
        this.cusPhone = cusPhone;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getCusAddress() {
        return cusAddress;
    }

    public void setCusAddress(String cusAddress) {
        this.cusAddress = cusAddress;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getRecipeSKU() {
        return recipeSKU;
    }

    public void setRecipeSKU(String recipeSKU) {
        this.recipeSKU = recipeSKU;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecCuisine() {
        return recCuisine;
    }

    public void setRecCuisine(String recCuisine) {
        this.recCuisine = recCuisine;
    }

    public String getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        this.recipeServings = recipeServings;
    }

    public String getRecipeQuantity() {
        return recipeQuantity;
    }

    public void setRecipeQuantity(String recipeQuantity) {
        this.recipeQuantity = recipeQuantity;
    }

    public String getRecPrice() {
        return recPrice;
    }

    public void setRecPrice(String recPrice) {
        this.recPrice = recPrice;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getNewCusDiscount() {
        return newCusDiscount;
    }

    public void setNewCusDiscount(String newCusDiscount) {
        this.newCusDiscount = newCusDiscount;
    }

    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getDelCharges() {
        return delCharges;
    }

    public void setDelCharges(String delCharges) {
        this.delCharges = delCharges;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getGrcash() {
        return grcash;
    }

    public void setGrcash(String grcash) {
        this.grcash = grcash;
    }

    public String getWalletcash() {
        return walletcash;
    }

    public void setWalletcash(String walletcash) {
        this.walletcash = walletcash;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAddNotes() {
        return addNotes;
    }

    public void setAddNotes(String addNotes) {
        this.addNotes = addNotes;
    }

    public String getDelTime() {
        return delTime;
    }

    public void setDelTime(String delTime) {
        this.delTime = delTime;
    }

    public String getOrderAt() {
        return orderAt;
    }

    public void setOrderAt(String orderAt) {
        this.orderAt = orderAt;
    }

    public String getOrderCancelTill() {
        return orderCancelTill;
    }

    public void setOrderCancelTill(String orderCancelTill) {
        this.orderCancelTill = orderCancelTill;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDeliveryExpected() {
        return deliveryExpected;
    }

    public void setDeliveryExpected(String deliveryExpected) {
        this.deliveryExpected = deliveryExpected;
    }

    public String getDispatchReal() {
        return dispatchReal;
    }

    public void setDispatchReal(String dispatchReal) {
        this.dispatchReal = dispatchReal;
    }

    public String getNumberOfIngredient() {
        return numberOfIngredient;
    }

    public void setNumberOfIngredient(String numberOfIngredient) {
        this.numberOfIngredient = numberOfIngredient;
    }

    public List<Ingredient> getIngredient() {
        return ingredient;
    }

    public void setIngredient(List<Ingredient> ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("cusPhone", cusPhone).append("cusName", cusName).append("cusAddress", cusAddress).append("merId", merId).append("recipeSKU", recipeSKU).append("recipeName", recipeName).append("recCuisine", recCuisine).append("recipeServings", recipeServings).append("recipeQuantity", recipeQuantity).append("recPrice", recPrice).append("subTotal", subTotal).append("discountPercentage", discountPercentage).append("discount", discount).append("newCusDiscount", newCusDiscount).append("sgst", sgst).append("cgst", cgst).append("delCharges", delCharges).append("totalPrice", totalPrice).append("grcash", grcash).append("walletcash", walletcash).append("finalAmount", finalAmount).append("paymentType", paymentType).append("paymentStatus", paymentStatus).append("addNotes", addNotes).append("delTime", delTime).append("orderAt", orderAt).append("orderCancelTill", orderCancelTill).append("orderStatus", orderStatus).append("orderNumber", orderNumber).append("deliveryExpected", deliveryExpected).append("dispatchReal", dispatchReal).append("numberOfIngredient", numberOfIngredient).append("ingredient", ingredient).toString();
    }

}