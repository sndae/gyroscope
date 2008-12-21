package com.prosyst.mprm.backend.proxy.gen;

import com.prosyst.mprm.backend.proxy.ref.Ref;

public interface ProxyFactory {
  <V> V proxy(Class<V> type, Ref<?, V> ref);
}