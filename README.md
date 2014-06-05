# Follower Maze

A system which acts as a socket server, reading events from an *event source*
and forwarding them when appropriate to *user clients*.

Clients will connect through TCP and use the simple protocol described in a
section below. There will be two types of clients connecting to your server:

- **One** *event source*: It will send you a
stream of events which may or may not require clients to be notified
- **Many** *user clients*: Each one representing a specific user,
these wait for notifications for events which would be relevant to the
user they represent

### The Protocol
The protocol used by the clients is string-based (i.e. a `CRLF` control
character terminates each message). All strings are encoded in `UTF-8`.

The *event source* **connects on port 9090** and will start sending
events as soon as the connection is accepted.

The many *user clients* will **connect on port 9099**. As soon
as the connection is accepted, they will send to the server the ID of
the represented user, so that the server knows which events to
inform them of. For example, once connected a *user client* may send down:
`2932\r\n`, indicating that they are representing user 2932.

After the identification is sent, the *user client* starts waiting for
events to be sent to them. Events coming from *event source* should be
sent to relevant *user clients* exactly like read, no modification is
required or allowed.

### The Events
There are five possible events. The table below describe payloads
sent by the *event source* and what they represent:

| Payload                   | Seq #  | Type         | From User Id | To User Id |
|---------------------------|--------|--------------|--------------|------------|
|666&#124;F&#124;60&#124;50 | 666    | Follow       | 60           | 50         |
|1&#124;U&#124;12&#124;9    | 1      | Unfollow     | 12           | 9          |
|542532&#124;B              | 542532 | Broadcast    | -            | -          |
|43&#124;P&#124;32&#124;56  | 43     | Private Msg  | 32           | 56         |
|634&#124;S&#124;32         | 634    | Status Update| 32           | -          |

Using the verification program supplied, you will receive exactly 10000000 events,
with sequence number from 1 to 10000000. **The events will arrive out of order**.

*Note: Please do not assume that your code would only handle a finite sequence
of events, **we expect your server to handle an arbitrarily large events stream**
(i.e. you would not be able to keep all events in memory or any other storage)*

Events may generate notifications for *user clients*. **If there is a
*user client* ** connected for them, these are the users to be
informed for different event types:

* **Follow**: Only the `To User Id` should be notified
* **Unfollow**: No clients should be notified
* **Broadcast**: All connected *user clients* should be notified
* **Private Message**: Only the `To User Id` should be notified
* **Status Update**: All current followers of the `From User ID` should be notified

If there are no *user client* connected for a user, any notifications
for them must be silently ignored. *user clients* expect to be notified of
events **in the correct order**, regardless of the order in which the
*event source* sent them.

### Running

#### Production

```mvn clean compile exec:java -DskipTests -Dexec.mainClass="org.example.presentation.App"```

or from project root

```
mvn clean package -DskipTests
java -jar target/follower-maze-0.0.1-SNAPSHOT.jar
```

#### Unit & End-to-end Tests

```mvn clean test```

All classes fully unit tested except for the threaded entry-point which is covered by an end-to-end integration test
`AppEndToEndTest.java`. Every commit is built by a CI build on TravisCI:
https://travis-ci.org/williamboxhall/follower-maze/builds