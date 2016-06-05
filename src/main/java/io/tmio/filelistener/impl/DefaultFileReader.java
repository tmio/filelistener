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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import io.tmio.filelistener.FileReader;

public class DefaultFileReader<T> implements FileReader<T> {

  private ObjectReader reader;

  public DefaultFileReader(Class<T> klass) {
    ObjectMapper mapper = new ObjectMapper();
    reader = mapper.readerFor(klass);
  }

  @Override
  public T read(File file) throws IOException {
    if (!file.exists()) {
      return null;
    }

    T result = reader.readValue(file);
    return result;
  }

}
