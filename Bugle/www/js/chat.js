//Make Connection
var socket = io.connect();
//IndexedDB
var indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB || window.shimIndexedDB;

//Query DOM
var message = document.getElementById('message');
var handle = document.getElementById('handle');
var btn = document.getElementById('send');

var output = document.getElementById('output');
var getbtn = document.getElementById('getdb');

//Emit Events
btn.addEventListener('click', function () {
    if (message.value) {
        socket.emit('chat', {
            message: message.value,
            handle: handle.value
        });
    }
    message.value = null;
});

//Listen for Events
socket.on('chat', function (data) {
    output.innerHTML += '<p><strong>' + data.handle + ': </strong>' + data.message + '</p>';

    //Starts =====
    var db;
    var openRequest = indexedDB.open('test_db', 4);

    openRequest.onupgradeneeded = function (e) {
        db = e.target.result;
        console.log('running onupgradeneeded');
        if (!db.objectStoreNames.contains('store')) {
            var storeOS = db.createObjectStore('store',
                { keyPath: 'id', autoIncrement: true });
        }
    };
    openRequest.onsuccess = function (e) {
        console.log('running onsuccess');
        db = e.target.result;
        addItem();
    };

    openRequest.onerror = function (e) {
        console.log('onerror!');
        console.dir(e);
    };

    function addItem() {
        var transaction = db.transaction(['store'], 'readwrite');
        var store = transaction.objectStore('store');
        var item = {
            name: data.handle,
            message: output.innerHTML
        };
        var clearRequest = store.clear();
        clearRequest.onerror = function (e) {
            console.log("Couldnt clear store");
        };
        clearRequest.onsuccess = function (e) {
            console.log("Store cleared!");
        }
        var request = store.add(item);

        request.onerror = function (e) {
            console.log('Error', e.target.error.name);
        };
        request.onsuccess = function (e) {
            console.log('Woot! Did it');
        };

        transaction.oncomplete = function (event) {
            db.close();
        }
    }
    //Ends ====
});

function initialize() {
    console.log('Displaying...');
    var db;
    var openRequest = indexedDB.open('test_db', 4);
    openRequest.onupgradeneeded = function (e) {
        db = e.target.result;
        console.log('running onupgradeneeded');
        if (!db.objectStoreNames.contains('store')) {
            var storeOS = db.createObjectStore('store',
                { keyPath: 'id', autoIncrement: true });
        }
    };

    openRequest.onsuccess = function (e) {
        console.log('running onsuccess');
         db = e.target.result;
        var count = 0;
        var answer="";
        var transaction = db.transaction(['store'], 'readwrite');
        var store = transaction.objectStore('store');
        store.openCursor().onsuccess = function (event) {
            var cursor = event.target.result;
            console.log(event.target.result);
            if (event.target.result) {
                console.log("Cursor exists: " + cursor.key);
                for (var field in cursor.value) {
                    console.log(field + "=" + cursor.value[field]);
                    console.log("answer : "+answer);
                }
            }
            cursor.continue();
            answer = answer + cursor.value['message'];
            count = count + 1;
            console.log("Count: "+count);
             output.innerHTML = answer;
        };

        /*    console.log("Yoooo Billu : "+maxRevisionObject) 
    
            console.log("Yo Billu : "+JSON.stringify(store.getA))*/

        transaction.oncomplete = function (event) {
            db.close();
        }

    };
};

getbtn.addEventListener('click', function () {
    console.log('Displaying...');
    var db;
    var openRequest = indexedDB.open('test_db', 4);
    openRequest.onupgradeneeded = function (e) {
        db = e.target.result;
        console.log('running onupgradeneeded');
        if (!db.objectStoreNames.contains('store')) {
            var storeOS = db.createObjectStore('store',
                { keyPath: 'id', autoIncrement: true });
        }
    };
    openRequest.onsuccess = function (e) {
        console.log('running onsuccess');
        db = e.target.result;
        var transaction = db.transaction(['store'], 'readwrite');
        var store = transaction.objectStore('store');
        console.log(store.getAll());
    };

});
