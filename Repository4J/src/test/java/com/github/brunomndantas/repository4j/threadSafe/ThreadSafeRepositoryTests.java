package com.github.brunomndantas.repository4j.threadSafe;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import com.github.brunomndantas.repository4j.memory.MemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class ThreadSafeRepositoryTests extends RepositoryTests {

    private static class SleepRepository<K,E> extends MemoryRepository<K,E> {

        public SleepRepository(Function<E, K> keyExtractor) {
            super(keyExtractor);
        }


        @Override
        public Collection<E> getAll() {
            sleep();
            return super.getAll();
        }

        @Override
        public E get(K key) {
            sleep();
            return super.get(key);
        }

        @Override
        public void insert(E entity) throws RepositoryException {
            sleep();
            super.insert(entity);
        }

        @Override
        public void update(E entity) throws RepositoryException {
            sleep();
            super.update(entity);
        }

        @Override
        public void delete(K key) {
            sleep();
            super.delete(key);
        }

        private void sleep() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }


    @Override
    protected ThreadSafeRepository<String, Person> createRepository() {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        return new ThreadSafeRepository<>(sourceRepository);
    }


    @Test
    public void shouldAcquireReadLockOnGetAll() throws RepositoryException {
        AtomicBoolean readLockAcquired = new AtomicBoolean();
        AtomicBoolean writeLockAcquired = new AtomicBoolean();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public Collection<Person> getAll() {
                readLockAcquired.set(lock.getReadLockCount() == 1);
                writeLockAcquired.set(lock.isWriteLocked());
                return new LinkedList<>();
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        repository.getAll();

        Assertions.assertTrue(readLockAcquired.get());
        Assertions.assertFalse(writeLockAcquired.get());
    }

    @Test
    public void shouldAcquireReadLockOnGet() throws RepositoryException {
        AtomicBoolean readLockAcquired = new AtomicBoolean();
        AtomicBoolean writeLockAcquired = new AtomicBoolean();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public Person get(String key) {
                readLockAcquired.set(lock.getReadLockCount() == 1);
                writeLockAcquired.set(lock.isWriteLocked());
                return null;
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        repository.get("1");

        Assertions.assertTrue(readLockAcquired.get());
        Assertions.assertFalse(writeLockAcquired.get());
    }

    @Test
    public void shouldAcquireWriteLockOnInsert() throws RepositoryException {
        AtomicBoolean readLockAcquired = new AtomicBoolean();
        AtomicBoolean writeLockAcquired = new AtomicBoolean();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public void insert(Person entity) {
                readLockAcquired.set(lock.getReadLockCount() != 0);
                writeLockAcquired.set(lock.isWriteLocked());
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        repository.insert(new Person("1", "A"));

        Assertions.assertFalse(readLockAcquired.get());
        Assertions.assertTrue(writeLockAcquired.get());
    }

    @Test
    public void shouldAcquireWriteLockOnUpdate() throws RepositoryException {
        AtomicBoolean readLockAcquired = new AtomicBoolean();
        AtomicBoolean writeLockAcquired = new AtomicBoolean();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public void update(Person entity) {
                readLockAcquired.set(lock.getReadLockCount() != 0);
                writeLockAcquired.set(lock.isWriteLocked());
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        repository.update(new Person("1", "A"));

        Assertions.assertFalse(readLockAcquired.get());
        Assertions.assertTrue(writeLockAcquired.get());
    }

    @Test
    public void shouldAcquireWriteLockOnDelete() throws RepositoryException {
        AtomicBoolean readLockAcquired = new AtomicBoolean();
        AtomicBoolean writeLockAcquired = new AtomicBoolean();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public void delete(String key) {
                readLockAcquired.set(lock.getReadLockCount() != 0);
                writeLockAcquired.set(lock.isWriteLocked());
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        repository.delete("1");

        Assertions.assertFalse(readLockAcquired.get());
        Assertions.assertTrue(writeLockAcquired.get());
    }

    @Test
    public void shouldFreeLockIfGetAllFails() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public Collection<Person> getAll() {
                throw new IllegalArgumentException("Error");
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        Assertions.assertThrows(IllegalArgumentException.class, repository::getAll);
        Assertions.assertEquals(0, lock.getReadLockCount());
    }

    @Test
    public void shouldFreeLockIfGetFails() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public Person get(String key) {
                throw new IllegalArgumentException("Error");
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.get("1"));
        Assertions.assertEquals(0, lock.getReadLockCount());
    }

    @Test
    public void shouldFreeLockIfInsertFails() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public void insert(Person entity) {
                throw new IllegalArgumentException("Error");
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.insert(new Person("1", "A")));
        Assertions.assertFalse(lock.isWriteLocked());
    }

    @Test
    public void shouldFreeLockIfUpdateFails() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public void update(Person entity) {
                throw new IllegalArgumentException("Error");
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.update(new Person("1", "A")));
        Assertions.assertFalse(lock.isWriteLocked());
    }

    @Test
    public void shouldFreeLockIfDeleteFails() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public void delete(String key) {
                throw new IllegalArgumentException("Error");
            }
        };
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository, lock);

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.delete("1"));
        Assertions.assertFalse(lock.isWriteLocked());
    }

    @Test
    public void shouldAllowMultipleReads() throws Exception {
        IRepository<String,Person> sourceRepository = new SleepRepository<>(person -> person.id);
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(3);

        createThread(repository::getAll, start, finish).start();
        createThread(repository::getAll, start, finish).start();
        createThread(()->repository.get("A"), start, finish).start();

        Thread.sleep(1000);
        start.countDown();

        Thread.sleep(1500);
        Assertions.assertEquals(0, finish.getCount());
    }

    @Test
    public void shouldOnlyAllowOneWrite() throws Exception {
        IRepository<String,Person> sourceRepository = new SleepRepository<>(person -> person.id);
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(3);

        createThread(()->repository.insert(new Person("1", "A")), start, finish).start();
        createThread(()->repository.update(new Person("1", "B")), start, finish).start();
        createThread(()->repository.delete("A"), start, finish).start();

        Thread.sleep(1000);
        start.countDown();

        Thread.sleep(500);
        Assertions.assertEquals(3, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(2, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(1, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(0, finish.getCount());
    }

    @Test
    public void shouldNotAllowReadAndWriteOnInsert() throws Exception {
        IRepository<String,Person> sourceRepository = new SleepRepository<>(person -> person.id);
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(2);

        createThread(()->repository.insert(new Person("1", "A")), start, finish).start();
        createThread(()->repository.get("1"), start, finish).start();

        Thread.sleep(1000);
        start.countDown();

        Thread.sleep(500);
        Assertions.assertEquals(2, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(1, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(0, finish.getCount());
    }

    @Test
    public void shouldNotAllowReadAndWriteOnUpdate() throws Exception {
        IRepository<String,Person> sourceRepository = new SleepRepository<>(person -> person.id);
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(2);

        createThread(()->repository.update(new Person("1", "A")), start, finish).start();
        createThread(()->repository.get("1"), start, finish).start();

        Thread.sleep(1000);
        start.countDown();

        Thread.sleep(500);
        Assertions.assertEquals(2, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(1, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(0, finish.getCount());
    }

    @Test
    public void shouldNotAllowReadAndWriteOnDelete() throws Exception {
        IRepository<String,Person> sourceRepository = new SleepRepository<>(person -> person.id);
        ThreadSafeRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(2);

        createThread(()->repository.delete("1"), start, finish).start();
        createThread(()->repository.get("1"), start, finish).start();

        Thread.sleep(1000);
        start.countDown();

        Thread.sleep(500);
        Assertions.assertEquals(2, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(1, finish.getCount());

        Thread.sleep(1000);
        Assertions.assertEquals(0, finish.getCount());
    }

    private Thread createThread(Executable executable, CountDownLatch start, CountDownLatch finish) {
        return new Thread(() -> {
            try {
                start.await();
                executable.execute();
            } catch (Throwable e) {
            } finally {
                finish.countDown();
            }
        });
    }

}