package edu.uv.cs.cache.common;


import static org.junit.Assert.*;
import org.junit.Test;



public class PairTest {
   @Test
   public void testPair() {
      Pair p = new Pair("key","value");
      assertEquals("key",p.getKey());
      assertEquals("value", p.getValue());      
  }
}

