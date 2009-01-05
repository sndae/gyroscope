package com.prosyst.mprm.backend.autowire;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.Transformer;
import com.prosyst.mprm.backend.proxy.ref.RefException;

/**
 * @author Todor Boev
 *
 * @param <V>
 */
public class ImportTransformer<V> implements Transformer<ServiceReference/*<V>*/, V> {
  private final BundleContext bc;
  
  public ImportTransformer(BundleContext bc) {
    this.bc = bc;
  }
  
  @SuppressWarnings("unchecked")
  public V create(ServiceReference arg, Map<String, Object> props) {
    V val = (V) bc.getService(arg);
    
    if (val == null) {
      throw new RefException("ServiceReference points to an unregistered service" + arg);
    }
    
    return val;
  }

  public void destroy(V val, ServiceReference arg, Map<String, Object> props) {
    bc.ungetService(arg);
  }
}
