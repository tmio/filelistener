/*
Copyright 2016 Antoine Toulme

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package io.tmio.filelistener;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.tmio.filelistener.FileMonitor;
import io.tmio.filelistener.FileMonitorFactory;
import io.tmio.filelistener.FileReader;

public class FileMonitorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testReadFile() {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    File file = new File(rootFolder, "test.txt");
    Assert.assertTrue(file.exists());
    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(file, Map.class);
    Assert.assertNotNull(monitor.get());
    Assert.assertEquals(1, monitor.get().size());
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());
  }

  @Test
  public void testFileMissing() {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(new File(rootFolder, "testNotThere.index"), Map.class);
    Assert.assertNull(monitor.get());
  }

  @Test
  public void testFileStartsExisting() throws Exception {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    File destFile = Files.createTempFile("monitored", "file").toFile();

    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(destFile, Map.class);
    Assert.assertNull(monitor.get());
    FileUtils.copyFile(new File(rootFolder, "test.txt"), destFile);
    Assert.assertNotNull(monitor.get());
  }

  @Ignore
  @Test
  public void testFileContentsChange() throws Exception {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    File destFile = Files.createTempFile("monitored", "file").toFile();
    FileUtils.copyFile(new File(rootFolder, "test.txt"), destFile);
    CountingFileReader<Map> reader = new CountingFileReader<>(Map.class);

    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(destFile, reader);
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());

    FileUtils.writeStringToFile(destFile, "{\"TarantinoMovies2\":[\"Django\"]}");
    Thread.sleep(5000);
    Assert.assertEquals("TarantinoMovies2", monitor.get().keySet().iterator().next());
    Assert.assertEquals(2, reader.getCount());

    Thread.sleep(2000);

    FileUtils.writeStringToFile(destFile, "{\"TarantinoMovies3\":[\"Reservoir Dogs\"]}");
    Thread.sleep(2000);

    Assert.assertEquals("TarantinoMovies3", monitor.get().keySet().iterator().next());
    Assert.assertEquals(3, reader.getCount());

  }

  @Test
  public void testFileIsDeleted() throws Exception {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    File destFile = Files.createTempFile("monitored", "file").toFile();
    FileUtils.copyFile(new File(rootFolder, "test.txt"), destFile);
    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(destFile, Map.class);
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());
    destFile.delete();
    Assert.assertNull(monitor.get());
  }

  @Test
  public void testFileDoesNotChange() throws Exception {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    File destFile = Files.createTempFile("monitored", "file").toFile();
    FileUtils.copyFile(new File(rootFolder, "test.txt"), destFile);
    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(destFile, Map.class);
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());
  }

  @Test
  public void testFileIsNotRereadIfNotChanged() throws Exception {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    File destFile = Files.createTempFile("monitored", "file").toFile();
    FileUtils.copyFile(new File(rootFolder, "test.txt"), destFile);
    CountingFileReader<Map> reader = new CountingFileReader<>(Map.class);

    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(destFile, reader);
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());
    Assert.assertEquals("TarantinoMovies", monitor.get().keySet().iterator().next());
    Assert.assertEquals(1, reader.getCount());
  }

  @Test
  public void testFileNull() {
    thrown.expect(IllegalArgumentException.class);
    FileMonitorFactory.createFileMonitor(null, Map.class);
  }

  @Test
  public void testFileReaderNull() {
    thrown.expect(IllegalArgumentException.class);
    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(new File("abc"), (FileReader) null);
  }

  @Test
  public void testClassNull() {
    thrown.expect(IllegalArgumentException.class);
    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(new File("abc"), (Class) null);
  }

  @Test
  public void testConcurrentAccess() throws Exception {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    CountingFileReader<Map> reader = new CountingFileReader<>(Map.class);
    final FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(new File(rootFolder, "test.txt"), reader);

    Callable<Void> call = new Callable<Void>() {

      @Override
      public Void call() throws Exception {
        monitor.get();
        return null;
      }
    };

    Collection<Callable<Void>> concurrentCalls = new ArrayList();
    for (int i = 0; i < 40; i++) {
      concurrentCalls.add(call);
    }
    ExecutorService service = Executors.newFixedThreadPool(20);
    List<Future<Void>> futures = service.invokeAll(concurrentCalls);

    for (Future f : futures) {
      f.get();
    }

    Assert.assertEquals(1, reader.getCount());
  }

  @Test
  public void testDefaultValueWithMissingFile() {
    String parentFile = FileMonitor.class.getResource("/").getFile();
    File rootFolder = new File(parentFile);
    Map defaultMap = new HashMap();
    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitorWithDefaultValue(new File(rootFolder, "testNotThere.index"), defaultMap, Map.class);
    Assert.assertEquals(defaultMap, monitor.get());
  }

  @Test
  public void testDefaultValueWithEmptyFile() throws Exception {
    Map defaultMap = new HashMap();
    File emptyFile = Files.createTempFile("test", "empty").toFile();

    FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitorWithDefaultValue(emptyFile, defaultMap, Map.class);
    Assert.assertEquals(defaultMap, monitor.get());
  }
}
