Lookup Service
=======================================

This is a simple Go application that returns the URL to the service that an application is looking for.

The URL to call to perform the lookup is as follows:
Second:
http://<ip-address>:9090/lookup?<serviceName> 
This call returns the URL to the commission service. 

Currently <serviceName> can be:
    gameCommCalc

The service uses an embedded database called Bolt.
https://github.com/boltdb/bolt

For monitoring, it uses new relic go-agent.
https://github.com/newrelic/go-agent


See the links above for more details on getting these pre-requisites when building this application
