package biz.turnonline.ecosystem.product.billing;

import biz.turnonline.ecosystem.billing.facade.ProductBillingAdapteeModule;
import biz.turnonline.ecosystem.billing.facade.ProductBillingClientModule;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.restapi.client.appengine.CtoolkitRestFacadeAppEngineModule;
import org.ctoolkit.restapi.client.appengine.DefaultOrikaMapperFactoryModule;

/**
 * Guice module.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class TestModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new CtoolkitRestFacadeAppEngineModule() );
        install( new DefaultOrikaMapperFactoryModule() );
        install( new ProductBillingClientModule() );
        install( new ProductBillingAdapteeModule() );

        ApiCredential configuration = new ApiCredential();
        configuration.load( "/api.properties" );
        Names.bindProperties( binder(), configuration );
    }
}
