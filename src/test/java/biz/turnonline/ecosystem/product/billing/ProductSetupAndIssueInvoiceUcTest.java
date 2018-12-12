package biz.turnonline.ecosystem.product.billing;

import biz.turnonline.ecosystem.billing.model.Invoice;
import biz.turnonline.ecosystem.billing.model.Order;
import biz.turnonline.ecosystem.billing.model.Product;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.RestFacade;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.truth.Truth.assertThat;

/**
 * Product Billing REST API tests.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
@Guice( modules = TestModule.class )
public class ProductSetupAndIssueInvoiceUcTest
{
    @Inject
    private RestFacade facade;

    private Long productId;

    private Long orderId;

    private Long invoiceId;

    @Inject()
    @Named( "onBehalf.email" )
    private String email;

    @Inject()
    @Named( "onBehalf.identityId" )
    private String identityId;

    /**
     * To deserialize from JSON to {@link com.google.api.client.json.GenericJson}.
     */
    public static <T> T genericJsonFromFile( String json, Class<T> valueType )
    {
        InputStream stream = valueType.getResourceAsStream( json );
        if ( stream == null )
        {
            String msg = json + " file has not been found in resource package " + valueType.getPackage() + ".";
            throw new IllegalArgumentException( msg );
        }

        T item = null;

        try
        {
            com.google.api.client.json.JsonFactory factory = new JacksonFactory();
            item = factory.fromInputStream( stream, valueType );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return item;
    }

    @Test
    public void createProduct()
    {
        Product product = genericJsonFromFile( "product.json", Product.class );

        Product updated = facade.insert( product )
                .onBehalf( email, identityId )
                .finish();

        productId = updated.getId();
        assertThat( updated.getId() ).isNotNull();
    }

    @Test( dependsOnMethods = "createProduct" )
    public void createOrder()
    {
        Order order = genericJsonFromFile( "order.json", Order.class );
        order.getCustomer().setContactEmail( email );
        order.getItems().get( 0 ).getProduct().setId( productId );

        Order updated = facade.insert( order )
                .onBehalf( email, identityId )
                .finish();

        orderId = updated.getId();
        assertThat( updated.getId() ).isNotNull();
    }

    @Test( dependsOnMethods = "createOrder" )
    public void createInvoice()
    {
        // empty instance of the invoice means no values from the order will be overridden
        // orderId identifies the parent order to be used to issue an invoice
        Invoice updated = facade.insert( new Invoice(), new Identifier( orderId ) )
                .onBehalf( email, identityId )
                .finish();

        invoiceId = updated.getId();
        assertThat( updated.getId() ).isNotNull();
    }

    @Test( dependsOnMethods = "createInvoice" )
    public void cleanup() throws InterruptedException
    {
        Identifier identifier = new Identifier( orderId ).add( invoiceId );
        facade.delete( Invoice.class ).identifiedBy( identifier )
                .onBehalf( email, identityId )
                .finish();

        // workaround, wait until invoice will be deleted from datastore, otherwise order deletion fails
        Thread.sleep( 2000 );

        facade.delete( Order.class ).identifiedBy( orderId )
                .onBehalf( email, identityId )
                .finish();

        facade.delete( Product.class ).identifiedBy( productId )
                .onBehalf( email, identityId )
                .finish();
    }
}
