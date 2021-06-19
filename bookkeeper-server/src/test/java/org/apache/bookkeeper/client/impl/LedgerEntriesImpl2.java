package org.apache.bookkeeper.client.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.bookkeeper.client.api.LedgerEntry;
import org.apache.bookkeeper.client.impl.LedgerEntriesImpl;
import org.apache.bookkeeper.client.impl.LedgerEntryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(value = Parameterized.class)
public class LedgerEntriesImpl2 {

        private final int entryNumber = 3;
        private LedgerEntriesImpl ledgerEntriesImpl;
        private final List<LedgerEntry> entryList = Lists.newArrayList();

        private final long ledgerId = 1234L;
        private final byte[] dataBytes = "test entry data".getBytes(UTF_8);
        private final ArrayList<ByteBuf> bufs = Lists.newArrayListWithExpectedSize(entryNumber);


        @Before
        public void setup() {
            for (int i = 0; i < entryNumber; i++) {
                ByteBuf buf = Unpooled.wrappedBuffer(dataBytes);
                bufs.add(buf);
                entryList.add(LedgerEntryImpl.create(ledgerId,
                        i,
                        dataBytes.length,
                        buf));
            }


            ledgerEntriesImpl = LedgerEntriesImpl.create(entryList);
        }

        @After
        public void tearDown() {
            ledgerEntriesImpl.close();


            try {
                ledgerEntriesImpl.iterator();
                fail("Should fail iterator after close");
            } catch (NullPointerException e) {
                // expected behavior
            }
        }

        @Test
        public void testGetEntry() {
            LedgerEntry actualResult = null;
            try{
                actualResult = ledgerEntriesImpl.getEntry(0);
                assertEquals(entryList.get(0).getLedgerId(),  actualResult.getLedgerId());
                assertEquals(entryList.get(0).getEntryId(),  actualResult.getEntryId());
                assertEquals(entryList.get(0).getLength(),  actualResult.getLength());

            }catch(Exception e) {
                fail("Exception");
            }

            try {
                actualResult = ledgerEntriesImpl.getEntry(2);
                assertEquals(entryList.get(2).getLedgerId(),  actualResult.getLedgerId());
                assertEquals(entryList.get(2).getEntryId(),  actualResult.getEntryId());
                assertEquals(entryList.get(2).getLength(),  actualResult.getLength());
            }catch(Exception e) {
                fail("Exception");
            }

            try{
                actualResult = ledgerEntriesImpl.getEntry(-1);
                fail("Should get IndexOutOfBoundsException");
            }catch(Exception e) {

            }

            try{
                actualResult = ledgerEntriesImpl.getEntry(3);  //adeguacy
                fail("Should get IndexOutOfBoundsException");
            }catch(Exception e) {

            }

            List<LedgerEntry> entryList2 = Lists.newArrayList();
            for (int i = 1; i < entryNumber; i++) {
                ByteBuf buf = Unpooled.wrappedBuffer(dataBytes);
                bufs.add(buf);
                entryList2.add(LedgerEntryImpl.create(ledgerId,
                        i,
                        dataBytes.length,
                        buf));
            }

            LedgerEntriesImpl ledgerEntriesImpl2 = LedgerEntriesImpl.create(entryList2);

            try {
                actualResult = ledgerEntriesImpl2.getEntry(2);
                assertEquals(entryList2.get(1).getLedgerId(),  actualResult.getLedgerId());
                assertEquals(entryList2.get(1).getEntryId(),  actualResult.getEntryId());
                assertEquals(entryList2.get(1).getLength(),  actualResult.getLength());
            }catch(Exception e) {
                fail("Exception");
            }

        }
}
