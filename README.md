Your task is to implement a technical Maven or Gradle module to calculate the amount to pay for the phone bill according to the call statement.

The input of the method is a text string containing the call statement. 
The call dump is in CSV format with the following fields:
- Phone number in normalized form containing only numbers (e.g. 420774567453)
- Start of call in the form dd-MM-yyyy HH:mm:ss
- End of call in the form dd-MM-yyyy HH:mm:ssExample:420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00

For the purpose of the test, you can assume that the data in the input will always strictly conform to the above rules. 

The output of the method is the amount to be paid calculated according to the input statement according to the following rules:
- - Minute rate in the interval <8:00:00,16:00:00) is charged 1 CZK for each minute started. Outside the above interval, the reduced rate is 0.50 CZK per minute. For each minute of the call, the time of the minute is the determining factor for determining the rate.
- - For calls longer than five minutes, a reduced rate of CZK 0.20 is paid for each additional minute beyond the first five minutes, regardless of the time the call is in progress.
  - As part of the operator's promotional offer, calls to the most frequently called number within the listing will not be charged. In case the listing contains two or more such numbers, calls to the number with the highest aritmeBcky value will not be charged.
