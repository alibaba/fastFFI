/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
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
package com.alibaba.fastffi;

import java.io.Closeable;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.alibaba.fastffi.FFIUnsafe.U;

public class CXXValueScope implements AutoCloseable {

    private static final ThreadLocal<Buffer> BUFFER;

    static {
        BUFFER = ThreadLocal.withInitial(Buffer::new);
    }

    private final Segment snapshot;
    private final long top;
    private final int rc;

    public CXXValueScope() {
        Buffer buffer = BUFFER.get();
        snapshot = buffer.snapshot();
        assert snapshot != null;
        top = snapshot.top;
        rc = ++buffer.rc;
    }

    public static long allocate(int size) {
        Buffer buffer = BUFFER.get();
        long addr = buffer.allocate(size);
        assert addr > 0;
        return addr;
    }

    @Override
    public void close() {
        Buffer buffer = BUFFER.get();
        if (rc != buffer.rc) {
            throw new IllegalStateException();
        }
        buffer.rc--;
        buffer.restore(snapshot, top);
    }

    private static class Buffer {

        private static final long INIT_SEGMENT_SIZE = 64 * 1024; // 64k
        private static final long DEFAULT_SEGMENT_SIZE = 16 * 1024; // 16k
        private static final int CACHE_SIZE = 16;

        private final Queue<Segment> cache = new ArrayDeque<>(CACHE_SIZE);
        private Segment current;
        private int rc;

        long allocate(int size) {
            assert current != null;
            long res = current.allocate(size);
            if (res > 0) {
                return res;
            }

            long segSize = Math.max(DEFAULT_SEGMENT_SIZE, ((long) size) << 2);
            if (segSize == DEFAULT_SEGMENT_SIZE && cache.size() > 0) {
                Segment segment = cache.poll();
                segment.next = current;
                current = segment;
            } else {
                current = new Segment(segSize, current);
            }

            res = current.allocate(size);
            assert res > 0;
            return res;
        }

        Buffer() {
            current = new Segment(INIT_SEGMENT_SIZE, null);
        }

        Segment snapshot() {
            return current;
        }

        void restore(Segment target, long top) {
            assert target != null;
            while (current != target) {
                if (current.size == DEFAULT_SEGMENT_SIZE && cache.size() < CACHE_SIZE) {
                    current.top = current.base;
                    cache.offer(current);
                } else {
                    current.close();
                }
                current = current.next;
            }
            current.top = top;
        }
    }

    private static class Segment implements Closeable {

        private static final int DEFAULT_ALIGNMENT = 8;
        private final long end;
        private final long size;
        private long base;
        private Segment next;
        private long top;

        Segment(long size, Segment next) {
            top = base = U.allocateMemory(size);
            if (base == 0) {
                throw new OutOfMemoryError();
            }
            end = base + size;
            this.size = size;
            this.next = next;
        }

        static long round(long top, long size) {
            return (top + size + DEFAULT_ALIGNMENT - 1) & -DEFAULT_ALIGNMENT;
        }

        long allocate(int size) {
            assert size > 0;
            long newTop = round(top, size);
            if (newTop <= end) {
                long res = top;
                top = newTop;
                return res;
            }
            return 0;
        }

        @Override
        public void close() {
            if (base != 0) {
                U.freeMemory(base);
                base = 0;
            }
        }

        public void finalize() {
            close();
        }
    }
}
