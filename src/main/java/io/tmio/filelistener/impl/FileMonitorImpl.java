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
package io.tmio.filelistener.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tmio.filelistener.FileMonitor;
import io.tmio.filelistener.FileReader;

public class FileMonitorImpl<T> implements FileMonitor<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileMonitorImpl.class);

  private File file;

  private T cached;

  private Long timestamp;

  private FileReader<T> fileReader;

  public FileMonitorImpl(File file, FileReader<T> reader) {
    if (file == null) {
      throw new IllegalArgumentException("file cannot be null");
    }
    if (reader == null) {
      throw new IllegalArgumentException("reader cannot be null");
    }
    this.file = file;
    this.fileReader = reader;
  }

  @Override
  public T get() {
    long localTimestamp = file.lastModified();
    if (timestamp == null || !timestamp.equals(localTimestamp)) {
      try {
        synchronized (this) {
          if (timestamp == null || !timestamp.equals(localTimestamp)) {
            cached = readContents();
            timestamp = file.lastModified();
          }
        }
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
    return cached;
  }

  protected T readContents() throws IOException {
    return fileReader.read(file);
  }

}
