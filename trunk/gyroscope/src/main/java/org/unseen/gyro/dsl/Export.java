/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unseen.gyro.dsl;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.Transformer;


/**
 * @author Todor Boev
 *
 */
public interface Export {
  interface Builder<A, V> extends ModeSelector<A> {
    <N> Builder<N, V> from(Class<N> type, Transformer<N, A> fact);
    
    <N> Builder<A, N> as(Class<N> type, Transformer<V, N> fact);
    
    Builder<A, V> attributes(Map<String, Object> attrs);
  }
  
  interface ModeSelector<T> {
    Ref<T, ServiceRegistration/*<T>*/> single();
    
    Ref<T, ServiceRegistration/*<T>*/> single(T instance);
    
    Ref<Transformer<Bundle, T>, ServiceRegistration/*<T>*/> multiple();
    
    Ref<Transformer<Bundle, T>, ServiceRegistration/*<T>*/> multiple(Transformer<Bundle, T> instance);
  }
}
