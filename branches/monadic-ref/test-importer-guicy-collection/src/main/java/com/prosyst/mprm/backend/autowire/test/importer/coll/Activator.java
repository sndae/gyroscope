package com.prosyst.mprm.backend.autowire.test.importer.coll;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.name.Names.named;
import static com.google.inject.util.Types.newParameterizedType;

import java.util.Collection;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;

/**
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  @Override
  public void configure() {
    Injector injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(collectionOf(RichHello.class)).toInstance( 
          require(RichHello.class)
          /*
           * The Autowire DSL requires an ObjectFactory instance at this spot. For
           * good Guice support here a Guice key() must be supplied instead. This
           * will allow the ObjectFactory instance to also be created by Guice and
           * get it's dependencies injected.
           */
          .from(Hello.class, new ObjectFactory.Adapter<Hello, RichHello>() {
            public RichHello create(final Hello delegate, Map<String, Object> props) {
              return new RichHello() {
                public void hello(String title, String name) {
                  ((Hello) delegate).hello(title + " " + name);
                }
              };
            }
          })
          .collection()
        );
            
        /*
         * This must be a singleton because later we want to hook a signal from
         * the collection of services to the printer.
         */
        bind(Printer.class).in(SINGLETON);
        /*
         * The worker needs a runnable. Tell Guice to use the printer as a
         * source of Runnables.
         */
        bind(Runnable.class).to(Printer.class);
        bind(String.class).annotatedWith(named("worker name")).toInstance("Printer worker");
      }
      
      /*
       * In case Worker has no @Inject annotated constructor we can annotate a
       * method to act as the constructor. Cool stuff from Guice 2.0.
       */
      @Provides
      @SuppressWarnings("unused")
      public Worker provideWorker(@Named("worker name") String name, Runnable task) {
        return new Worker(name, task);
      }
    });

    /*
     * Declare that the printer will start working as soon as the collection of
     * services becomes available. A collection becomes available (and empty) as
     * soon as the bundle is started.
     * 
     * Notice that to make this link we had to ask Guice to create the
     * Collection<RichHello> and the Printer objects. So we have effectively
     * bootstrapped our bundle a this spot. Now the two objects are strongly
     * referenced by Autowire and will automatically be started/stopped when the
     * appropriate conditions are met. In future versions of Autowire these
     * linkages must happen automatically and be guided by special Autowire
     * annotations on the linked classes.
     * 
     */
    from(injector.getInstance(Key.get(collectionOf(RichHello.class))))
    .notify(injector.getInstance(Printer.class));
  }
  
  /**
   * Going around Java 5's dreaded erasure in order to get type safety of
   * generified collections requires the crufly TypeLiteral - alas it can't be
   * made shorter.
   * 
   * @param <V>
   * @param typeParam
   * @return
   */
  @SuppressWarnings("unchecked")
  private static <V> TypeLiteral<Collection<V>> collectionOf(final Class<V> typeParam) {
    return (TypeLiteral<Collection<V>>) TypeLiteral.get(newParameterizedType(Collection.class, typeParam));
  }
}
