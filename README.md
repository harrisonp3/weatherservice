# Welcome to weatherservice
Built in IntelliJ using SDK: Oracle OpenJDK version 21

## Getting started
From the weatherservice directory in your terminal, run the following:

`./mvnw spring-boot:run`

Then (in a separate terminal instance) cd into weatherservice/app/ and run:

`npm start`

### Running tests
#### Server
From the weatherservice directory, run
`./mvnw test`
#### Client
From the weatherservice/app directory, run
`npm test`

### Using the app
When app is up and running in your browser, you can enter either a city name (eg. "miami", "los angeles", "providence,ri") OR you can enter coordinate values in the "latitude" and "longitude" input fields. Once you've entered either a city name or coordinates, hit enter and wait for results
