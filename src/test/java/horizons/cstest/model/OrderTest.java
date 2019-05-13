package horizons.cstest.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static horizons.cstest.model.OrderSide.SELL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void orderIsSuccessfullyCreatedTest(){
        Order order = new Order(SELL, 306, 3.5, "user1");
        assertThat(order.getTrader(), is("user1"));
        assertThat(order.getQuantity(), is(3.5));
        assertThat(order.getPrice(), is(306L));
        assertThat(order.getSide(), is(SELL));
        assertThat(order.toString(), is("SELL: 3.5 kg for £306 [user1]"));
    }

    @Test
    public void orderConstructorThrowsExceptionForNullOrderSideTest(){
        exception.expect(IllegalArgumentException.class);
        Order order = new Order(null, 306, 3.5, "user1");
    }

    @Test
    public void orderConstructorThrowsExceptionForIllegalPriceTest(){
        exception.expect(IllegalArgumentException.class);
        Order order = new Order(SELL, -2, 3.5, "user1");
    }

    @Test
    public void orderConstructorThrowsExceptionForIllegalQuantityTest(){
        exception.expect(IllegalArgumentException.class);
        Order order = new Order(SELL, 306, 0, "user1");
    }

    @Test
    public void orderConstructorThrowsExceptionForBlankUserTest(){
        exception.expect(IllegalArgumentException.class);
        Order order = new Order(SELL, 306, 3.5, "  ");
    }

    @Test
    public void orderConstructorThrowsExceptionForNullUserTest(){
        exception.expect(IllegalArgumentException.class);
        Order order = new Order(SELL, 306, 3.5, null);
    }
}
