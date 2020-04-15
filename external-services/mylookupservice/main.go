package main

import (
	"fmt"
	"net/http"
	"io/ioutil"
	"log"
	"github.com/boltdb/bolt"
	"github.com/newrelic/go-agent"

)

func check(e error) {
    if e != nil {
		log.Fatal(e)
        panic(e)
    }
}

func lookupHandler(writer http.ResponseWriter, request *http.Request) {
	
	if isInstrumentationOn() {
		txn := writer.(newrelic.Transaction)
		payload := request.Header.Get("NR-Payload")
		// txn := app.StartTransaction("/Inbound/Callee_LookupCommServiceTransaction",writer,request)
		txn.AcceptDistributedTracePayload(newrelic.TransportHTTP, payload)
		defer txn.End()
		
	}

	var service = request.URL.RawQuery
	var url = ""
	if service=="gameCommCalc" {
		url = getGameCommCalcURL()
	}
	
	fmt.Fprintf(writer, "%s", url)
}

func setServiceHandler(writer http.ResponseWriter, request *http.Request) {
	// var dealSize = request.URL.Path[1:]
	var newService = request.URL.RawQuery
	var newurl = "http://"+newService+"/calcComm"
	setCommCalcURL(newurl)
	fmt.Fprintf(writer, "gameCommCalc Service URL updated to %s", newurl)
}

func getVersionHandler(writer http.ResponseWriter, request *http.Request) {
	fmt.Fprintf(writer, "Version: %s", getVersion())
}

func testOutboundHandler(writer http.ResponseWriter, request *http.Request) {
	resp, err := http.Get("https://www.google.com")
	check(err)
	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)
	check(err)
	
	fmt.Fprintf(writer, "Called External Service and Returned: %s", body)
}



func getGameCommCalcURL() string {
	
	url := "http://localhost:9090/commCalc"
	db, err := bolt.Open("lookupservice.db", 0600, nil)
	check(err)
	defer db.Close()

	db.View(func (tx *bolt.Tx) error {
		b := tx.Bucket([]byte("LookupBucket"))
		v := b.Get([]byte("gameCommCalc"))
		url = string(v)
		return nil
	})
 
	return url
	

	/* old implementation - read from file
	dat, err := ioutil.ReadFile("./commCalcURL.txt")
	check(err)
	fmt.Println(string(dat))
	return string(dat)
	*/
}

func setCommCalcURL(newURL string) string {
	db, err := bolt.Open("lookupservice.db", 0600, nil)
	check(err)
	defer db.Close()

	err = db.Update(func(tx *bolt.Tx) error {
		b, err := tx.CreateBucketIfNotExists([]byte("LookupBucket"))
		if err != nil {
			return fmt.Errorf("create bucket: %s", err)
		}
		err = b.Put([]byte("gameCommCalc"),[]byte(newURL))
		if err != nil {
			return fmt.Errorf("put bucket: %s", err)
		}
		fmt.Println("gameCommCalc Service Registered @ " + newURL)
		return nil
	})
	check(err)

	return "Added: gameCommCalc @ " + newURL

	/* old implementation - read from file
	var f, err1 = os.Create("./commCalcURL.txt")
	check(err1)
	
	var b, err2 = f.WriteString(newURL)
	check(err2)
	fmt.Printf("Updated CommCalcURL to %s - number of bytes: %d\n", newURL,b)

	f.Sync()
	f.Close()
	*/
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

func getVersion() string {
	return "LookupService v1.0"
}

func makeNRApplication()(newrelic.Application) {
	config := newrelic.NewConfig(getNRAppName(), "9618c282f3ad69f9d7ed21d5808651fe4b5abf1e")
	config.DistributedTracer.Enabled = true
	app, err := newrelic.NewApplication(config)
	check(err)
	return app
}

func main() {

	if isInstrumentationOn() {
		app := makeNRApplication()
		http.HandleFunc(newrelic.WrapHandleFunc(app, "/lookup", lookupHandler))
		http.HandleFunc(newrelic.WrapHandleFunc(app, "/setService", setServiceHandler))
		http.HandleFunc(newrelic.WrapHandleFunc(app, "/getVersion", getVersionHandler))
		http.HandleFunc(newrelic.WrapHandleFunc(app, "/testOutbound", testOutboundHandler))
	} else {
	
		http.HandleFunc("/lookup", lookupHandler)
		http.HandleFunc("/setService", setServiceHandler)
		http.HandleFunc("/getVersion", getVersionHandler)
		http.HandleFunc("/testOutbound", testOutboundHandler)
	}

	http.ListenAndServe(":8888", nil)
}
