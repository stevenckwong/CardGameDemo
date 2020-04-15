package main

import (
	"fmt"
	"net/http"
	"strconv"
	"io/ioutil"
	"github.com/newrelic/go-agent"
	"time"
)

func calcCommHandler(writer http.ResponseWriter, request *http.Request) {
	
	if isInstrumentationOn() {
		txn := writer.(newrelic.Transaction)
		payload := request.Header.Get("NR-Payload")
		// txn := app.StartTransaction("/Inbound/Callee_CalculateCommissionTransaction",writer,request)
		txn.AcceptDistributedTracePayload(newrelic.TransportHTTP, payload)
		defer txn.End()
	}

	var dealSizeString = request.URL.RawQuery
	var d, err = strconv.Atoi(dealSizeString)
	check(err)
	var c = calcComm(d)
	fmt.Fprintf(writer, "%d", c)
}

func getVersionHandler(writer http.ResponseWriter, request *http.Request) {
	fmt.Fprintf(writer, "Version: %s", getVersion())
}

func testOutboundHandler(writer http.ResponseWriter, request *http.Request) {
	resp, err := http.Get("http://www.google.com")
	check(err)
	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)
	check(err)
	
	fmt.Fprintf(writer, "Called External Service and Returned: %s", body)
}

func check(e error) {
    if e != nil {
        panic(e)
    }
}

func calcComm(dealSize int) int {
	comm := 0
	wait := 0

	if dealSize > 10 {
		comm = 1
		wait = 250
		if dealSize > 100 {
			comm = 10
			wait = 500
			if dealSize > 1000 {
				comm = 100
				wait = 1000
				if dealSize > 10000 {
					comm = 1000
					wait = 2000
				}
			}
		}
	}
	// time.Sleep(time.Duration(wait) * time.Millisecond)
	calcCommWait(wait)
	return comm
}


func getVersion() string {
	return "v2.1"
}

func isInstrumentationOn() bool {
	dat, err := ioutil.ReadFile("./instrumentation.txt")
	check(err)
	// fmt.Println("instrumentation: " + string(dat))
	if string(dat)=="on" {
		fmt.Println("Instrumentation: On")
		return true
	} else {
		fmt.Println("Instrumentation: Off")
		return false
	}
	
}

func getNRAppName() string {
	dat, err := ioutil.ReadFile("./nrappname.txt")
	check(err)
	// fmt.Println("instrumentation: " + string(dat))
	return string(dat)
}


func makeNRApplication()(newrelic.Application) {
	config := newrelic.NewConfig(getNRAppName(), "9618c282f3ad69f9d7ed21d5808651fe4b5abf1e")
	config.DistributedTracer.Enabled = true
	app, err := newrelic.NewApplication(config)
	check(err)
	return app
}

func calcCommWait(interval int) {
	ticker := time.NewTicker(time.Millisecond)
	defer ticker.Stop()
	done := make(chan bool)
	go func() {
		time.Sleep(time.Duration(interval) * time.Millisecond)
		done <- true
	}()
	for {
		select {
		case <-done:
			fmt.Println("Done!")
			return
		case t := <-ticker.C:
			fmt.Println("Current time: ", t)
		}
	}
}

func main() {

	if isInstrumentationOn() {
		app := makeNRApplication()
		http.HandleFunc(newrelic.WrapHandleFunc(app, "/calcComm", calcCommHandler))
		http.HandleFunc(newrelic.WrapHandleFunc(app, "/getVersion", getVersionHandler))
		http.HandleFunc(newrelic.WrapHandleFunc(app, "/testOutbound", testOutboundHandler))
	} else {
	
		http.HandleFunc("/calcComm", calcCommHandler)
		http.HandleFunc("/getVersion", getVersionHandler)
		http.HandleFunc("/testOutbound", testOutboundHandler)
	}

	http.ListenAndServe(":9090", nil)
}
