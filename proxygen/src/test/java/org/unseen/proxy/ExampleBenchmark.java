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
package org.unseen.proxy;


/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ExampleBenchmark {
  private final String name;
  private final Example tested;
  private final int repeats;
  private final int warmup;
  
  public interface Example {
    double action(double id);
  }
  
  public static class ExampleImpl implements Example {
    public double action(double val) {
      return Math.exp(val * Math.cosh(val));
    }
  }
  
  public static class SyncExampleImpl implements Example {
    public synchronized double action(double val) {
      return Math.exp(val * Math.cosh(val));
    }
  }
  
  public ExampleBenchmark(String name, Example tested, int repeats, int warmup) {
    this.name = name;
    this.tested = tested;
    this.repeats = repeats;
    this.warmup = warmup;
  }
  
  public double benckmark(long baseline) {
    long time = time();
    
    double perc = 100*((double)time/baseline - 1);
        
    System.out.printf("%15s: %4d ns/call diff from base %4d ns/call is %6.2f%%\n", name, time, baseline, perc);
    return perc;
  }
  
  public long time() {
    act(warmup);
    return act(repeats);
  }
  
  private long act(int repeats) {
    System.gc();
    System.runFinalization();
    System.gc();
    
    double point = 0;
    double step = 1f/repeats;
    
    long now = System.nanoTime();
    double dummy = 0;
    for (int i = 0; i < repeats; i++, point +=step) {
      dummy += tested.action(point);
    }
    return (System.nanoTime() - now) / repeats;
  }
}
