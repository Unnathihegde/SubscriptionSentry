package backend;

import java.sql.Date;

/**
 * Model class representing a Subscription in the system.
 * Matches the 'Subscriptions' database table columns.
 */
public class Subscription {
    private int subscriptionId;
    private String serviceName;
    private double cost;
    private String billingCycle;
    private Date nextRenewalDate;
    private int categoryId;
    private String categoryName; // Populated via SQL JOIN with Categories
    private boolean isFreeTrial;
    private String status;

    // Default Constructor
    public Subscription() {}

    // Parameterized Constructor without ID (useful for inserts)
    public Subscription(String serviceName, double cost, String billingCycle, Date nextRenewalDate,
                        int categoryId, boolean isFreeTrial, String status) {
        this.serviceName = serviceName;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.nextRenewalDate = nextRenewalDate;
        this.categoryId = categoryId;
        this.isFreeTrial = isFreeTrial;
        this.status = status;
    }

    // Complete Parameterized Constructor
    public Subscription(int subscriptionId, String serviceName, double cost, String billingCycle,
                        Date nextRenewalDate, int categoryId, boolean isFreeTrial, String status) {
        this.subscriptionId = subscriptionId;
        this.serviceName = serviceName;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.nextRenewalDate = nextRenewalDate;
        this.categoryId = categoryId;
        this.isFreeTrial = isFreeTrial;
        this.status = status;
    }

    // Getters and Setters
    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Date getNextRenewalDate() {
        return nextRenewalDate;
    }

    public void setNextRenewalDate(Date nextRenewalDate) {
        this.nextRenewalDate = nextRenewalDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isFreeTrial() {
        return isFreeTrial;
    }

    public void setFreeTrial(boolean freeTrial) {
        isFreeTrial = freeTrial;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
