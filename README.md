# Solar Power Plant Simulator

This project simulates a solar power plant network and estimates energy output over time.

### Domain knowledge:

* Each solar power plant produces an optimal power output of 20*(1-(D/365*0,005))kW (D=age in days of the power plant).
* Every solar power plant has the same output at a given age
* Each year has 1550 hours of sun of varying intensities, but to simplify our calculations it is considered that each year has 1000 hours of full sun
* A power plant output can be calculated using the full sun hours, where it is expected optimal power output out of the solar panels
* Solar power plants need a couple of days after installation to be able to start delivering energy. We can consider power starts being delivered once a power plant is older than 60 days.
* A solar power plant is considered having age = 0 on the day of it's installation
* A solar power plant breaks down 25 years after installation.


### Features:

* Upload a network of power plants with their names and ages.
* Calculate and retrieve the total estimated energy output for a specified number of days.
* Retrieve the network state with estimated output for each plant after a specified number of days.

### Technologies:
* Kotlin language
* Spring-boot Framework
* Maven
* MySQL database
* Hibernate
* Junit
* Mockito

### Installation:

* Clone the repository
* Install dependencies:
```bash
mvn install
```

* Run the application:
```bash
mvn spring-boot:run
```
Application will start running on port: 8000

### API Endpoints:


**POST** /solar-simulator/upload: Uploads a JSON file containing power plant network. 
  * Request Param: A json multipart file containing power plant network.
  * Request Param: T (integer) - The number of days for estimation.
  * Returns :
    * producedKWh: Total estimated energy output in kilowatt-hours (kWh) for T days.
    * Network: A list of objects representing each power plant

Input network.json:

```json
[
  {
    "name": "Power plant 1",
    "age": 854
  },
  {
    "name": "Power plant 2",
    "age": 473
  }
]
```
output:

```json
{
  "producedKWh": "value",
  "Network": [
    { "name": "Power plant 1", "age": "<age when T days elapse>" },
    { "name": "Power plant 2", "age": "<age when T days elapse>" }
  ]
}
```



**POST** /solar-simulator/load: Posts the current state of the power plant network, clearing all previous state.
* Request Body: An array of objects with properties name (string) and age (integer).
* Returns a Http Status Code 205 (Reset Content)

input:
```json
[
  {
    "name": "Power plant 1",
    "age": 854
  },
  {
    "name": "Power plant 2",
    "age": 473
  }
]
```
output: Status Code 205 (Reset Content)


**GET** /solar-simulator/output/T:{days}: Returns the total estimated energy output for T days of the existing power plants. 
  * Path Variable: T (integer) - The number of days for estimation.
  * Response: A map containing total output in kwh


output:

```json
{
  "total-output-in-kwh": "<OUTPUT>"
}

```

**GET** /solar-simulator/network/T: Returns the network state with estimated output for each plant after T days. 
  * Path Variable: T (integer) - The number of days for estimation. 
  * Response: An object containing:
    * name: Power Plant name. 
    * age: Power Plant age in days. 
    * outputInKWh: Estimated energy output in kWh for T days.

output:

```json
[
  {
    "name": "Power plant 1",
    "age": "<age>",
    "output-in-kwh": "<OUTPUT>"
  },
  {
    "name": "Power plant 2",
    "age": "<age>",
    "output-in-kwh": "<OUTPUT>"
  }
]

```
  
Note:
* Replace T in the API endpoints with the desired number of days for estimation.