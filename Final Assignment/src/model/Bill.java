package model;

public class Bill {
    private double baseFee;
    private double discountAmount;
    private double penaltyAmount;
    private double netPayable;

    public Bill(double baseFee, double discountAmount, double penaltyAmount) {
        this.baseFee = baseFee;
        this.discountAmount = discountAmount;
        this.penaltyAmount = penaltyAmount;
        this.netPayable = Math.max(0.0, baseFee - discountAmount + penaltyAmount);
    }

    public double getBaseFee() {
        return baseFee;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getPenaltyAmount() {
        return penaltyAmount;
    }

    public double getNetPayable() {
        return netPayable;
    }

    public String generateDetailedReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("          RENTAL RECEIPT            \n");
        sb.append("====================================\n");
        sb.append(String.format("Base Rental Fee:    $%8.2f\n", baseFee));
        sb.append(String.format("Discounts Applied: -$%8.2f\n", discountAmount));
        sb.append(String.format("Penalties/Charges:  $%8.2f\n", penaltyAmount));
        sb.append("------------------------------------\n");
        sb.append(String.format("Net Payable Amount: $%8.2f\n", netPayable));
        sb.append("====================================\n");
        return sb.toString();
    }
}
