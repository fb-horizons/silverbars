package horizons.cstest.model;

public class AggregatedOrder {

    // contains all order information except trader details aggregated on order side and price.
    private final OrderSide side;
    private final Long price;
    private final double quantity;

    public AggregatedOrder(OrderSide side, long price, double quantity) {
        if(side == null || price <= 0 || quantity <= 0){
            throw new IllegalArgumentException("Check constructor arguments!");
        }
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public OrderSide getSide() {
        return side;
    }

    @Override
    public String toString() {
        // 5.5 kg for £306
        return quantity + " kg for £" + price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregatedOrder that = (AggregatedOrder) o;
        return Double.compare(that.quantity, quantity) == 0 &&
                side == that.side &&
                price.equals(that.price);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = side.hashCode();
        result = 31 * result + price.hashCode();
        temp = Double.doubleToLongBits(quantity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
