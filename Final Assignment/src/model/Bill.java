package model;

public class Bill {
    private double baseFee;
    private double discountAmount;
    private double penaltyAmount;
    private double depositPaid;
    private double totalCharges;
    private double netSettlement; // positive represents refund, negative represents additional pay due

    public Bill(double baseFee, double discountAmount, double penaltyAmount, double depositPaid) {
        this.baseFee = baseFee;
        this.discountAmount = discountAmount;
        this.penaltyAmount = penaltyAmount;
        this.depositPaid = depositPaid;
        this.totalCharges = Math.max(0.0, baseFee - discountAmount + penaltyAmount);
        this.netSettlement = depositPaid - this.totalCharges;
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

    public double getDepositPaid() {
        return depositPaid;
    }

    public double getTotalCharges() {
        return totalCharges;
    }

    public double getNetSettlement() {
        return netSettlement;
    }

    public String generateDetailedReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("      RENTAL SETTLEMENT RECEIPT     \n");
        sb.append("====================================\n");
        sb.append(String.format("Base Rental Fee:    $%8.2f\n", baseFee));
        sb.append(String.format("Discounts Applied: -$%8.2f\n", discountAmount));
        sb.append(String.format("Penalties/Charges:  $%8.2f\n", penaltyAmount));
        sb.append("------------------------------------\n");
        sb.append(String.format("Total Rental Cost:  $%8.2f\n", totalCharges));
        sb.append(String.format("Initial Deposit:    $%8.2f\n", depositPaid));
        sb.append("------------------------------------\n");
        if (netSettlement >= 0) {
            sb.append(String.format("Refund to User:     $%8.2f\n", netSettlement));
            sb.append("====================================\n");
            sb.append(" STATUS: CLOSED - DEPOSIT REFUNDED  \n");
        } else {
            sb.append(String.format("Outstanding Balance: $%8.2f\n", Math.abs(netSettlement)));
            sb.append("====================================\n");
            sb.append(" STATUS: CLOSED - BALANCE PAID     \n");
        }
        sb.append("====================================\n");
        return sb.toString();
    }
}
