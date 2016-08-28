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

import io.tmio.filelistener.impl.DefaultFileReader;
import io.tmio.filelistener.impl.DefaultFileReaderWithDefaultValue;
import io.tmio.filelistener.impl.FileMonitorImpl;

public class FileMonitorFactory {

  public static <T> FileMonitor<T> createFileMonitor(File file, Class<T> klass) {
    return new FileMonitorImpl<T>(file, new DefaultFileReader<T>(klass));
  }

  public static <T> FileMonitor<T> createFileMonitor(File file, FileReader<T> reader) {
    return new FileMonitorImpl<T>(file, reader);
  }

  public static <T> FileMonitor<T> createFileMonitorWithDefaultValue(File file, T defaultValue, Class<T> klass) {
    return new FileMonitorImpl<T>(file, new DefaultFileReaderWithDefaultValue<T>(defaultValue, klass));
  }
}
