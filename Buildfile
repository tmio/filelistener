require 'buildr/gpg'
require 'buildr/custom_pom'

repositories.release_to[:url] = 'https://oss.sonatype.org/service/local/staging/deploy/maven2/'
repositories.release_to[:username] = ENV['USERNAME']
repositories.release_to[:password] = ENV['PASSWORD']

VERSION_NUMBER="1.0.0-SNAPSHOT"

DEPENDENCIES = [
  transitive('com.fasterxml.jackson.core:jackson-databind:jar:2.7.4'),
  'org.slf4j:slf4j-api:jar:1.7.21'
]
  
COMMONSIO = 'commons-io:commons-io:jar:2.4'

define('filelistener', :group => 'io.tmio', :version => VERSION_NUMBER) do
  compile.with(DEPENDENCIES)

  package(:jar)
  package(:sources)
  package(:javadoc)
  
  test.with(COMMONSIO)

  pom.add_apache_v2_license
  pom.add_github_project('tmio/filelistener')
  pom.add_developer('atoulme', 'Antoine Toulme')
end
