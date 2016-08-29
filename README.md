[![Build Status](https://travis-ci.org/tmio/filelistener.svg?branch=master)](https://travis-ci.org/tmio/filelistener)

#File Listener

You can use this library to create a file listener that reads and caches the file contents, monitoring the file mtime to reload contents on demand.

#Examples

## Reading a JSON file

With a Map:
```
FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(new File("someFile.json"), Map.class);
Map contents = monitor.get();  
```

With your custom type:
```
FileMonitor<CustomPOJO> monitor = FileMonitorFactory.createFileMonitor(new File("someFile.json"), CustomPOJO.class);
CustomPOJO contents = monitor.get();  
```

## Make your own reader

```
public class CountingFileReader<T> extends DefaultFileReader<T> {

  private int count = 0;

  public CountingFileReader(Class klass) {
    super(klass);
  }

  @Override
  public T read(File file) throws IOException {
    count++;
    return super.read(file);
  }

  public int getCount() {
    return count;
  }
}
```

```
CountingFileReader<Map> reader = new CountingFileReader<>(MyPOJO.class);
FileMonitor<Map> monitor = FileMonitorFactory.createFileMonitor(new File("someFile.dat"), reader);
```

#License

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