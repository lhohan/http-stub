# HTTP Stub server

A small test project that can serve as an example of an HTTP server, implemented with Akka-HTTP, that:


- writes all POST-ed 'reports' to files in the working directory
- reads the response from a (file-based) repository, and
- returns based on the report id found in the posted body either the matching response or a 404
- all ids can be retrieved by sending a GET request to the main URL

Default URL is: `http:localhost:6666:activate`. File repository is in `resources` directory. Files in local working dir are saved with `payload` prefix.


Running the server stub:
```
sbt run
```


Examples of curl commands:

GET all ids:
```
$ curl http://localhost:6666/activate
{
  "ids": ["100", "101", "102"]
}%
```

POST of known id:
```
$ curl -v -H "Content-Type: application/json" \
         http://localhost:6666/activate \
         -d '{"id": "100", "count": 12345}'
*   Trying ::1...
* TCP_NODELAY set
* Connection failed
* connect to ::1 port 6666 failed: Connection refused
*   Trying fe80::1...
* TCP_NODELAY set
* Connection failed
* connect to fe80::1 port 6666 failed: Connection refused
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 6666 (#0)
> POST /activate HTTP/1.1
> Host: localhost:6666
> User-Agent: curl/7.51.0
> Accept: */*
> Content-Type: application/json
> Content-Length: 29
>
* upload completely sent off: 29 out of 29 bytes
< HTTP/1.1 200 OK
< Server: akka-http/10.0.9
< Date: Sat, 12 Aug 2017 20:30:27 GMT
< Content-Type: application/json
< Content-Length: 24
<
* Curl_http_done: called premature == 0
* Connection #0 to host localhost left intact
{"result":"a", id="100"}%
```

POST of unknown id:
```
$ curl -v -H "Content-Type: application/json" \
         http://localhost:6666/activate \
         -d '{"id": "200", "count": 12345}'
*   Trying ::1...
* TCP_NODELAY set
* Connection failed
* connect to ::1 port 6666 failed: Connection refused
*   Trying fe80::1...
* TCP_NODELAY set
* Connection failed
* connect to fe80::1 port 6666 failed: Connection refused
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 6666 (#0)
> POST /activate HTTP/1.1
> Host: localhost:6666
> User-Agent: curl/7.51.0
> Accept: */*
> Content-Type: application/json
> Content-Length: 29
>
* upload completely sent off: 29 out of 29 bytes
< HTTP/1.1 404 Not Found
< Server: akka-http/10.0.9
< Date: Sat, 12 Aug 2017 20:30:54 GMT
< Content-Type: text/plain; charset=UTF-8
< Content-Length: 42
<
* Curl_http_done: called premature == 0
* Connection #0 to host localhost left intact
The requested resource could not be found.%
```