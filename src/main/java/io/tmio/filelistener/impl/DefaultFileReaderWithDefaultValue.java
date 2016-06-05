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

public class DefaultFileReaderWithDefaultValue<T> extends DefaultFileReader<T> {

  private T defaultValue;

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileReaderWithDefaultValue.class);

  public DefaultFileReaderWithDefaultValue(T defaultValue, Class<T> klass) {
    super(klass);
    this.defaultValue = defaultValue;
  }

  @Override
  public T read(File file) throws IOException {
    T result = null;
    try {
      result = super.read(file);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    if (result == null) {
      return defaultValue;
    }
    return result;
  }
}
