# Example

Run this commands at **ibf-example** folder and check the results

### Getting default value
`mvn exec:java -Dexec.mainClass="com.rest4hub.ifb.example.App"`
### With env variable
`VERSION="1.0" mvn exec:java -Dexec.mainClass="com.rest4hub.ifb.example.App"`

### With system property
`mvn exec:java -DVERSION=1.1 -Dexec.mainClass="com.rest4hub.ifb.example.App"`

### Programmatically
`mvn exec:java -Dexec.mainClass="com.rest4hub.ifb.example.AppProgrammatically"`
