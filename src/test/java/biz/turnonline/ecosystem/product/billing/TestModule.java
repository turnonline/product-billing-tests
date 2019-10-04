package biz.turnonline.ecosystem.product.billing;

import biz.turnonline.ecosystem.steward.facade.AccountStewardAdapterModule;
import biz.turnonline.ecosystem.steward.facade.AccountStewardClientModule;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.restapi.client.appengine.CtoolkitRestFacadeAppEngineModule;
import org.ctoolkit.restapi.client.appengine.CtoolkitRestFacadeDefaultOrikaModule;

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
        install( new CtoolkitRestFacadeDefaultOrikaModule() );
        install( new AccountStewardClientModule() );
        install( new AccountStewardAdapterModule() );

        ApiCredential configuration = new ApiCredential();
        configuration.load( "/api.properties" );
        Names.bindProperties( binder(), configuration );
    }
}
