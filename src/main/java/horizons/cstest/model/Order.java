package horizons.cstest.model;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Order {
    private final OrderSide side;
    private final long price; // assuming price is in whole pounds
    private final double quantity; // weight of the order
    private final String trader;

    public Order(OrderSide side, long price, double quantity, String trader)
    {
        if(side == null || price <= 0 || quantity <= 0 || isBlank(trader)){
            throw new IllegalArgumentException("Check constructor arguments!");
        }

        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.trader = trader;
    }

    public OrderSide getSide(){
        return side;
    }

    public long getPrice(){
        return price;
    }

    public double getQuantity(){
        return quantity;
    }

    public String getTrader(){
        return trader;
    }

    @Override
    public String toString() {
        // SELL: 1.2 kg for £310 [user]
        return side + ": " + quantity + " kg for £" + price + " [" + trader + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return price == order.price &&
                Double.compare(order.quantity, quantity) == 0 &&
                side == order.side &&
                trader.equals(order.trader);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = side.hashCode();
        result = 31 * result + (int) (price ^ (price >>> 32));
        temp = Double.doubleToLongBits(quantity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + trader.hashCode();
        return result;
    }
}
